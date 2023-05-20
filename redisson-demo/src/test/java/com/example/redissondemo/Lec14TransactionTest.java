package com.example.redissondemo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RTransactionReactive;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Lec14TransactionTest extends BaseTest {

	RBucketReactive<Long> user1Balance;
	RBucketReactive<Long> user2Balance;

	@BeforeAll
	void accountSetup() {
		user1Balance = this.client.getBucket("user:1:balance", LongCodec.INSTANCE);
		user2Balance = this.client.getBucket("user:2:balance", LongCodec.INSTANCE);

		user1Balance.set(100L).subscribe(); // invoking the reactive pipeline which sets value for a key
		user2Balance.set(0L).subscribe();
	}

	@AfterAll
	void accountBalanceStatus() {
		user1Balance.get().subscribe(balance -> System.out.println("User1 Balance is: " + balance));
		user2Balance.get().subscribe(balance -> System.out.println("User2 Balance is: " + balance));
	}

	// user:1:balance 100
	// user:2:balance 0
	@Test
	public void nonTransactionTest() throws InterruptedException {
		this.transfer(user1Balance, user2Balance, 50)
				.thenReturn(0)
				.map(i -> 5 / i) // simulate some error
				.doOnError(System.out::println)
				.subscribe();

		Thread.sleep(1000);
	}

	@Test
	public void transactionTest() throws InterruptedException {
		RTransactionReactive transaction = this.client.createTransaction(TransactionOptions.defaults());
		RBucketReactive<Long> user1Balance = transaction.getBucket("user:1:balance", LongCodec.INSTANCE);
		RBucketReactive<Long> user2Balance = transaction.getBucket("user:2:balance", LongCodec.INSTANCE);

		this.transfer(user1Balance, user2Balance, 50)
				.thenReturn(0)
				//.map(i -> 5 / i) // some error
				.then(transaction.commit()) // if everything successfully completes, then commit transaction
				.doOnError(System.out::println)
				.onErrorResume(ex -> transaction.rollback()) // functionality of transaction
				.subscribe();

		Thread.sleep(1000);
	}

	private Mono<Void> transfer(RBucketReactive<Long> fromAcc, RBucketReactive<Long> toAcc, long amount) {
		return Flux.zip(fromAcc.get(), toAcc.get()) // [balance1, balance2]
				.filter(t -> t.getT1() >= amount) // balance of fromAcc is >= amt to be transferred
				.flatMap(t -> fromAcc.set(t.getT1() - amount).thenReturn(t))
				.flatMap(t -> toAcc.set(t.getT2() + amount))
				.then();
	}

}

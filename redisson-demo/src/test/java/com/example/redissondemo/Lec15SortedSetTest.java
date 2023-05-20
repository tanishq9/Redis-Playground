package com.example.redissondemo;

import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;

public class Lec15SortedSetTest extends BaseTest {

	@Test
	void sortedSet() {
		RScoredSortedSetReactive<String> scoredSortedSet = this.client.getScoredSortedSet("student:score");
		scoredSortedSet.addScore("sam", 10).subscribe(); // add to score if already exists
		// scoredSortedSet.add(12, "sam"); // replace the existing score with this score
		scoredSortedSet.addScore("mike", 15).subscribe();
		scoredSortedSet.addScore("jake", 7).subscribe();

		scoredSortedSet.entryRangeReversed(0, 1)
				.doOnNext(entry -> entry.forEach(stringScoredEntry -> System.out.println(stringScoredEntry.getValue() + " -> " + stringScoredEntry.getScore())))
				.subscribe();
	}
}

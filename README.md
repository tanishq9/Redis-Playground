# Introduction
- Redis is a in-memory database. Doesn't store data on disk and that is why fast performance.
- Altho Redis comes under db category, imagine it as a data structure server.
- Use-cases: Caching, pub/sub, message queue, etc.
- https://github.com/vinsguru/redis-webflux

# Storing simple key values
- key is case-sensitive and can be anything like string, int, boolean, etc and the value would always be stored as string (the key would also be stored as string).
- Redis encourages developers to follow their own standards for key names. Example: user:1:name, user:1:address, etc.
- Using set cli command, put value in double quotes if it exceeds more than a word like:
```
set key "some value"
```

### Accessing all keys
- To get all keys: keys *
- To get all keys matching a pattern: keys user*
- To get all keys in a paginated way: scan 0
- To get all keys matching a pattern in a paginated way: scan 0 match user* count 3

### Expiring keys
- To set expiry for a value in redis: set key value ex 10
- To get ttl (in seconds) for a value in redis: ttl key
- To set expire for a value (once value is created): expire key seconds
- If ttl value is -1 it means there is no expiry and if ttl value -2 it means expired.
- One of the use-case of this expiry feature: User selects a seat and goes to checkout, that seat would be reserved for few minutes, Redis can be used to control drive this validity/expiry functionality.
- When reseting value of a redis key we can specify keepttl keyword so as to retain ttl of an already existing key, for example: set key newValue keepttl otherwise the ttl is made as -1 which means expiry - do not expire.

### Set Option XX/NX
- While updating/resetting value for a key we can also specify xx (if present) or nx (if not present) to control updating value, xx means value will be set if key is present and nx means value will be set if key is not present.

### Exists Command
- exists key_name can be used to check if a key exists in redis db.

### Incr/Decr Command
- incr key_name and decr key_name can be used to increase and decrease integer value corresponding to a key.
- If we do incr and decr for key_name not present then that key for key_name is automatically created with 1 or -1 value depending on whether incr or decr was used.
- If we want to increase or decrease an integer value by a certain number then we can use:
- incrby key_name increment_value
- incrbyfloat float_key_name increment_value for float values.
- Redis cheatsheet:
    - https://cheatography.com/tasjaevan/cheat-sheets/redis/
    - https://redis.io/commands/

# Hash
- If we want to store multiple/group of fields for a key then we can use hash type. We can access/update individual field of an object.
```
hset user:1 name sam age 10 city atlanta

hget user:1 name

hgetall user:1

hkeys user:1

hvals user:1

hexists user:1 name

hdel user:1 age

del user:1
```

# List & Queue
- rpush: to insert element at the end of list
```
rpush users same mike jake
```
- lpush: to insert element always at the beginning of list
- lpop: to remove element from left/beginning of list
- rpop: to remove element from right/end of list
- lrange: to get elements from list
```
lrange users 0 1
```
- llen: to get length of list

Note: Redis list can be used like a queue (FIFO - rpush and lpop) or stack (LIFO - rpush and rpop).

# Redis Set
- Set: An unordered collection of unique items (string)
- Init and add to set: sadd users 1 2 3 4
- Check number of elements in set: scard users
- smembers users: enlist all members of a set
- sismember users 5: to check if an element (5) is member of set
- srem users 5: to remove element from set if it exists.

### Set Intersection & Union
- Union: Either elements of set A or set B
- Intersection: Common elements of set A or set B
- Difference: Elements only in set A BUT NOT in set B

# Redis Sorted Set
- An ordered collection of unique items.
- Similar to Java Sorted Set.
- Use cases: 
    - Priority Queue
    - Frequently visited pages
    - Top rate movie/product

### Sorted Set Opertions
- Add item to SortedSet: zadd products 0 books
- Increment value for an element: zincrby  products 1 books
- Get size of set: zcard products
- Get sorted order of elements in sorted set with scores in maximum to minimum value: zrange products 0 -1 rev withscores
- To get rank of an element in sorted set in ascending order (min to max score): zrank products books
- To get rank of an element in sorted set in descending order: zrevrank products books
- To get score of an element in sorted set: zscore products books
- To pop max element (element with max score) from sorted set: zpopmax products

# Redis Transaction
- Redis Transaction: If we want to execute a bunch of commands together then we should club them into a transaction. 
- Syntax is multi ...commands... exec. 
- We can watch over redis keys before starting a transaction that would ensure if any change is made to those keys (which can cause a conflict) before we exec our transaction then transaction will be aborted.

Note: Redis periodically saves data to disc, not exactly like Postgres, MySQL or other DBs. We can execute bgsave command to save data to disc, the generated .rdb file can be used to restore Redis state after restart.

Note:
- .then() return Mono<Void>: https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#then--
- Always return the publisher i.e. mono or flux so that it can be subscribed by consumer/subscriber: https://github.com/tanishq9/Reactive-Microservices-with-Spring-WebFlux/blob/74ad3757d91620256398dcb0e0eece33b3e85d25/product-service/src/main/java/com/example/productservice/service/ProductService.java#L54
- Redisson doc: https://github.com/redisson/redisson/wiki

# Some Code Examples using Redisson
- Check project for more examples. Sharing few here. 

### Bucket Expiry using Redisson
```
// access bucket expiration time
// either get value of a publisher by subscribing it
bucket.remainTimeToLive().subscribe(System.out::println);
// or test using step verifier
StepVerifier.create(bucket.remainTimeToLive().doOnNext(System.out::println))
  .expectNextCount(1)
  .verifyComplete();
// other way to test it using StepVerifier but not expectNext
Mono<Void> longMono = bucket.remainTimeToLive()
  .doOnNext(System.out::println)
  .then();
StepVerifier.create(longMono)
  .verifyComplete();
```

### Storing Object value in Bucket
- Everything is stored as string in Redis.
- For object value, it would be stored as json string, the @class key inside that json string would be useful when deserialising the json string into that class java object.
```    
"{\"@class\":\"com.example.redissondemo.dto.Student\",\"age\":10,\"city\":\"atlanta\",\"name\":\"marshal\"}"
```
- Usually BinaryCodec is good for app performance.

### Expired Event Listener
- We might need this command to get the expired events from Redis. Check the demo in the following lecture for more details.
```
config set notify-keyspace-events AKE
```
- Reference: https://redis.io/topics/notifications#configuration
- By default notifications are disabled, to enable we can follow above way.

### MapCache
- Using MapCache in Redisson we can set timeout for individual fields of a Map in Redis.

### Local Cached Map
- There are 3 different types of Sync strategy, before that, we know - When someone updates the Redis Map, the update will be sent to Redis.
    - None: The other app server would not be informed by Redis i.e. do not sync.
    - Invalidate: The other app server would have this key removed, only when that invalidated key/field is requested then only it would be fetched from Redis.
    - Update: The other app server would have the updated key/field i.e. updates by someone are reflected immediately incase the sync strategy is Update for the LocalCachedMap.

- ReconnectStrategy
    - Clear: When the connection is back up, now LocalCachedMap will be clearing all local cache and get from Redis.
    - None: Keep serving old data, if there are any future updates then LocalCachedMap will get those.

### Message Queue
- Message Queues are to be used when there is requirement to continuously poll some data from Redis i.e. to listen to live events.

### HyperLogLog
- HyperLogLog is a probabilistic data structure which doesn't store the item but can be used to give the unique count, and therefore can be used incase we want a high level estimation of unique count regarding something like website visits WITHOUT storing the websites, this is a less size consuming solution compared to set or list for the same use-case.

### Pub-Sub
- There is no duplicate processing in Message Queue.
- In Pub-Sub, everyone will receive the information.
    - Misc: https://stackoverflow.com/questions/1050222/what-is-the-difference-between-concurrency-and-parallelism

### Batch
- Batch can be used when we want to send lot of elements at once, this would help save on the network calls.

### Transaction using Redisson
```
user1Balance.set(100L).subscribe(); // invoking the reactive pipeline which sets value for a key
```

### Spring Data Redis vs Redisson
- Spring Data Redis (by default uses Lettuce client/library) has some performance issue compared to Redisson client and has less features, we can directly use Redisson.
- There is no support for Reactive CRUD repository in Spring Data Redis.

### Cache Aside Pattern
Cache Aside Pattern
- Read from cache.
- If not, get it from DB/compute.
- Save it in cache for future use.
- Return the result.

### Cacheable Annotation
- @Cacheable annotation over a method is used to achieve cache aside pattern i.e. it will first check if the key exists in Redis, if yes it returns that value, if not then it will store the result of computation in Redis.
- @Cacheable by default uses all method parameters (for the method it is annotated over) to form the hash key. But if we don't want every method parameter to be part of the key then we can explicitly method in Cacheable annotation parameter.

### CachePut Annotation
- @CachePut - Do the method execution always and update the corresponding cache.

### Using Scheduled Annotation
- Every 10 seconds, external api is called and values are updated in Redis.
- Whenever client calls, the values are fetched from Redis and NOT the external api. Client will not feel any kind of slowness.
- Therefore, we save on external api call for every request (which may be costly) by caching the result for sometime in Redis.
- Similar logic is used in recommendation served to us by Twitter or Netflix.

Note: Mono or Flux is a reactive pipeline, unless it is not consumed or subscribed till then the logic/steps inside the reactive pipeline won't be evaluated.
Note: The caching annotations doesn't work properly with reactive types.

### JMeter Test
- Once everything is ready, you will use CLI mode (Command-line mode previously called Non-GUI mode) to run it for the Load Test. Don't run load test using GUI mode !
- https://blog.e-zest.com/how-to-run-jmeter-in-non-gui-mode/
```
jmeter -n -t product-service.jmx -l results.jtl
```

### WebSocket Session
- We will be using Pub-Sub feature of Redis for message broadcasting.

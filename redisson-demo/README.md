Run docker container for Redis for testing purpose:

```
version: '3'
services:
  redis:
    container_name: redis
    hostname: redis
    image: redis:6.2
    ports:
    - 6379:6379
```
1. docker compose up # This will start the Redis server in the docker container
2. docker exec -it redis bash # Exec in Redis docker container
3. redis-cli # Go inside ~/data dir and start Redis client i.e. to perform operation on Redis server

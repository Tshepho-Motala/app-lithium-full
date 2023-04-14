#service-casino-provider-iForium-perftest-k6
All the parameters for k6 script are passed with using environment variables.

### Steps to run

1. Create RabbiMQ and MySQL docker containers - [How to setup Lithium local development#Step 4 Create Docker Containers](https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/1674936347/How+To+Setup+Lithium+Local+Development#Step-4---Create-Docker-Containers)

2. Set `hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests` property value to `200` in yaml file under library-common/src/main/resources/lithium/common/application.yml

3. Start WireMock container. Navigate to service-casino/service-casino-provider-iforium/src/test/k6/mocks/iforium-service-discovery-mocks/wiremock and run: 

```shell
docker build -t wiremock-hello . && docker run -it --rm -p 8080:8080 wiremock-hello --no-request-journal --container-threads 200 --jetty-acceptor-threads 97 --jetty-accept-queue-size 97
```

4. Run following services with specifying `-Xms100m` VM parameter:
- ServerConfigApplication
- ServerEurekaApplication
- ServerHazelcastApplication
- ServerOath2Application
- ServiceCasinoMockApplication
- ServiceDomainMockApplication
- ServiceUserMockApplication
- ServiceGamesMockApplication

5. Run `ServiceCasinoProviderIforiumApplication` service with specifying `-Xms750m -Xmx750m` VM parameters

6. Execute k6 load test. Navigate to service-casino/service-casino-provider-iforium/src/test/k6 and run:

```shell
# execute to verify that everything goes fine
k6 run --env VUS=1 --env DURATION=5s main.js

# main execution
k6 run --env VUS=100 --env DURATION=10m main.js
```

<br />

### Environment variables description

- VUS **required** - virtual users count
```shell
VUS=2
```

<br />

- DURATION **required** - duration of performance test. This duration does not include INIT_TIMEOUT, so if INIT_TIMEOUT is 10 seconds and DURATION is 10 seconds -> whole script execution will be 20 seconds
```shell
DURATION=10s
```
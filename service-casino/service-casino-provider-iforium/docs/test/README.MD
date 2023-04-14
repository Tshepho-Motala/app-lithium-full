### Steps to setup local environment to execute postman collection

1. Follow [Lithium - How to Setup Lithium Local Development](https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/1674936347/How+To+Setup+Lithium+Local+Development) and run all the services from [Step 6 - Run the Services](https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/1674936347/How+To+Setup+Lithium+Local+Development#Step-6---Run-the-Services)
2. Host LBO-SDK in order to run BackOffice.
```shell
vavdeev@9600k:~/IdeaProjects/Lithium/app-lithium-full/lbo-sdk$ npm install
vavdeev@9600k:~/IdeaProjects/Lithium/app-lithium-full/lbo-sdk$ npm run host
```
3. Create following queues on [Rabbitmq Admin](http://localhost:15672/#/queues) with specifying `Durability=Transient`. That is required to run ServiceCasinoApplication
 - scratchcardwinners
 - winnersfeedlist
4. Run ServiceGamesApplication
5. Run ServiceCasinoApplication
6. Run UiNetworkAdminApplication
7. Setup domain - execute file by file in [./setup-domain-flow](./setup-domain-flow) folder. *TODO: make a script*
8. Register new user and adjust his balance - execute file by file in [./adjust-balance-flow](./adjust-balance-flow). *TODO: make a script*
8. Setup game - execute file [./setup-game](./setup-game). *TODO: make a script*
9. Run ServiceCasinoProviderIforiumApplication
10. Verify that everything is work by calling balance endpoint.
```http request
POST http://localhost:9000/service-casino-provider-iforium/v1.0/balance
Authorization: Basic admin admin
Content-Type: application/json

{
  "PlatformKey": "L100",
  "Sequence": "41b650f5-ff2c-4e55-9af0-8c63c2dd7792",
  "Timestamp": "2020-09-14T13:21:49.9546136Z",
  "OperatorAccountID": "livescore_uk/volodtest1"
}
```
# Supera integration

## Synopsis

The project creates a configurable integration with the Supera casino system using the Supera GameArt Slot Integration v3.8 specification.</br>
This provider integration is intended for use within the Lithium application suite.

##Installation
This module is dependent on the **service-domain**, **service-casino**,**ui_network_admin** and **service-games** modules and their dependencies.

##Configuration
The parameters are defined in the *SuperaModuleInfo.java* file.</br>
The parameter definitions are read from the module using the /modules/providers call in the ModuleInfo interface.</br>
The parameters are then stored in the **service-domain** module where it is read from when a service call is initiated for a provider instance.</br>
Following is a list of the configuration parameters:
* BASE_URL ("baseUrl")
* SALT_KEY ("saltKey")
* API_KEY ("apikey")
* IMAGE_URL ("imageUrl")
* CURRENCY ("currency")
* API_PASSWORD("apiPassword")
* API_USER ("apiUser")

## API Reference
The service calls from Supera needs to be made in the following format: */{providerName}/{apiKey}/{domainName}*</br>
The *providerName* is a unique identification of the provider, in this case service-casino-provider-supera

The calls to game launch and game lists have their implementations based on the **client-service-games** and **client-service-casino** interfaces. 

Demo game: `http://localhost:9783/games/default/demoGame?gameguid=service-casino-provider-supera_23&lang=en`</br>
Start game: `http://localhost:9783/games/default/startGame?gameguid=service-casino-provider-supera_23&lang=en&currency=USD&token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhbnkiXSwiZXhwIjoxNDc0NTI2NjI0LCJhdXRob3JpdGllcyI6WyJST0xFX1NZU1RFTSJdLCJqdGkiOiI3MjU0OGM2Mi1iZTkwLTRkNmEtOWM0Zi0zZmJhYTY3MDA5OTgiLCJjbGllbnRfaWQiOiJzeXN0ZW0ifQ.fkwqRzmB0-JHTu5DZ0ssoXKm44SSgBnNdk97Gh-yn3b4oR0oinEVDNiXCbKv_jeyEkkKypCDYJH7poJeGCWZysI3weg7EbjZWivEmUm3sVU7BatHs5r-jVm1xq-68nqMmFuuGHLvA3WNSKZGjvZcj1hSswnoa-2qcc88D50zIycIPuFXDDraOxe0lWb5DjFviTIV6xRGbgowrs-8yuTgvA15gKB5u1TWTAgV1X2CzkwPWW2NUembHs8HMkfPVLyhalhy9j_ynHKhBeWWLbHEndD6IdGslWTX9y80U-9iKXe35UE761WQkTed5zBqTU4R5c-wOujTuEUN1kMAu-QImg`</br>
The token used is the oauth encrypred authentication token generated, when a user logs in.</br>
Game list: `http://localhost:9783/games/default/listDomainGames`</br>

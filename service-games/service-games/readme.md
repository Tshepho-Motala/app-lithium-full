# Games service
## Synopsis
A microservice that stores lists of games and their metadata per domain.

The service serves as a broker between the front-end and the implemented game providers. Game list data is gathered from all available providers and stored in the Game object and its helpers. This functions as the base for domain specific list configurations. The domain specific game configurations are resolved from the leaf domains up the ancestry domain chain. The end result is a composite representation of a domain game object that is used in frontend applications.

##Installation
This module is dependent on the **service-domain**  module and their dependencies.

##Configuration
The security configuration is located in the *GamesModuleInfo.java* class.

## API Reference
TODO
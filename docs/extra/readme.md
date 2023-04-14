# Common solutions


## How to obtain the external URL for the gateway:

Whenever you send an external provider a URL that they should use to post data back to you, you should use the external gateway URL for the project. This is a custom configured URL in openshift or locally, and therefore is stored as a property in application.yml (ie available via service config).

https://git.playsafesa.com:3000/system-configs/lithium-develop/blob/master/application.yml



In any service:

```@Autowired LithiumConfigurationProperties config;```

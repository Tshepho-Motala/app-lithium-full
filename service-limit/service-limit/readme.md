# Limit service
## Synopsis
A microservice that stores the configurations per player and domain on matters such as betting and deposit limits, self exclusions etc. The service proxies most requests to other storage systems where limits are checked, but services as a base for the business logic of such requests. A bet subsystem will check with service-limit if betting limits are reach, while service-limit will check its configurations on the limits for the player, and in turn make a call to service-accounting to get up to date statistics for the users betting history to ascertain whether the bet should be allowed.

### Installation
TODO

### Configuration
TODO

 Additional domain settings
 
 ##### default-reality-check  
 Set in milliseconds when needed to change the period for Reality Check feature. If it not set default value will be "3600000" (one hour)
 
 #####default-reality-check-periods-in-ms 
 Set in list of milliseconds for BO users. If it not set default value will be "0,1800000,3600000".

 ##### default-deposit-limit-pending-periods-in-hr 
 Set in hours when needed to change the period for Deposit Limits Pending state. If it not set default value will be "24"

##### initiate_wd_on_balance_limit_reached_delay_in_ms
There is a possibility to define value of delay (in milliseconds) before direct withdrawal transaction initiated on balance limit reached. Default value is 5000ms. To change it add propery **initiate_wd_on_balance_limit_reached_delay_in_ms** to related domain settings.

##### pending_balance_limit_update_delay_in_hr
 Set **pending_balance_limit_update_delay_in_hr** property in domain when needed to change the delay for apply Pending Balance Limit to current state. Default value is "168" (7 days)
 
### API Reference
TODO


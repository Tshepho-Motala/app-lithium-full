# Jmeter (Provider Internal) service

### Configuration
TODO
User Defined Variables
Name: BONUS_CODE | Value: FIVES
Name: SLOTAPI_HASH_PASSOWORD | Value: asdASD123(This is example this value might be different).

Thread Group:
Number of threads(user): 50
Ramp-up period(seconds): 1
Loop Count: 10
Check: Same user on each iteration

Counter:
Starting value: 99368113 
Everytime you run the script change starting value increment by 1

CSV Data Set Config
User list export from an environment for example Dev
The CSV data should be on CSV format and only contain userGuid

Bonus Trigger
Server Name or IP: Gateway: gateway.lithium-staging.ls-g.net
HTTP Request: POST, Path: /service-casino-provider-slotapi/bonus/trigger

View Results Tree(Success): Successfully active bonuses will be display on this tab. 
View Results Tree(Errors): Failed active bonuses will be display on this tab. 

How to video below:
https://drive.google.com/file/d/1ZGrddpgXZ25KPNijQ8HrLcngvFJD5x65/view





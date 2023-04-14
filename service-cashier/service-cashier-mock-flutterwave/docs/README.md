# Flutterway USSD Mock
To enable Flutterway USSD MOCK deposit cashier method should be configured in LBO with processor properties:
* * "charges_api_url": {{gateway}}/service-cashier-mock-flutterwave/transactions/deposit
* * "verify_api_url": {{gateway}}/service-cashier-mock-flutterwave/transactions/{id}/verify


#Following scenarios can be simulated so far:
 
| Scenario                          | Amount |
|-----------------------------------|--------|
| Transaction Successful            | any    |
| Bad request                       | 100.00 |
| Insufficient Fund                 | 101.00 |
| Transaction not completed by user | 102.00 |
| Transaction Failed                | 103.00 |

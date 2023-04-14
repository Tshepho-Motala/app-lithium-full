# Inpay Mock
To enable inpay MOCK withdraw cashier method should be configured in LBO with processor properties:
- "withdraw_api_url": {{gateway}}/service-cashier-mock-inpay/transactions/withdraw
- "get_payment_api_url": {{gateway}}/service-cashier-mock-inpay/transactions/verify
- save "INPAY_CERTIFICATE" and replace it with value from "MERCHANT_CERTIFICATE". After using mock please restore original "INPAY_CERTIFICATE". 

#Following scenarios can be simulated so far:
| Scenario                                                          | Amount |
| ----------------------------------------------------------------- |:------:|
| Completed transaction                                             |  any   |
| Rejected transaction                                              |   99   |
| Returned transaction                                              |   98   |
| Pending transaction                                               |   97   |    
| Notification will not be sent                                     |   96   |
| No delay notification                                             |   95   |
| Incorrect final amount                                            |   94   |

#NOTE: 
- If scenario amount contain cents (like 99.10 , 95.01 etc) will use **_webhook V2_** 

#Withdraw flow:
- make BLUEM LITHIUM deposit to get active processor account id.
- call LITHIUM Get Processor Accounts to get list of stored processor accounts.
- call LITHIUM Withdraw with one of "processorAccountId" get from the previous request.
- check transaction in LBO.
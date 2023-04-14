# Smartcash Mock
To enable Smartcash MOCK deposit/withdraw cashier method should be configured in LBO with processor properties:
"api_url":   {{gateway}}/service-cashier-mock-smartcash
"public_key": MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCkq3XbDI1s8Lu7SpUBP+bqOs/MC6PKWz6n/0UkqTiOZqKqaoZClI3BUDTrSIJsrN1Qx7ivBzsaAYfsB0CygSSWay4iyUcnMVEDrNVOJwtWvHxpyWJC5RfKBrweW9b8klFa/CfKRtkK730apy0Kxjg+7fF0tB4O3Ic9Gxuv4pFkbQIDAQAB
"hash_key": a2d4766e94924fa0b5f92bcbadfdff76
"secret_key": 20643a01-37a3-47af-a4d5-880b687a212e
"client_id": 407531ff-10a9-435e-a7b9-dda9f4e13e07


Following self explained scenarios can be simulated so far:
| Scenario                                                                                          | Amount  |
| ------------------------------------------------------------------------------------------------- |:-------:|
| Success flow                                                                                      | any     |
| Transaction Failed                                                                                | 9999    |
| Transaction Ambiguous                                                                             | 9998    |
| Notification will not be sent                                                                     | 9997    |
| Notification will be sent with no delay                                                           | 9996    | 
| Notification will be sent without signature                                                       | 9995    |
| Notification will be sent with failed signature                                                   | 9994    |
| ERROR_RESPONSE("Error response on initial request")                                               | 9993    |
| INVALID_AMOUNT("User enters invalid amount.","DP01100001004")                                     | 9992    |
| INCORRECT_PIN("User enters incorrect pin.")                                                       | 9991    |
| LIMIT_EXCEEDED("User Exceeds withdrawal transaction limit.", ,"DP01100001003"),                   | 9990    |
| INSUFFICIENT_FUNDS("User has insufficient funds to complete the transaction.", "DP01100001007")   | 9989    |
| INVALID_MOBILE_NUMBER("Invalid mobile number","DP01100001012")                                    | 9988    |
| TRANSACTION_ALREADY_EXIST("This transaction already exists", "DP01100001016")                     | 9987    |
| User does not confirm payment                                                                     | 9986    |

Also authentication errors can be simulated specifying invalid client_id/client_secret in the processor properties

The list of NG phone numbers that can be used:
8022220282
8022221041
8022221042
8022221043
8022221044
8022221045
8022221046
8022221047
8022221048
8022221049
8022221050
8022221051

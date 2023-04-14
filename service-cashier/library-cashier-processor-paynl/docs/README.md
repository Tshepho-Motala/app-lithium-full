# Pay.nl Mock
To use Pay.nl MOCK please configure cashier method in LBO with these processor properties:
- payments_api_url - {{gateway}}/service-cashier-mock-paynl/transactions/payout
- get_transaction_url - {{gateway}}/service-cashier-mock-paynl/transactions/verify

#Following scenarios can be simulated so far:

| Payout Scenario                                                   | Amount   |
| ----------------------------------------------------------------- |:--------:|
| Success transaction                                               | any      |
| Failure transaction                                               | 99.99    |
| Canceled transaction                                              | 99.98    |
| Denied transaction                                                | 99.97    |
| Denied v2 transaction                                             | 99.96    |
| Expired transaction                                               | 99.95    |
| No notification transaction                                       | 99.94    |
| No delay between notifications for transaction                    | 99.93    |
| Different start and final transaction amount                      | 99.92    |
| General Error                                                     | 99.91    |
| Multiple Errors                                                   | 99.90    |
| Empty Error's body                                                | 99.89    |
| Empty Response body                                               | 99.88    |

#NOTE: Errors for missing or incorrect mandatory request fields are thrown according Pay.nl documentation.
https://docs.pay.nl/merchants?language=en#transactions-statuses
https://developer.pay.nl/docs/error-codes
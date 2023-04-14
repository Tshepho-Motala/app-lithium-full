# Hexopay Mock
To enable Hexopay MOCK deposit/withdraw cashier method should be configured in LBO with processor properties:
"payments_page_url": {{gateway}}/service-cashier-mock-hexopay
"gateway_api_url":   {{gateway}}/service-cashier-mock-hexopay
"public_key": MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvY+MnVxHKkQqjB8QzoKQFIQ+zYXePSFHDmcZoNxZ2A7MxptmaXBrXa0gvVxCfk7E5Iq5lTEeWWnD8ZpkXNSFG3kI1vg1btVOn2/HzU9BLOkJqqFfYF63qKhp8M8uqtYCT9EvXHqxG70dVZ/55sYGFCUWXT7SC1by+ZEs+Z13q9Sffc1KvW9vEu3TLvCwKTz6wY0i9ohQzdQtLlixTdWhzTH5oxe5ADFVvxhZn3nUzcgkmSJO9saq3axaxbru4S+hpuWz22Ui+kmHLX89m5fHZSaaiueiO5KMycClU32JEq/SKZ5KiADtoeuNgU+jqk0AZfwBOfhQknahR9YD9GyxfwIDAQAB

Following self explained scenarios can be simulated so far:
| Scenario                                                          | Amount    | CVV   | Address Line 1 |
| ----------------------------------------------------------------- |:---------:|:-----:|:--------------:|
| Success flow                                                      | any       |       |                |
| Failed flow                                                       | 99.99     |       |                |
| Notification will not be sent                                     | 99.98     |       |                |
| Notification will be sent with no delay                           | 99.97     |       |                |    
| Notification will be sent without signature                       | 99.96     |       |                |
| Notification will be sent with failed signature                   | 99.95     |       |                |
| Different initial and final transaction amount                    | 99.94     |       |                |
| Error response on initial request                                 | 99.93     |       |                |
| AVS B: Street address matches, but postal code is not verified    |           |       |  Test B        |
| AVS N: Street address and postal code do not match                |           |       |  Test N        |
| AVS M: Street address and postal code match                       |           |       |  Test M        |
| AVS P: Postal code matches, but street address is not verified    |           |       |  Test P        |
| AVS U: Address information unavailable                            |           |       |  Test U        |
| AVS E: A system error prevented any verification                  |           |       |  Test E        |
| AVS 0: AVS result is unknown                                      |           |       |  Test 0        |
| CVC M: Card verification code matched                             |           | 111   |                |
| CVC N: Card verification code not matched                         |           | 222   |                |
| CVC U: Card verification is not supported by the issuing bank     |           | 333   |                |
| CVC E: A system error prevented any CVC verification              |           | 444   |                |
| CVC 0: CVC result is unknown                                      |           | 555   |                |
| Email is black listed                                             | 99.92     |       |                |
| IP is black listed                                                | 99.91     |       |                |
| Card is black listed                                              | 99.90     |       |                |
| Duplicate card check failed                                       | 99.89     |       |                |

Scenario can be selected using dropdown on card details page or by setting corresponding amount/cvv/address 

Use "Hexopay API" postman collection to simulate hexopay payments flow using mock
1. Initial deposit flow:
    - Call LITIUM Web Deposit API
    - open url from "iframeUrl" response parameter
    - submit credit card details
    - user will be redirected to the "return_url" from LITIUM Web Deposit API with corespoding status in query string
    - check transaction in LBO 
    
2. Quick deposit flow:
    - call LITIUM Get Processor Accounts to get processor account id that was stored in initial deposit flow 
    - call LITIUM Web Quick Deposit with one of "processorAccountId" get from the previous request
    - if 3d secure was selected for this card and "use_3DSecure" processor property set to "true"  
        - open 3d secure verification url from "iframeUrl" response field
        - user will be redirected to the "return_url" from LITIUM Web Deposit API with corespoding status in query string    
    - check transaction in LBO

3. Withdraw flow:
    - call LITIUM Get Processor Accounts to get list of stored processor accouts 
    - call LITIUM Withdraw with one of "processorAccountId" get from the previous request
    - check transaction in LBO


Hexopay Backoffice:
url: https://sonicbackoffice.hexopay.com/merchants/sign_in
user:rahul.das@livescore.com

Documentation:
https://techdocuments.hexopay.com/en/introduction

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




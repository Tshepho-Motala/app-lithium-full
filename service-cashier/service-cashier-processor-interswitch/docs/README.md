#Interswitch API

##Web deposit 
#https://developer.interswitchgroup.com/docs/pay-with-quickteller/#basic-implementation

## Withdraw
#https://sandbox.interswitchng.com/docbase/docs/quickteller-sva/funds-transfer/
https://sandbox.interswitchng.com/api/v2/quickteller/payments/transfers

## Verify transaction
#https://sandbox.interswitchng.com/docbase/docs/quickteller-sva/query-transaction/
https://sandbox.interswitchng.com/api/v2/quickteller/transactions?requestreference={referenceNumber}

##Lithium Interswitch withdraw endpoint
/service-cashier/frontend/withdraw/v2?methodCode=interswitch

{
  "inputFieldGroups": {
     "1": { "fields": { "amount": { "value": "123.00"} } },
    "2": { "fields": {
      "bank_code": { "value": "044" },
      "account_number": { "value": "0000000000" }
    }}
  },
  "state": "VALIDATEINPUT",
  "stage": "1"
}

##Lithium Interswitch banks list endpoint
/service-cashier-processor-interswitch/public/banks



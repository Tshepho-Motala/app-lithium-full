# KYC service


### User verification threshold
The user verification threshold is a setting that will be used to as part of the evaluation process on whether the system is allowed to perform a remote request to verify a player’s KYC
Your can define it in domain settings via `user-kyc-total-attempt-threshold` property

### Kyc method ordering
To define order in `/service-kyc/frontend/kyc/method-list` response, you can add property `kyc-method-ordering` in domain settings.
Example: `kyc-method-ordering : METHOD_BVN,METHOD_BANK_ACCOUNT,METHOD_PASSPORT,METHOD_DRIVERS_LICENSE,METHOD_NATIONAL_ID,METHOD_VOTER_ID`
The response is stored in the cache

### Kyc cache reset
GET `/service-kyc/frontend/kyc/method-list-reset` - Clear stored methods list from cache.
Call clear cache : LBO Admin UI `Domain -> Provides -> Kyc -> <Provider name> -> Modify -> Submit`
No needs any changes in provider properties!
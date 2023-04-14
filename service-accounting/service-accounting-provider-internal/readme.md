# Accounting (Provider Internal) service
## Synopsis
A microservice that stores financial transactions per user, in a chart of accounts using double-book entry best practice. All transactions are ACID, and statistics on transaction types and metadata are calculated within the same transaction thus ensuring financial consistency. If a transaction returns successfully, the sum total of transactions of similar types for the same user over all time, per day, per week, per month, per year, are guaranteed to also be consistent.

### Installation
TODO

### Configuration
TODO

### API Reference
TODO


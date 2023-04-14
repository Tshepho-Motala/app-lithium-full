# Cashier service
## Synopsis
A microservice that stores the payment processing configurations per domain, and marshals frontend requests to provider microservices for each payment processing method. service-cashier-provider-* services have an API so that they may perform callbacks into cashier and they apply configuration requirements on startup using rabbit MQ towards cashier so that processor availability is automatic as long as services are running together in the same cluster.

### Installation
TODO

### Configuration
TODO

### API Reference
TODO


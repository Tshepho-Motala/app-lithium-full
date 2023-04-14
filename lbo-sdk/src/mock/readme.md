# mock

All dependency injected components are derived from interfaces and therefore have no actual implementation on their own. While this is necessary for the integration into LBO - as LBO will be providing the intended functionality - it also severely limits the functionality and testability of the SDK.  

Mock functionality is created to mitigate this issue. The intention is to create base classes that derive from an interface with minimal code to prove a positive and negative flow.

---


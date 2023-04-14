# core

This directory contains all of the core functionality needed for the SDK to function correctly, as well as any shared components or modules needed for any plugins.

Think of core as the parent to the SDK as well as all plugins derived from the SDK. So anything in core should either relate directly to the SDK, or be a shared component to be distributed between plugins.

---

### Folder Breakdown  
  
`api/`  
[SDK] [Plugin]  
This folder contains all connections to external API endpoints including calls to LBO or other.

`components/`  
[SDK]  
This folder contains all the core components for the SDK, including layouts and dynamic plugin hosts.

`directive/`  
[SDK] [Plugin]  
This folder contains all the shared directives to make use of within the SDK and any plugins created.

`interface/`  
[SDK] [Plugin]  
This folder contains all the interfaces for internal and external services, providers, and injectors.

`modules/`  
[SDK]  
This folder contains all the SDK modules for proper rendering and component theming.

`router/`  
[SDK]  
This folder contains the routes for the SDK to capture the browser's URL and render the appropriate plugin.

`store/`  
[SDK]  
This folder contains the state management systems for the SDK.

`views/`  
[SDK]  
This folder contains all the UI views for the SDK.

`PluginRegistry.ts`  
[Plugin]  
This file contains all the plugin registrations and render/route definitions to inject into the SDK.
# DIPS - Dynamic Import Plugin Script


### Installation & Startup
- Open a Terminal instance to the `root directory` of the project
- Execute `npm i` in the Terminal to install all required dependancies
- Execute `npm start` to start the development server
- Open http://localhost:8080 in your browser to navigate to the website

## Project Structure
The main entrypoint to executing the website is in `/src/main.ts`, and the initial webpage scaffold requested is in `/src/App.vue`. This two files should only house setup and scaffold code, and should be kept as clean and simple as possbile.

For the purposes of this demo, there are only two folders in the `root directory` that needs attention:
- ./public  
  All _static_ website items (Such as large files, images, videos) put in this folder will be copied to the output directory exactly as-is, and will be served at the root of the website (Using the folder heirarchy)

- ./src  
  All source code and bundled assets

### SRC folder
- assets  
  All assets that need to be _bundled_ with the website are included in this folder. Webpack will automatically convert all small images to their Base64 Strings and replace their file reference instance where necessary. The files in this folder ___will not___ be served as static files, and need to be referenced by an `import()` statement in your code.

- common  
  All shared functions, models, and interfaces are included in the common folder.

- components  
  All shared DOM elements are included in the components folder. The ideal component should be standalone and require all information from the __StoreProxy__.

- plugins  
  All API connections, external integrations, and Vue plugins are stored in the plugins folder.

- router  
  All frontend Routes and RouteGuards are stored in the router folder.

- store  
  All dynamic data required to run the website and generate dynamic components are stored in the store folder. Reference to the store will be made through __StoreProxy__ for type-safety.

- views  
  All Views and Pages ares stored in the views folder;  
  
  - Views are the templated wrappers for pages, wrapping overall style and security inherited by pages  
  - Pages house the main components and features to drive core functionality of the website

---

## Dynamic Menu Generation  
The application _state_ is derived from a singleton known as a _store_. This is a powerful component making use of Vue's native two-way data binding mechanisms to abstract data binding functionality to modular __Store Modules__.  
Data can be created, edited, or removed on-the-fly, which will invoke instant dynamic module rebuilds to alter visual and functional features of the website.

> Using Vue Comoponents we can create a losely-coupled ecosystem where each component can act in a standalone manner. This allows you to easily and efficiently reuse functionality and features without relying on static reference and implementation.

In this project there is _one_ `SideMenuList` component that listens for menu items from the `SideMenuModule` in the `StoreProxy`. Any items that get added to the module will be dynamically generated as interactable menu items.

The `AdminView` and the `PrivateView` each house a single reference to the `SideMenuList` without passing it any data or requiring a parent reference.

When requested to `mount` on the page, the `SideMenuList` first asks the `SideMenuModule` to fetch any list items from the API*. This is a fire-and-forget function as the `SideMenuModule` requires zero-or-more items, which allows background loading and 'anytime' data generation.  
Once the API returns the results, the `SideMenuModule` updates its list of items which then allows the `SideMenuList` to render out the updates.

  > \* API - For this project the API fetches mock data depending on the Role the user logged in with. This has been written in a way to easily replace the mock data with an actual API call, as all functions are async.  
    (The API URL can be passed in as a `environment variable` or set in `/src/common/config.ts`)


    #####

    https://markus.oberlehner.net/blog/distributed-vue-applications-loading-components-via-http/
# LBO Website SDK

This SDK is used to create modular, standalone components in order to generate valid UMD modules for dynamic website injection.

---

# Quick Info

**Setup and Development Server**  
(Development server needed to load plugins on the LBO website)
- Open the terminal and `cd ` into `lbo-sdk`
- Execute `npm i` in the terminal, this will install all packages and generate the initial plugins
- Execute `npm run host` to start the development server for running the website on localhost
- Execute `npm run deploy [plugin-name]` to rebuild only your plugin, then refresh the LBO website to see all changes. To rebuild all plugins, simply omit the `[plugin-name]`
  - HINT: Run this in a second terminal to avoid needing to restart the development server

**Register a plugin to be deployed**
- Open `/ci/deploy.js`
- Look for a property under the following comment: `// PLUGIN REGISTRY`
- Add a new line to the `plugins` array in the following way:  
  `newPlugin('[src/location]', 'plugin-[name]')`

**Register a plugin for SDK access**
- Open `/src/core/PluginRegistry.ts`
- Import your plugin in the function named `registerMockPlugins()`

**Mount Plugin on LBO website**  
(This only needs to be done once per plugin)
- Navigate to an HTML page where you want to load the plugin
- Create a new `div` where the plugin will be mounted, and set its ID to a `[unique-dom-id]`
- Navigate to the JS file that controls the HTML page
- In the controller function, add the following code  
  `window.VuePluginRegistry.loadByPage("[page-name]")`
  - This will load any plugins that have a matching `[page-name]` in its descriptor
- Navigate to `ui-network-admin/src/main/resources/static/scripts/vue-plugin-registry.js`
- Add a new plugin descriptor to the `vuePlugins` array with the following format:  
  `new VuePlugin("plugin-[name]", "#[unique-dom-id]", "[page-name]")`
  - `plugin-[name]` is the name of the plugin needing to be mounted
  - `#[unique-dom-id]` is the selector for the DOM component to mount this plugin
  - `[page-name]` the name of the page, when called, to load the plugin


** When installing a new library, please use the `--save` or `--save-dev` command where necessary.

---
## Dependancy Injection

All plugin logic should be injected via the host, and interfaces are used to describe the properties and function within the injection. This is to allow an agnostic host, where we can inject the functionality through the Angular website, mock it out in the SDK, or implement 'real' functionality within the SDK or Website.

## Plugin based architecture

Plugins needs to be developed in a 'standalone' manner, where each plugin can run independantly of any other, and should work correctly under any host environment. To accurately simulate that envionment the SDK implements a `PluginHost` which all plugins need to be registered to in order to see them in the SDK website.

---

## HOWTO

### Run local environment
The SDK is completley decoupled from the original LBO solution. This means that there is no direct reference to any of the generated plugins from the original LBO website, and therefore needs a 'plugin host' in order for it to function correctly on development environments.  
*Note: You will first need to register your plugin for it to compile during the deploy process. See the FAQ.*

Looking in the `package.json`, you will see the following noteworthy scripts:

**Script: `deploy [key [key2] [key3]]`**  
This is the entrypoint to generating valid JS plugins from Vue templates. This process takes long, and will output all the assets in the `/assets` folder at the root of this project.

Passing a plugin key(s) will only deploy the keys set, and skip all other plugins. This can be useful if you want to only run one (or more) plugins on your local environment without waiting for them to all compile.

You can rerun the deploy script even after the initial deploy, without clearing your assets folder, and everything will still function correctly.

It is recommented that you run a full deploy on your first use.

**Script: `host`**  
Executing this script will start the `PluginHost` server, which will host the assets in the `/assets/` folder. All changes made in this folder are automatically picked up, and you do not need to restart the service after every change.

**Script: `di`**  
This is a convenience script to execute the `deploy` script, and subsequently the `host` scrip when deploy is complete.

### Logging
Logging is provided as a service and should be used in all plugins instead of `console.log`. This is to give us a configurable way of allowing console logs, as well as the ability to extend to othet logging services if need be.

To inject the logging service into a plugin file, simply add the following injector:
```typescript
@Inject('logService') readonly logService!: LogServiceInterface
```

This will allow you to use `logService.*` in place of `console.*log`.

---

## FAQ

Q: Where do I place new Plugins?  
A: `./src/plugin/`

Q: Where do I register my plugin?  
A: `./src/core/pluginRegistry.ts`

Q: What is the `core` folder for?  
A: To house functionality and components required for the SDK to function correctly

Q: Where do I setup mock data and mock functions?  
A: `./src/mock/RootScopeMock.ts`

Q: Are there naming conventions for DOM ID's?  
A: `[page]-[control]-[reference]` - eg: `id="login-button-signin`, `playerprofile-label-fullname`, etc  

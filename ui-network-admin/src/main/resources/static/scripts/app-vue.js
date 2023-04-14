const appVue = {

    // The location of the plugins, either the base assets folder (for deploy), or the local service host (for local dev)
    get pluginLocation() {
        const localDomains = ["localhost", "127.0.0.1", ''] // A list of all the domains that qualify for a local environment
        if(localDomains.includes(window.location.hostname)) {
            return 'http://localhost:62300/' // This is the URL of the local plugin host
        }
        return '/assets/'
    },

    // The final URL to fetch the plugin data
    get pluginUrl() {
        return this.pluginLocation + this.pluginFile
    },

    // The final URL to fetch the route data
    get routeUrl() {
        return this.pluginLocation + this.routeFile
    },

    // A flat map of all the paths (considering the angularPath in meta as well)
    get routePaths() {
        if(!this.hasFetchedRoute) {
            return []
        }
        return this.fetchedRouteData.map((x) => {
            return {
                key: x.meta ? x.meta.key || x.name : x.name,
                path: x.meta ? x.meta.angularPath || x.path : x.path,
                mount: x.meta ? x.meta.mount : ''
            }
        })
    },

    // The name of the JSON file generated for plugins
    pluginFile: 'vue-plugins.json',

    // The name of the JSON file generated for routes
    routeFile: 'vue-routes.json',

    // Helper property to know if we've already fetched the plugins
    hasFetchedPlugin: false,

    // Helper property to know if we've already fetched the route
    hasFetchedRoute: false,

    // Helper property to know if we're in the process of fetching data
    isFetchingData: false,

    // Ensure we don't double load
    loading: false,

    // Local storage for our plugin data
    fetchedPluginData: null,

    // Local storage for our router data
    fetchedRouteData: null
}

/**
 * Create UMD module initiator, ready for injection
 * @param url URL relative location of the script to initiate
 * @returns {Promise<unknown>|*} Returns the initiator after loading
 */
function createInitiator(url) {
    const split = url
        .split("/")
        .reverse()[0]
        .match(/^(.*?)\.umd/);
    if (!split) {
        throw new Error("Can't load plugin: " + url);
    }
    const name = split[1];

    if (window[name]) {
        return window[name];
    }

    const initiator = new Promise((resolve, reject) => {
        const script = document.createElement("script");
        script.async = true;
        script.addEventListener("load", () => {
            resolve(window[name]);
        });
        script.addEventListener("error", () => {
            reject(new Error(`Error loading ${url}`));
        });
        script.src = url;
        document.head.appendChild(script);
    });

    window[name] = initiator;

    return initiator;
}

/**
 * Import the stylesheet associated to the plugin
 * @param url URL location of the stylesheet
 */
function linkStyle(url) {
    const location = url.replace('umd.min.js', 'css')
    const style = document.createElement("link")
    style.rel = "stylesheet"
    style.type = "text/css"
    style.href = location

    return document.head.appendChild(style)
}

/**
 * Helper function to load external Vue plugin scripts
 * @param name Name of plugin
 * @param mount ID of the DOM element to mount this component
 * @returns {Promise<void>}
 */
async function loadPlugin({key, mount}) {
    if(appVue.loading) {
        await waitForLoadingToComplete()
    }

    if(!appVue.hasFetchedPlugin) {
        await fetchPluginList()
    }

    const chunk = getPluginChunk(key)

    const url = appVue.pluginLocation + key + "." + chunk + ".umd.min.js"
    const script = await createInitiator(url)
    //const style = await linkStyle(url)
    window.VueMount(script, mount)
    return null
}

/**
 * Load external Vue scripts
 * @param scriptLocation Location of the script
 * @param mountId ID of the DOM element to mount this component
 * @returns {Promise<void>}
 */
async function loadAndMount(scriptLocation, mountId) {
    if(appVue.loading) {
        await waitForLoadingToComplete()
    }

    if(!appVue.hasFetchedPlugin) {
        await fetchPluginList()
    }
    if(!appVue.hasFetchedRoute) {
        await fetchRouteList()
    }
    const initiator = await createInitiator(scriptLocation)
    linkStyle(url)
    return window.VueMount(initiator, mountId)
}

/**
 * Fetches the plugin list from a remote URL
 * @returns {Promise<object>} Object of plugins with chunk hash
 */
async function fetchPluginList () {
    if(appVue.loading) {
        await waitForLoadingToComplete()
    }
    appVue.loading = true

    return new Promise((res, rej) => {
        fetch(appVue.pluginUrl).then(data => data.json()).then(json => {
            appVue.hasFetchedPlugin = true
            appVue.loading = false
            appVue.fetchedPluginData = json
            res()
        }).catch(rej)
    })
}

/**
 * Fetches the plugin list from a remote URL
 * @returns {Promise<object>} Object of plugins with chunk hash
 */
async function fetchRouteList () {
    if(appVue.loading) {
        await waitForLoadingToComplete()
    }
    appVue.loading = true

    return new Promise((res, rej) => {
        fetch(appVue.routeUrl).then(data => data.json()).then(json => {
            appVue.hasFetchedRoute = true
            appVue.loading = false
            appVue.fetchedRouteData = json
            res()
        }).catch(rej)
    })
}

/**
 * Gets the latest chunk hash for a plugin
 * @param key Key of the plugin
 * @returns {null|string} Chunk hash, if available
 */
function getPluginChunk(key) {
    if (!key) {
        return null
    }

    const val = appVue.fetchedPluginData[key] // Value of the key
    const isValArray = Array.isArray(val) // Check to see if we're dealing with an array (legacy) or object (new)

    let chunks = []

    if(isValArray) {
        chunks = val
    } else {
        chunks = val.chunks
    }


        if(!chunks || chunks.length === 0) {
            return null
        }
        return chunks[chunks.length - 1]

}

function waitForLoadingToComplete() {
    return new Promise((res) => {
        waitForLoading(res)
    })
}

function waitForLoading(next) {
    if(appVue.loading) {
        setTimeout(() => {
            waitForLoading(next)
        }, 200)
    } else {
        next()
    }
}

function waitForElementToBeVisible(elementId) {
    return new Promise((res) => {
        waitForElement(elementId.replace('#', ''), res)
    })
}

function waitForElement(elementId, next) {
    const el = document.getElementById(elementId)
    if(el === null) {
        setTimeout(() => {
            waitForElement(elementId, next)
        }, 200)
    } else {
        next()
    }
}

// Add these functions to the global scope for now
window.VueFetchPluginList = fetchPluginList
window.VueFetchRouteList = fetchRouteList
window.VueLoadPlugin = loadPlugin
window.VueLoad = loadAndMount
window.VueWaitForElement = waitForElementToBeVisible
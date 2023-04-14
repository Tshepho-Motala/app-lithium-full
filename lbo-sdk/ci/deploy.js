const { exec } = require('child_process')
const { cpus } = require('os')
const cluster = require('cluster')
const fs = require('fs')
const path = require('path')
const yargs = require('yargs')
const { hideBin } = require('yargs/helpers')

// Location of the plugin files
const destPluginFile = 'vue-plugins.json'
const destRouteFile = 'vue-routes.json'
const destDir = 'assets/'

const newPlugin = function (location, key) {
  return { location, key }
}

// PLUGIN REGISTRY
// DEPRECATED - Use Routes instead
const plugins = [
  newPlugin('src/plugin/documents/ManageDocumentTypesTab.vue', 'plugin-ManageDocumentTypesTab'),
  newPlugin('src/plugin/cashier/LastXCashierTransactions.vue', 'plugin-LastXCashierTransactions'),
  newPlugin('src/plugin/documents/DocumentsTab.vue', 'plugin-DocumentsTab'),
  newPlugin('src/plugin/documents/DocumentUploadQuickAction.vue', 'plugin-DocumentUploadQuickAction'),
  newPlugin('src/plugin/cashier/BankAccountLookup.vue', 'plugin-BankAccountLookup'),
  newPlugin('src/plugin/cashier/BankAccountLookupTable.vue', 'plugin-BankAccountLookupTable'),
  newPlugin('src/plugin/components/PageHeader.vue', 'plugin-PageHeader'),
  newPlugin('src/plugin/components/multi-level-menu/MultiLevelMenu.vue', 'plugin-QuickActions'),
  newPlugin('src/plugin/cashier/config/CashierConfigPage.vue', 'plugin-CashierConfig'),

  // Generic dialogs - never delete this!
  newPlugin('src/plugin/components/dialog/GenericDialog.vue', 'util-dialogGeneric'),
  newPlugin('src/plugin/components/dialog/ConfirmDialog.vue', 'util-dialogConfirm'),

  newPlugin('src/plugin/cms/images/Images.vue', 'plugin-CasinoCmsImages'),
  newPlugin('src/plugin/cms/webassets/Assets.vue', 'plugin-UploadCmsAssets'),

  newPlugin('src/plugin/csv-export/ButtonExport.vue', 'plugin-CsvExport'),
  // Cashier
  newPlugin('src/plugin/cashier/autowithdrawals/AutoWithdrawalDetailPage.vue', 'plugin-AutoWithdrawalDetailPage'),
  newPlugin('src/plugin/cashier/autowithdrawals/AutoWithdrawalPage.vue', 'plugin-AutoWithdrawalPage'),

  // CASHIER Transaction
  newPlugin('src/plugin/cashier/transactions/TransactionsDetailPage.vue', 'plugin-TransactionsDetailPage'),
  newPlugin('src/plugin/cashier/transactions/TransactionsListPage.vue', 'plugin-TransactionsListPage'),
  newPlugin('src/plugin/cashier/withdrawal-bulk/WithdrawalBulk.vue', 'plugin-WithdrawalBulk'),
  newPlugin('src/plugin/cashier/transactions/balance-adjustments-transaction/BalanceAdjustmentsTransaction.vue', 'plugin-BalanceAdjustmentsTransaction'),

  //Players
  newPlugin('src/plugin/player/player-search/PlayerSearchTopBar.vue', 'plugin-PlayerSearchTopBar'),
  newPlugin('src/plugin/player/player-search/PlayerLinksSearchTopBar.vue', 'plugin-PlayerLinksSearchTopBar'),
  newPlugin('src/plugin/domain-select/DomainSelect.vue', 'plugin-DomainSelect'),
  newPlugin('src/plugin/player/player-search/PlayerKYCVendorModal.vue', 'plugin-PlayerKYCVendorModal'),
  newPlugin('src/plugin/mail/MailSendDialog.vue', 'plugin-MailSendDialog'),

  //Bonuses
  newPlugin('src/plugin/bonuses/BonusSearchTopBar.vue', 'plugin-BonusSearchTopBar'),

  //Games
  newPlugin('src/plugin/games/GameSearchTopBar.vue', 'plugin-GameSearchTopBar'),

  //Promotion Scheduling
  newPlugin('src/plugin/promotions/reward/RewardPlayerHistory.vue', 'plugin-RewardPlayerHistory'),

  //Rewards
  newPlugin('src/plugin/rewards/Rewards.vue', 'plugin-Rewards'),

  // BalanceMovement
  newPlugin('src/plugin/cashier/balance-movement/BalanceMovement.vue', 'plugin-BalanceMovement'),

  //PlayerProtection
  newPlugin('src/plugin/player-protection/PlayerProtection.vue', 'plugin-PlayerProtection')
]

/// ** Build Logic **

const compile = function (plugin) {
  return new Promise(async (res, rej) => {
    try {
      await checkIfFileExists(plugin.location)
    } catch (e) {
      console.warn('[VUE] File does not exist: ' + plugin.location)
      res()
      return
    }

    console.log('[VUE] Executing Vue CLI build command for ', plugin.key)

    const session = exec(
      `npx vue-cli-service build --mode development --target lib --formats umd-min --no-clean --dest ${destDir} --name "${plugin.key}.[chunkhash]" ${plugin.location}`,
      (error, stdout, stderr) => {
        console.log('[VUE] CLI build finished - the next output is from the build process')

        if (error) {
          console.log('[VUE] ------------------------------------------------------')
          // console.log(stdout) // We dont need this, its spammy
          console.error(stderr) // We only need this if there's an error
          console.log('[VUE] ------------------------------------------------------')

          console.warn('[VUE] CLI build has failed with an error (Propagating to top catch)')
          rej(error)
        } else {
          console.info('[VUE] CLI build passed, fetching chunk.')
          fetchChunkName(plugin.key).then(res).catch(rej)
        }
      }
    )
  })
}

const fetchChunkName = function (outputName) {
  return new Promise((res, rej) => {
    console.log('[VUE] Fetching output directory')
    fs.readdir(destDir, async (err, files) => {
      if (err) {
        console.error('[VUE] Output directory could not be read!')
        console.error(err)
        rej(err)
        return
      } else {
        console.log('[VUE] Fetching files in output directory')
        let fetchedFiles = files.filter((file) => file.endsWith('js') && file.includes(outputName))
        if (!fetchedFiles || fetchedFiles.length === 0) {
          rej('File belonging to "' + outputName + '" does not exist.')
          return
        }

        let fetchedFile = fetchedFiles[0]

        if (fetchedFiles.length > 1) {
          // If we are here, only fetch the latest files
          let lastFileMs = 0
          // let lastFilePath = 0
          for (const file of fetchedFiles) {
            console.log('[VUE] File exists, ensure we have write access')
            const createMs = await fetchFileCreateTime(destDir + file)
            if (createMs > lastFileMs) {
              lastFileMs = createMs
              fetchedFile = file
            }
          }
        }

        fetchedFiles = fetchedFile.replace(outputName + '.', '') // Removing the original name removes any issues with special characters before splitting
        const fileParts = fetchedFiles.split('.')
        const chunk = fileParts[0] // Now that we know there is no name, we can always just get the first elements

        console.info('[VUE] Chunk completed')
        res(chunk)
      }
    })
  })
}

const fetchFileCreateTime = function (file) {
  return new Promise((res, rej) => {
    fs.stat(file, (err, stats) => {
      if (err) {
        rej(err)
      } else {
        res(stats.birthtimeMs)
      }
    })
  })
}

const checkIfFileExists = function (file) {
  return new Promise((res, rej) => {
    fs.stat(file, (err, stats) => {
      if (err) {
        rej(err)
      } else {
        res()
      }
    })
  })
}

const checkIfDirectoryExists = async function (directory) {
  await fs.promises.access(directory)
}

const fetchRouteFile = function (whitelist) {
  console.log('Fetching coded route file')
  return new Promise((res, rej) => {
    fs.readFile('src/router/routes.ts', async (err, data) => {
      if (err) {
        console.error('There was an error fetching the coded routes file, see error')
        console.debug(err)
        rej(err)
      } else {
        console.log('Existing coded route file fetched')
        if (!data || data.length === 0) {
          console.log('There is no data in the routes file')
          res([])
        } else {
          console.log('Using the data in the routes file')
          // Transform from a valid module export to a valid JS object
          // Transform imports to normal strings
          const formattedData = data
            .toString()
            .replace('export default ', '')
            .replace(/import/g, '')
          // Evaluate this into an in-memory object
          let evaled = eval(formattedData)

          if (whitelist && whitelist.length > 0) {
            evaled = evaled.filter((x) => whitelist.some((y) => x.name === y))
          }

          res(evaled)
        }
      }
    })
  })
}

const getPluginsUsingWhitelist = function (whitelist) {
  if (whitelist && whitelist.length > 0) {
    return plugins.filter((x) => whitelist.some((y) => x.key === y))
  }
  return plugins
}

const fetchPluginFile = function () {
  return new Promise((res, rej) => {
    fs.readFile(destDir + '/' + destPluginFile, (err, data) => {
      if (err) {
        res({})
      } else {
        if (!data || data.length === 0) {
          res({})
        } else {
          res(JSON.parse(data))
        }
      }
    })
  })
}

const setPluginFile = function (jsonData) {
  return new Promise((res) => {
    fs.writeFile(destDir + '/' + destPluginFile, JSON.stringify(jsonData), (error) => {
      if (error) {
        console.error('[VUE] Could not write plugin file!')
        console.error(error.message)
        rej(error)
        return
      }
      console.info('[VUE] Updated plugin file')
      res()
    })
  })
}

const setRouteFile = function (routeData) {
  console.log('Writing output Routes file')
  return new Promise((res, rej) => {
    fs.writeFile(destDir + '/' + destRouteFile, JSON.stringify(routeData), (err) => {
      if (err) {
        console.error('There was an error writing the output Route file, see error')
        console.debug(err)
        rej(err)
        return
      }
      console.log('Output Route File saved')
      res()
    })
  })
}

const updatePluginFile = async function (plugin, chunk) {
  console.log('[VUE] Updating plugin file')
  const jsonData = await fetchPluginFile()
  console.info('[VUE] Plugin file fetched')
  if (!jsonData[plugin.key]) {
    jsonData[plugin.key] = {
      route: {},
      store: {},
      chunks: []
    }
  }
  if (jsonData[plugin.key].chunks.indexOf(chunk) === -1) {
    jsonData[plugin.key].chunks = [chunk]
  }

  await setPluginFile(jsonData)
}

const flattenRouteArray = async function (routes, flattened) {
  for (const route of routes) {
    const comp = await route.component()
    flattened.push(newPlugin(comp.replace('@', 'src'), route.name))
    if (route.children) {
      await flattenRouteArray(route.children, flattened)
    }
  }
}

const deleteDestinationReferenceFiles = async function () {
  try {
    await checkIfDirectoryExists(destDir)
  } catch (e) {
    // Don't do anything if it doesnt exist
    return
  }
  console.log('[VUE] Removing Destination Reference Files')
  return new Promise((res, rej) => {
    fs.readdir(destDir, (err, files) => {
      if (err) {
        rej(err)
        return
      }

      const limit = files.length
      let index = 0

      const toDelete = ['vue-plugins.json', 'vue-routes.json']

      for (const file of files) {
        if (!toDelete.some((x) => file === x)) {
          continue
        }

        fs.unlink(path.join(destDir, file), (err) => {
          index++
          if (err) {
            rej(err)
            return
          }
        })
      }

      res()
    })
  })
}

const emptyDestinationDirectory = async function () {
  try {
    await checkIfDirectoryExists(destDir)
  } catch (e) {
    // Don't do anything if it doesnt exist
    return
  }
  console.log('[VUE] Emptying Destination Directory')
  return new Promise((res, rej) => {
    fs.readdir(destDir, (err, files) => {
      if (err) {
        rej(err)
        return
      }

      const limit = files.length
      let index = 0

      for (const file of files) {
        fs.unlink(path.join(destDir, file), (err) => {
          index++
          if (err) {
            rej(err)
            return
          }
        })
      }

      res()
    })
  })
}

const ensureAssetsFolderExists = async function () {
  return new Promise((res, rej) => {
    fs.mkdir(destDir, (err) => {
      if (err) {
        console.log('Assets folder already exists')
        return res() // If it exists then fine
      }
      console.log('Created assets folder')
      return res()
    })
  })
}

const doWork = async function (start = null, end = null, routePlugins = [], localPlugins = []) {
  // await deleteDestinationReferenceFiles()
  await ensureAssetsFolderExists()

  // Flatten the data
  let wantedPlugins = localPlugins
  await flattenRouteArray(routePlugins, wantedPlugins)

  // Splice the plugins if necessary
  if (start !== null && end !== null) {
    wantedPlugins = wantedPlugins.slice(start, end)
  }

  const label = '[VUE] Time taken to generate plugins'
  console.time(label)

  console.log('[VUE] Please wait until all plugins have completed. This may take a while.\n\n')

  for (const plugin of wantedPlugins) {
    console.log('[VUE] Generating plugin for: ' + plugin.key)

    try {
      const chunk = await compile(plugin)
      await updatePluginFile(plugin, chunk)
    } catch (ex) {
      console.error('[VUE-ERROR] Can not build/update ' + plugin.key)
      console.debug(ex.message)
      console.debug(ex)

      throw new Error(ex)
    }
  }

  console.log('\n[VUE] Plugin generation complete!\n')

  console.timeEnd(label)

  console.log('[VUE] Done! You can now run the following command for local development: npm run host')
  process.exit()
}

const distributeNum = function (num = 1, parts = 1) {
  let n = Math.floor(num / parts)
  const arr = []
  for (let i = 0; i < parts; i++) {
    arr.push(n)
  }
  if (arr.reduce((a, b) => a + b, 0) === num) {
    return arr
  }
  for (let i = 0; i < parts; i++) {
    arr[i]++
    if (arr.reduce((a, b) => a + b, 0) === num) {
      return arr
    }
  }

  return []
}

const doFork = function (start, end, i) {
  return new Promise((res) => {
    setTimeout(() => {
      cluster.fork({
        index_start: start,
        index_end: end
      })
      res()
    }, 1000 * i)
  })
}

const beginProcess = async function () {
  console.log('Beginning the Deployment Process')

  console.log('Ensuring the assets folder exists')
  await ensureAssetsFolderExists()

  // Get our variables
  const args = await yargs(hideBin(process.argv))
    .option('cpu', {
      description: 'Set max number of CPU cores to use for building Vue plugins',
      type: 'number'
    })
    .usage('npm run deploy -- --cpu {number}')
    .example('npm run deploy', 'This is the same as "npm run deploy -- --cpu 1"')
    .example('npm run deploy -- --cpu 1', 'This will force all plugin builds to run on a single thread')
    .example('npm run deploy -- --cpu 3', 'This will distribute plugin builds to run between 3 threads')
    .option('only', {
      description: 'Only deploy these plugins',
      type: 'array'
    })
    .usage('npm run deploy -- --only plugin-1 [plugin-2 plugin-3]')
    .example('npm run deploy -- --only plugin-Auth', 'This will only build and deploy a plugin with a key of "plugin-Auth"')
    .example(
      'npm run deploy -- --only plugin-Auth plugin-User',
      'This will only build and deploy a plugin with a key of "plugin-Auth" and a plugin with a key of "plugin-User"'
    ).argv

  // Calculate a fair distribution
  const whitelist = args.only || []

  if (whitelist.length === 0) {
    console.log('Configured to build all plugins')
  } else {
    console.log('Configured to build the following plugins', ...whitelist)
  }

  const routePlugins = await fetchRouteFile(whitelist)
  console.log('Route plugins fetched,  total:', routePlugins.length)

  const localPlugins = getPluginsUsingWhitelist(whitelist)
  console.log('Local plugins fetched,  total:', localPlugins.length)

  const data = [...routePlugins, ...localPlugins]

  if (cluster.isPrimary) {
    console.log('Currently running on main single thread')
    // Create the route file
    await setRouteFile(routePlugins)

    // Get our desired CPU amount
    const availableCPUs = cpus().length - 1
    let desiredCPUs = 1
    if (args.cpu) {
      if (args.cpu > availableCPUs) {
        desiredCPUs = availableCPUs
      } else {
        desiredCPUs = args.cpu
      }
    }

    console.log('Calculating distribution')
    const total = data.length
    console.log('Host max plugin index', total)
    const dist = distributeNum(total, desiredCPUs)
    console.log('Host distribution Chart', dist)

    // Fork over CPU's if there is more than 1 CPU
    if (desiredCPUs === 1) {
      console.log('Continuting application on single main thread')
      doWork(null, null, routePlugins, localPlugins)
    } else {
      console.log('Starting build distribution')
      // Ensure to pass the dist values
      let start = 0
      for (let i = 0; i < desiredCPUs; i++) {
        const additive = dist[i]
        const end = start + additive

        await doFork(start, end, i)

        start = end
      }
    }
  } else {
    const start = process.env['index_start']
    const end = process.env['index_end']

    doWork(start, end, routePlugins, localPlugins)
  }
}

beginProcess()

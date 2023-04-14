const webpack = require('webpack')
const config = require('../webpack.config.dev')
const fs = require('fs')

const compiler = webpack(config)
console.log('Compiler Generated, Running')
compiler.run((err, stats) => {
  if (err) {
    console.error('Compile Error')
    console.error(err)
    return
  }
  console.log('Compile Complete')
  console.log('Copying file')
  fs.copyFile('./dist/AxiosApiClients.js', '../ui-network-admin/src/main/resources/static/scripts/vue-mocks/AxiosApiClients.js', (err) => {
    if (err) {
      console.error('Copy File Error')
      console.error(err)
      return
    }
    console.log('Copying Completed')
  })
})

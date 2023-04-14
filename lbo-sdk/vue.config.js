const webpack = require('webpack')
const CaseSensitivePathsPlugin = require('case-sensitive-paths-webpack-plugin');


module.exports = {
  productionSourceMap: false,
  configureWebpack: {
    devtool: 'source-map',
    output: {
      filename: '[name].js'
    },
    plugins: [
      new webpack.EnvironmentPlugin(['NODE_ENV']),
      new CaseSensitivePathsPlugin()
    ]
  },
  transpileDependencies: ['vuetify']
}

const webpack = require('webpack')

module.exports = {
  publicPath:
    process.env.NODE_ENV === 'production'
      ? '/' + process.env.CI_PROJECT_NAME + '/'
      : '/',

  productionSourceMap: false,

  configureWebpack: {
    devtool: 'source-map',
    output: {
      filename:
        process.env.NODE_ENV === 'production' ||
        process.env.NODE_ENV === 'staging'
          ? '[name].[contenthash].js'
          : '[name].js'
    },
    plugins: [
      new webpack.EnvironmentPlugin(['NODE_ENV'])
      // new BundleAnalyzerPlugin()
    ]
  },

  transpileDependencies: ['vuetify'],

  chainWebpack: (config) => {
    // Disable prefetch and preload of async modules

    config.plugins.store.delete('prefetch-app')
    config.plugins.store.delete('preload-app')

    config.plugins.store.delete('prefetch')
    config.plugins.store.delete('preload')

    config.plugins.delete('prefetch')
    config.plugins.delete('preload')
  },

  pluginOptions: {
    i18n: {
      locale: 'en',
      fallbackLocale: 'en',
      localeDir: 'locales',
      enableInSFC: false
    }
  }
}

import Vue from 'vue'
import App from './App.vue'
import './registerServiceWorker'
import router from './core/router'
import { store } from './core/store'
import vuetify from './core/modules/vuetify'

Vue.config.productionTip = false

new Vue({
  router,
  store,
  vuetify,
  render: (h) => h(App)
}).$mount('#app')

// Remove in prod
;(window as any).Vue = Vue
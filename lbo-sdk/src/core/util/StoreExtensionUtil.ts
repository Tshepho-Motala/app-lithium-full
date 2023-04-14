import { createModule } from 'vuex-class-component'
import { VuexModule } from 'vuex-class-component/dist/interfaces'

export const StoreExtension = function (name: string): typeof VuexModule {
  const vuexModule = createModule({
    namespaced: name,
    strict: false,
    target: 'nuxt'
  })

  Object.defineProperty(vuexModule, '__loaded__', {
    value: false,
    writable: true
  })

  return vuexModule
}

import { StoreExtension } from '@/core/util/StoreExtensionUtil'
import { mutation } from 'vuex-class-component'

// Prerequisite reading: https://github.com/michaelolof/vuex-class-component
export default class VueStoreDemoStore extends StoreExtension('VueStoreDemoStore') {
  works = false

  @mutation
  setWorks() {
    this.works = true
  }
}

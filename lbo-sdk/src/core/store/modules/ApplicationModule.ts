import { createModule, mutation } from 'vuex-class-component'

const ApplicationModule = createModule({
  namespaced: 'ApplicationModule',
  strict: false,
  target: 'nuxt'
})

export class ApplicationStore extends ApplicationModule {
  wide = false
  showNavigation = true

  @mutation
  changeShowNavigation() {
      this.showNavigation = !this.showNavigation
  }
}

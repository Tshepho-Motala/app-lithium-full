import { StoreExtension } from '@/core/util/StoreExtensionUtil'
import { mutation } from 'vuex-class-component'

export default class AuthenticationStore extends StoreExtension('AuthenticationStore') {
  get authenticated() {
    return !!this.access_token && !!this.refresh_token
  }
  access_token: string | null = null
  refresh_token: string | null = null

  @mutation
  clear() {
    this.access_token = null
    this.refresh_token = null
  }
}

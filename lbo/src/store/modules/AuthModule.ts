import { Role, Token } from '@/common/enums'
import apiConnect from '@/modules/apiConnect'
import { action, createModule, mutation } from 'vuex-class-component'

const AuthModule = createModule({
  namespaced: 'AuthModule',
  strict: false,
  target: 'nuxt'
})

export class AuthStore extends AuthModule {
  role: Role = Role.Anonymous
  token: Token | null = null

  get authenticated(): boolean {
    return this.role !== Role.Anonymous && this.token !== null
  }

  @action
  async login(role: Role) {
    const rt: Map<Role, Token> = new Map([
      [Role.Anonymous, Token.Anonymous],
      [Role.User, Token.User],
      [Role.Administrator, Token.Admin]
    ])

    const token = await apiConnect.auth.login('', '', rt.get(role))
    this.token = token
    this.role = role
  }

  @mutation
  setAnonymous() {
    this.role = Role.Anonymous
  }

  @action
  async logout(): Promise<void> {
    await apiConnect.auth.logout()

    this.role = Role.Anonymous
    this.token = null
  }
}

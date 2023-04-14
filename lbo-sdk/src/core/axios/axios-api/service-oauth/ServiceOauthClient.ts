import AuthenticationContract from '@/core/interface/contract-interfaces/service-oauth/AuthenticationContract'
import AxiosApiClient from '../../AxiosApiClient'

export default class ServiceOauthClient extends AxiosApiClient {
  localPrefix = 'server-oauth2/backoffice/oauth/token'
  livePrefix = 'auth/backoffice/oauth/token'

  token = btoa('default/una:uNa@h4sANEWp4sswd')

  getHeaders() {
    const headers = super.getHeaders() || {}
    const Authorization = 'Basic ' + this.token

    return {
      ...headers,
      Authorization, // Place this after ...headers to override the Authorization tag
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8'
    }
  }

  async authenticate(domain: string, username: string, password: string, type: string = 'password'): Promise<AuthenticationContract | null> {
    return await this.post<AuthenticationContract>({
      grant_type: type,
      domain,
      username,
      password
    })
  }
}

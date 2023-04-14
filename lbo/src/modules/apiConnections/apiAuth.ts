import { Config } from '@/common/config'
import { AuthEndpoint, Token } from '@/common/enums'
import httpCommon from '@/common/httpCommon'

export default class ApiAuth {
  url = process.env['API_URL'] || Config.ApiUrl

  async login(
    username: string,
    password: string,
    token?: Token
  ): Promise<Token> {
    if (token) {
      return Promise.resolve(token)
    }

    const body = new URLSearchParams({ username, password })
    const headers = await httpCommon.getHeaders()

    const response = await fetch(`${this.url}${AuthEndpoint.loginPost}`, {
      method: 'POST',
      credentials: 'include',
      headers,
      body
    })

    return (await response.json()) as Token
  }

  async logout(): Promise<void> {
    await fetch(`${this.url}${AuthEndpoint.logoutPost}`, {
      credentials: 'include'
    })
  }
}

import { AxiosInstance } from 'axios'
import { ApiClientInterface, ApiConfigInterface } from '@/core/interface/provider/ApiClientInterface'
import Cookies from 'js-cookie'

import UserServiceInterface from '@/core/interface/service/UserServiceInterface'

export default class ApiClient<T extends AxiosInstance /** Append additional types here, eg  '| FetchInstance'**/> implements ApiClientInterface<T> {
  constructor(public userService: UserServiceInterface, public provider: T) {}

  async responseErrorFunction(error) {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    const originalRequest: ApiConfigInterface = error.config
    if ((error.response.status === 401 || error.response.status === 403) && !originalRequest._retry) {
      originalRequest._retry = true

      // await reload token in Angular userService method
      await this.reloadTokenAngular()
      // write new token
      originalRequest.headers.Authorization = `Bearer ${localStorage.getItem('lithium-oauth-token')}`
      return originalRequest
    }
    return Promise.reject(error)
  }

  async reloadTokenAngular() {
    await this.userService._refreshToken()
  }
}

import axios, { AxiosResponse } from 'axios'
import UserServiceInterface from '@/core/interface/service/UserServiceInterface'
import ApiBasicClient from '@/core/api/ApiBasicClient'
import Cookies from 'js-cookie'
import { ApiConfigInterface } from '@/core/interface/provider/ApiClientInterface'

// Base  provider config
const AxiosRequestConfig: ApiConfigInterface = {
  // BASE URL
  baseURL: '/',
  headers: {
    'Content-type': 'application/json;charset=UTF-8'
  }
}

export default class AxiosBasicClient extends ApiBasicClient {
  constructor(userService: UserServiceInterface) {
    // send  provider in general class
    super(userService, axios.create(AxiosRequestConfig))

    this.provider.interceptors.request.use(
      (config: ApiConfigInterface) => {
        // Do something before request is sent
        if (localStorage.getItem('lithium-oauth-token')) {
          config.headers.Authorization = `Bearer ${localStorage.getItem('lithium-oauth-token')}`
        }
        return config
      },
      (error) => {
        // Do something with request error
        return Promise.reject(error)
      }
    )
    this.provider.interceptors.response.use(
      function (response: AxiosResponse) {
        // Any status code that lie within the range of 2xx cause this function to trigger
        // Do something with response data
        return response
      },
      async (error) => {
        const originalRequest: ApiConfigInterface = await this.responseErrorFunction(error)
        if (originalRequest) {
          return axios(originalRequest)
        }
        return Promise.reject(error)
      }
    )
  }
}

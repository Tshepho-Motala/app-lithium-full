import axios, { AxiosInstance, AxiosResponse } from 'axios'
import UserServiceInterface from '@/core/interface/service/UserServiceInterface'
import ApiClient from '@/core/api/ApiClient'
import Cookies from 'js-cookie'

// response.data.data || response.data is being used due to how Lithium API returns data
export default class AxiosApiClient extends ApiClient<AxiosInstance> {
  gatewayUrl: string = 'http://localhost:9000'

  /**
   * Use this prefix when wanting to query services locally.
   * Through SDK
   */
  localPrefix: string = ''
  /**
   * Use this prefix when wanting to query services live
   * Through LBO, any env
   */
  livePrefix: string = ''

  get servicePrefix(): string {
    if (this.isLocalSdk) {
      return this.localPrefix
    }
    return this.livePrefix
  }

  get isLocal() {
    return window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
  }

  get isLocalLbo() {
    return this.isLocal && window.location.port === '9800'
  }

  get isLocalSdk() {
    return this.isLocal && !this.isLocalLbo
  }

  constructor(userService: UserServiceInterface) {
    super(
      userService,
      axios.create({
        headers: {
          'Content-type': 'application/json;charset=UTF-8'
        }
      })
    )
  }

  /**
   * Takes in parts of a URL and combines them, for example:
   *
   * parts = ['https://www.google.com/','/a/', '/b', 'c/']
   * will create
   * "https://www.google.com/a/b/c"
   * @param parts Parts of a URL to build
   * @returns Properly pathed URL
   */
  path(...parts: string[]): string {
    return parts.map((x) => x.replace(new RegExp('/$|^/', 'g'), '')).join('/')
  }

  getBaseUrlByEnvironment(useLocalGateway: boolean = true) {
    // TODO: Find a way to allow the developer to select use local gateway

    if (this.isLocalSdk) {
      if (useLocalGateway) {
        return this.path(this.gatewayUrl, this.servicePrefix)
      }
    }

    if (!this.isLocal) {
      // TODO: Get environment URL
    }

    return this.path('/', this.servicePrefix)
  }

  getHeaders(contentType?: string): undefined | { [key: string]: string } {
    const bearer_token = localStorage.getItem('lithium-oauth-token')
    const headers = {}

    if (bearer_token) {
      headers['Authorization'] = 'Bearer ' + bearer_token
    }
    if (contentType) {
      headers['Content-Type'] = contentType
    }

    return headers
  }

  /**
   * Get data from a URL
   * @param url URL to GET from
   * @returns Data if success, null if error
   */
  async get<T>(...urlParts: string[]): Promise<T | null> {
    const baseURL = this.getBaseUrlByEnvironment()
    const url = this.path(baseURL, ...urlParts)

    const headers = this.getHeaders()

    const response = await this.provider.get(url, { headers })

    if (response.status === 200) {
      return response.data.data || response.data
    }

    this.logError(url, response)
    return null
  }

  /**
   * Get data from a URL
   * @param url URL to GET from
   * @returns Data if success, null if error
   */
  async getWithParameter<T>(parameters: { [key: string]: string }, ...urlParts: string[]): Promise<T | null> {
    const baseURL = this.getBaseUrlByEnvironment()
    const url = this.path(baseURL, ...urlParts)
    const parameterList: string[] = []
    for (const key of Object.keys(parameters)) {
      parameterList.push(`${key}=${parameters[key]}`)
    }

    const urlWithParameters = url + '?' + parameterList.join('&')
    const headers = this.getHeaders()

    const response = await this.provider.get(urlWithParameters, { headers })

    if (response.status === 200) {
      if (response.data.draw) {
        return response.data
      }
      return response.data.data || response.data
    }

    this.logError(url, response)
    return null
  }

  /**
   * Post data to a URL
   * @param url URL to POST to
   * @param data Data to POST in the Body
   * @returns null if success, anything else if error
   */
  async post<T>(data: { [key: string]: any }, ...urlParts: string[]): Promise<null | T> {
    const url = this.createFullUrl(...urlParts)
    const headers = this.getHeaders()

    // const response = await this.provider.post(url, data, { headers })
    const formData = new FormData()
    for (const key of Object.keys(data)) {
      formData.append(key, data[key])
    }

    const response = await this.provider({
      method: 'POST',
      url,
      data: formData,
      headers
    })

    if (response.status === 200) {
      return response.data.data || response.data
    }

    this.logError(url, response, data)
    return response.data.data || response.data
  }

  async postRaw<T>(data: any, ...urlParts: string[]): Promise<null | T> {
    const url = this.createFullUrl(...urlParts)
    const headers = this.getHeaders()

    const response = await this.provider.post(url, data, { headers })

    if (response.status === 200) {
      return response.data.data || response.data
    }

    this.logError(url, response, data)
    return response.data.data || response.data
  }

  async postJson<T>(data: any, ...urlParts: string[]): Promise<null | T> {
    const url = this.createFullUrl(...urlParts)
    const headers = this.getHeaders('application/json')

    const response = await this.provider.post(url, JSON.stringify(data), { headers })

    if (response.status === 200) {
      return response.data.data || response.data
    }

    this.logError(url, response, data)
    return response.data.data || response.data
  }

  async postWithParameters<T>(parameters: { [key: string]: string }, ...urlParts: string[]): Promise<null | T> {
    const baseURL = this.getBaseUrlByEnvironment()
    const url = this.path(baseURL, ...urlParts)
    const parameterList: string[] = []
    for (const key of Object.keys(parameters)) {
      parameterList.push(`${key}=${parameters[key]}`)
    }

    const urlWithParameters = url + '?' + parameterList.join('&')
    const headers = this.getHeaders()

    const response = await this.provider.post(urlWithParameters, {}, { headers })

    if (response.status === 200) {
      return response.data
    }

    this.logError(url, response)
    return null
  }

  async postWithURLParametersAndParams<T>(parameters: { [key: string]: string }, data: any, ...urlParts: string[]): Promise<null | T> {
    const baseURL = this.getBaseUrlByEnvironment()
    const url = this.path(baseURL, ...urlParts)
    const parameterList: string[] = []
    for (const key of Object.keys(parameters)) {
      parameterList.push(`${key}=${parameters[key]}`)
    }

    const urlWithParameters = url + '?' + parameterList.join('&')
    const headers = this.getHeaders()

    const response = await this.provider.post(urlWithParameters, data, { headers })

    if (response.status === 200) {
      return response.data
    }

    this.logError(url, response)
    return null
  }


  //Delete Promotion by ID
  async delete<T>(...urlParts: string[]): Promise<null | T> {
    const url = this.createFullUrl(...urlParts)
    const headers = this.getHeaders('application/json')

    const response = await this.provider.delete(url, { headers })

    if (response.status === 200) {
      return response.data.data || response.data
    }

    this.logError(url, response)
    return response.data.data || response.data
  }

  private logError(srcUrl: string, respons: AxiosResponse, optionalDataForDebug: any = null) {
    // Log error
  }

  private createFullUrl(...urlParts: string[]) {
    const baseURL = this.getBaseUrlByEnvironment()
    return this.path(baseURL, ...urlParts)
  }
}

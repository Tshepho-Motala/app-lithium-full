import ListenerServiceInterface from "@/core/interface/service/ListenerServiceInterface"

export default class ListenerServiceMock implements ListenerServiceInterface {
  subscriptions: Map<string, (params?: any | undefined) => any | void> = new Map()

  subscribe(key: string, fn: (params?: any | undefined) => any | void, replaceExisting?: boolean): void {
    if (this.subscriptions.has(key) && !replaceExisting) {
      console.warn('There is already a subscription with the key: ' + key)
      return
    }

    this.subscriptions.set(key, fn)
  }

  call(key: string, params: any[]): void {
    if (!this.subscriptions.has(key)) {
      console.warn('There is no subscription with the key: ' + key)
      return
    }

    const promise = this.subscriptions.get(key)
    if (promise !== undefined) {
      if (!params || params.length === 0) {
        promise()
      } else {
        promise(params)
      }
    }
  }
}

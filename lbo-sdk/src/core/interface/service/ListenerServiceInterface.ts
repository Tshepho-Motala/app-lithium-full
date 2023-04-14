export default interface ListenerServiceInterface {
  subscriptions: Map<string, (params?: any | undefined) => any | void>

  subscribe(key: string, fn: (params?: any | undefined) => any | void, replaceExisting?: boolean): void
  call(key: string, params?: any | undefined): void
}

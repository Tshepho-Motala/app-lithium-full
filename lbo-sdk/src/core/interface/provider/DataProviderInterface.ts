export default interface DataProviderInterface {
  user?: {
    id: number
    guid: string
    email: string
    username: string
    domain: {
      name: string
      id: number
    }
  }
}

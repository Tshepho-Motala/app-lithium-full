export default interface TranslateServiceInterface {
  instant: (key: string) => string
  isReady: () => boolean
}

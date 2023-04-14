import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface"

export default class Translate {
  translateService: TranslateServiceInterface

  constructor(translateService: TranslateServiceInterface) {
    this.translateService = translateService
  }

  this(key: string | undefined): string {
    if (!key) {
      return ''
    }
    return this.translateService.instant(key)
  }
}

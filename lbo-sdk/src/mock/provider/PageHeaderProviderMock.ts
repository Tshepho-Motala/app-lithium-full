import PageHeaderProviderInterface from '@/core/interface/provider/PageHeaderProviderInterface'
import {BankAccountDomainInterface, DomainSingleSelectInterface} from "@/core/interface/DomainSingleSelectInterface";

export default class PageHeaderProviderMock implements PageHeaderProviderInterface {
  domainSelect(item: DomainSingleSelectInterface | BankAccountDomainInterface | null, isAlreadyChecked: boolean): void {}

  clearSelectedDomain(): void {}

  textTitle(): string {
    return ''
  }

  textDescr(): string {
    return ''
  }

  getDomains(): Promise<any> {
    return new Promise((res, rej) => {});
  }

  getDomainsList(): DomainSingleSelectInterface[] {
    return []
  }

  textSubtitle(): string {
    return ''
  }
}
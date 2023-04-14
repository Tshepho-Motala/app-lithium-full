import {BankAccountDomainInterface, DomainSingleSelectInterface} from "@/core/interface/DomainSingleSelectInterface";

export default interface PageHeaderProviderInterface {
    domainSelect(item: DomainSingleSelectInterface | BankAccountDomainInterface | null, isAlreadyChecked: boolean): void
    clearSelectedDomain(): void
    textTitle(): string
    textDescr(): string
    getDomains(): Promise<any>
    getDomainsList(): DomainSingleSelectInterface[]
    textSubtitle(): string
}

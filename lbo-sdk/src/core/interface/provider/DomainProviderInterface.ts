import { DomainItemInterface } from "@/plugin/cms/models/DomainItem";


export default interface DomainProviderInterface {
  getDomains(): Promise<DomainItemInterface[]>
}

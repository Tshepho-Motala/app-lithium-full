import {
    CashierConfigDmpp,
    CashierConfigDmpu,
    CashierConfigMethod,
    CashierConfigProcessor,
    CashierConfigProcessorProperty, CashierConfigProfile, NewAddedProcessorInterface, NewMethodInterface
} from './../cashierConfig/CashierConfigInterface'

export default interface CashierConfigTempProviderInterface {
    deleteProcessor(processor: CashierConfigProcessor): Promise<any>
    findProperties(id: string | number): Promise<any>
    saveProperties(id: string | number, properties: CashierConfigProcessorProperty[]): Promise<any>
    domainMethods(domainName: string, type: string): Promise<any>
    domainMethodProcessors(methodId: string | number): Promise<any>
    changelogs(domainName: string, entity: string | number, page: number): Promise<any>
    methodChangelogs(domainName: string, deposit: boolean, page: number): Promise<any>
    mapAuthorNameToChangeLogs(domainName: string, list: any): Promise<any>
    getAccessRules(domainName: string): Promise<any>
    findProfiles(domainName: string): Promise<any>
    searchUsers(domainName: string, guid: string): Promise<any>
    getCurrencyMethod(domainName: string): Promise<any>
    domainMethodUpdateMultiple(methods: CashierConfigMethod[]): Promise<any>
    domainMethodUser(id: string | number, guid: string): Promise<any>
    domainMethodProcessorsByUserNoImage(guid: string): Promise<any>
    frontendMethods(type: string, guid: string): Promise<any>
    frontendProcessors(id: string | number, guid: string): Promise<any>
    domainMethodProfile(methodId: string | number, profileId: string | number): Promise<any>
    domainMethodProcessorsByProfileNoImage(profile: CashierConfigProfile): Promise<any>
    domainMethodProcessorUpdate(processor: CashierConfigProcessor): Promise<any>
    domainMethodProcessorUserCreateOrUpdate(dmpu: CashierConfigDmpu, type?: string): Promise<any>
    domainMethodProcessorUserSave(dmpu: CashierConfigDmpu, type?: string): Promise<any>
    domainMethodProcessorProfileCreateOrUpdate(dmpp: CashierConfigDmpp): Promise<any>
    domainMethodProcessorProfileSave(dmpp: CashierConfigDmpp, type?: string): Promise<any>
    domainMethodProcessorSave(processor: CashierConfigProcessor, type: string): Promise<any>
    domainMethodProcessorSaveDL(processor: CashierConfigProcessor): Promise<any>
    domainMethodAccounting(id: string | number): Promise<any>
    domainMethodAccountingUser(id: string | number, username: string): Promise<any>
    domainMethodProcessorAccounting(id: string | number): Promise<any>
    domainMethodProcessorAccountingUser(id: string | number, username: string): Promise<any>
    domainMethodProcessorUserDelete(dmpu: CashierConfigDmpu, type: string): Promise<any>
    domainMethodProcessorProfileDelete(dmpp: CashierConfigDmpp, type: string): Promise<any>
    domainMethodProcessorDelete(processor: CashierConfigProcessor, type: string): Promise<any>
    domainMethodAdd(domain: string, newMethod: NewMethodInterface, type: string): Promise<any>
    domainMethodUpdate(method: CashierConfigMethod): Promise<any>
    domainMethodDeleteFull(method: CashierConfigMethod): Promise<any>
    methodCopy(model: CashierConfigMethod): Promise<any>
    domainMethodProfileUpdate(profile: any): Promise<any>
    domainMethodUserUpdate(user: any): Promise<any>
    processors(id: number, type: string): Promise<any>
    domainMethodProcessorAdd(newProcessor: NewAddedProcessorInterface): Promise<any>
    user(guid: string): Promise<any>
    cashierMethods(): Promise<any>
    cashierDmpRest(): Promise<any>
    domainMethodUserUpdateMultiple(methods: CashierConfigMethod[]): Promise<any>
    domainMethodProfileUpdateMultiple(methods: CashierConfigMethod[]): Promise<any>
    domainMethodProcessorUserUpdateMultiple(dmpus: CashierConfigDmpu[]): Promise<any>
    domainMethodProcessorProfileUpdateMultiple(dmpps: CashierConfigDmpp[]): Promise<any>
    domainMethodProcessorUpdateMultiple(processors: CashierConfigProcessor[]): Promise<any>
}
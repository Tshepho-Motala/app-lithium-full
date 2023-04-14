import CashierConfigTempProviderInterface from '@/core/interface/provider/CashierConfigTempProviderInterface'
import {
    CashierConfigDmpp,
    CashierConfigDmpu,
    CashierConfigMethod,
    CashierConfigProcessor,
    CashierConfigProcessorProperty, CashierConfigProfile, NewAddedProcessorInterface, NewMethodInterface
} from '@/core/interface/cashierConfig/CashierConfigInterface'

export default class CashierConfigTempProviderMock implements CashierConfigTempProviderInterface {
    deleteProcessor(processor: CashierConfigProcessor): Promise<any> {
        return new Promise((res, rej) => {});
    }

    findProperties(id: string | number): Promise<any> {
        return new Promise((res, rej) => {});
    }

    saveProperties(id: string | number, properties: CashierConfigProcessorProperty[]): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethods(domainName: string, type: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodProcessors(methodId: string | number): Promise<any> {
        return new Promise((res, rej) => {});
    }

    changelogs(domainName: string, entity: string | number, page: number): Promise<any> {
        return new Promise((res, rej) => {});
    }

    methodChangelogs(domainName: string, deposit: boolean, page: number): Promise<any> {
        return new Promise((res, rej) => {});
    }

    mapAuthorNameToChangeLogs(domainName: string, list: any): Promise<any> {
        return new Promise((res, rej) => {});
    }

    getAccessRules(domainName: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    findProfiles(domainName: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    searchUsers(domainName: string, guid: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    getCurrencyMethod(domainName: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodUpdateMultiple(methods: CashierConfigMethod[]): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodUser(id: string | number, guid: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorsByUserNoImage(guid: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    frontendMethods(type: string, guid: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    frontendProcessors(id: string | number, guid: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodProfile(methodId: string | number, profileId: string | number): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorsByProfileNoImage(profile: CashierConfigProfile): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorUpdate(processor: CashierConfigProcessor): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorUserCreateOrUpdate(dmpu: CashierConfigDmpu, type?: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorUserSave(dmpu: CashierConfigDmpu, type?: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorProfileCreateOrUpdate(dmpp: CashierConfigDmpp): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorProfileSave(dmpp: CashierConfigDmpp, type?: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorSave(processor: CashierConfigProcessor, type: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorSaveDL(processor: CashierConfigProcessor): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodAccounting(id: string | number): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodAccountingUser(id: string | number, username: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorAccounting(id: string | number): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorAccountingUser(id: string | number, username: string): Promise<any> {
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorUserDelete(dmpu: CashierConfigDmpu, type: string): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorProfileDelete(dmpp: CashierConfigDmpp, type: string): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorDelete(processor: CashierConfigProcessor, type: string): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodAdd(domain: string, newMethod: NewMethodInterface, type: string): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodUpdate(method: CashierConfigMethod): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodDeleteFull(method: CashierConfigMethod): Promise<any>{
        return new Promise((res, rej) => {});
    }

    methodCopy(model: CashierConfigMethod): Promise<any>{
        return new Promise((res, rej) => {});
    }

    cashierMethods(): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodProfileUpdate(profile: any): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodUserUpdate(user: any): Promise<any>{
        return new Promise((res, rej) => {});
    }

    processors(id: number, type: string): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorAdd(newProcessor: NewAddedProcessorInterface): Promise<any>{
        return new Promise((res, rej) => {});
    }

    user(guid: string): Promise<any>{
        return new Promise((res, rej) => {});
    }

    cashierDmpRest(): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodUserUpdateMultiple(methods: CashierConfigMethod[]): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodProfileUpdateMultiple(methods: CashierConfigMethod[]): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorUserUpdateMultiple(dmpus: CashierConfigDmpu[]): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorProfileUpdateMultiple(dmpps: CashierConfigDmpp[]): Promise<any>{
        return new Promise((res, rej) => {});
    }

    domainMethodProcessorUpdateMultiple(processors: CashierConfigProcessor[]): Promise<any>{
        return new Promise((res, rej) => {});
    }
}
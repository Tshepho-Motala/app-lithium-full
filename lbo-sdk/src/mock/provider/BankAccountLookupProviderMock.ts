import BankAccountLookupProviderInterface from '@/core/interface/provider/BankAccountLookupProviderInterface'

export default class BankAccountLookupProviderMock implements BankAccountLookupProviderInterface {
  domainMethodProcessors(): Promise<any> {
    return new Promise((res, rej) => {});
  }

  banks(processorProperties: any, processorUrl: string): Promise<any> {
    return new Promise((res, rej) => {});
  }

  lookup(bankAccountLookupRequest: any, processorUrl: string): Promise<any> {
    return new Promise((res, rej) => {});
  }

  getModel(): Promise<any> {
    return new Promise((res, rej) => {});
  }

  refresh(): Promise<any> {
    return new Promise((res, rej) => {});
  }

  createManualWithdrawal(domainMethod: any, userGuid: string, accountNumber: string, bankCode: string, amount: number | string, comment: string, redirectToTransaction: boolean): Promise<any> {
    return new Promise((res, rej) => {});
  }

  searchUsers(search: string | null): Promise<any> {
    return new Promise((res, rej) => {});
  }

  getUserBalance(domain: string , user: string | number): Promise<any> {
    return new Promise((res, rej) => {});
  }

  getCurrencySymbol(): Promise<any> {
    return new Promise((res, rej) => {});
  }

}
export default interface BankAccountLookupProviderInterface {
  domainMethodProcessors(): Promise<any>
  banks(processorProperties: any, processorUrl: any): Promise<any>
  lookup(bankAccountLookupRequest: any, processorUrl: any): Promise<any>
  getModel(): Promise<any>
  refresh(): Promise<any>
  createManualWithdrawal(domainMethod: any, userGuid: string, accountNumber: string, bankCode: string, amount: number | string, comment: string, redirectToTransaction: boolean): Promise<any>
  searchUsers(search: string | null): Promise<any>
  getUserBalance(domain: string , user: string | number): Promise<any>
  getCurrencySymbol(): Promise<any>
}

import {
  AutoWithdrawalItemOperator,
  AutoWithdrawalItemField,
  AutoWithdrawalItem,
  DomainDropDown,
} from '@/core/interface/AutoWithdrawalInterface'
import {CashierTransactionsDataInterface} from "@/core/interface/cashier/cashierTransactions";


export default interface CashierProvideInterface {
  loadLastXCashierTransactions(count: number): Promise<any>
  openUserTransactions(domain: string, userId: string): void
  loadAutoWithdrawalsRulesets(): Promise<any>
  exportAutoWithdrawalsRulesets(data:any): Promise<any>
  importAutoWithdrawalsRulesets(data:any): Promise<any>
  submitAutoWithdrawalsRulesets(data:any): Promise<any>
  ruleCashierRest(): Promise<any>
  ruleVerificationStatusRest(): Promise<any>
  ruleFindAllTags(data:any): Promise<any>
  cloneRule(domain:string, data:AutoWithdrawalItem): Promise<any>
  getRuleset(): Promise<any>
  domains: DomainDropDown[]
  rulesOperators:  AutoWithdrawalItemOperator[]
  rulesFields: AutoWithdrawalItemField[]
  rulesetID: Number
  transactionID: Number | undefined
  transaction: CashierTransactionsDataInterface | undefined
  refreshTransaction(): void
  openUserBalanceMovementTransactionTransactions(domain: string, userId: number): void
  openTransactionAdd(domain: string): void
}
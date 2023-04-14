import {CurrentTransactionInterface, UserAutorInterface} from "@/core/interface/cashier/cashierTransactions";

export interface CashierTransactionsDetailInterface {
    id: number
    amountCents: number | null
    autoApproved: boolean
    declineReason: string | null
    createdOn: Date
    updatedOn: Date
    bonusCode: number | null
    bonusId: number | null
    directWithdrawal: boolean
    feeCents: number | null
    manual: boolean
    transactionType: string
    testAccount: boolean
    currencyCode: string
    sessionId?: number
    user: UserAutorInterface | null
    current: CurrentTransactionInterface
    runtime:number
    tags:string[]
    [key: string]: any
}

export interface CashierTransactionsApiSizeInterface {
    page: number
    itemsPerPage: number
}
export interface CashierTransactionsApiOrderInterface {
    sort?: string
    order?: string
    page?: number,
    size?: number,
    [key: string]: any
}
export interface CashierTransactionsApiParamsInterface {
    autoApproved?: undefined | boolean
    testAccount?: undefined | boolean
    createdDateRangeStart?: undefined | Date | string
    createdDateRangeEnd?: undefined | Date | string
    updatedDateRangeStart?: undefined | Date | string
    updatedDateRangeEnd?: undefined | Date| string
    registrationStart?: undefined | Date| string
    registrationEnd?: undefined | Date| string
    id?: undefined | string
    transactionType?: string | undefined,
    paymentType?: string,
    statuses?: undefined | string[],
    processorReference?: string,
    lastFourDigits?: string,
    [key: string]: any

}

export interface CashierTransactionsApiParamRransactionRuntimeQueryInterface {
    operator: string
    value:  number | undefined
}
export interface CashierTransactionsDataInterface {
    id: number
    amountCents: number | null
    approveDisabled: boolean
    autoApproved: boolean
    declineReason: string | null
    directWithdrawal: boolean
    feeCents: number | null
    manual: boolean
    transactionType: string
    testAccount: boolean
    currencyCode: string
    sessionId?: number
    user: UserAutorInterface | null
    current: CurrentTransactionInterface

    [key: string]: any
}

export interface BalanceMovementTransactionsDataItemInterface {
    id: number
    amountCents: number | null
    approveDisabled: boolean
    postEntryAccountBalanceCents: number | null
    transaction: CurrentTransactionInterface
    date: number
    [key: string]: any
}

export interface CashierFeesInterface {
    flat: number,
    flatDec?: number | null,
    id?: number | null,
    minimum: number,
    minimumDec?: number | null,
    percentage?: number | null,
    strategy?: number | null,
    version?: number | null
    percentageFee: number,
    playerAmount: number | string,
    playerAmountCents: number,
    depositAmount: number | string,
    depositAmountCents: number,
    feeAmount: number | string,
    minimumUsed?: boolean,

    [key: string]: any

}

export interface UserInterface {
    cellphoneNumber: string | number | null,
    comments: any,
    countryCode: string | null,
    createdDate: number,
    deleted: boolean,
    domain: {
        id: number,
        name: string,
        [key: string]: any
    },
    emailValidated: boolean,
    firstName: string,
    guid: string,
    id: number,
    lastName: string,
    telephoneNumber: string | number | null,
    updatedDate: number,
    username: string,

    [key: string]: any
}

export interface UserAutorInterface {
    guid: string
    id: number | string
    limits: null | number
    testAccount: boolean
    version?: number | null

    [key: string]: any
}

export interface CurrentTransactionInterface {
    author: UserAutorInterface
    id: number | string
    status: CurrentStatusTransactionInterface
    timestamp: number
    processor: any

    [key: string]: any

}

export interface CurrentStatusTransactionInterface {
    active: boolean
    approved: boolean
    autoApproved: boolean
    autoApprovedDelayed: boolean
    cancelled: boolean
    code: string
    declined: boolean
    deleted: boolean
    description: null | string
    fatalError: boolean
    id: number | string
    onHold: boolean
    playerCancelled: boolean
    start: boolean
    success: boolean
    waitForApproval: boolean
    waitForProcessor: boolean

    [key: string]: any
}

export interface BalanceTransactionInterface {
    ltDeposits: number,
    ltWithdrawals: number,
    pendingWithdrawals: number,
    escrowBalance: number,
    currentBalance: number,

    [key: string]: any
}

export interface ParamsWorkflowApiInterface {
    page: number,
    pageSize: number,
    totalPages?: number,
}

export interface ModelTransactionInterface {
    tranAmount: number,
    isWithdrawalAndApprovedInWorkflow: boolean
}

export interface transactionDataListItemInterface {
    field: string
    id: number
    output: boolean
    stage: number
    transaction: CashierTransactionsDataInterface | null
    value: number | string
}

export interface lastXCashierTransactionsApiParamsInterface {

    count: number,
    trId: number | string

}

export interface lastXCashierTransactionsApiParamsInterface {

    count: number,
    trId: number | string

}

export interface sendTransactionRemarksParamsInterface {
    message: string
}

export interface changeStatusParamsInterface {
    amount: number
}

export interface withdrawApprovableParamsInterface {
    currencyCode: string,
    guid: string,
    isWithdrawalFundsReserved: boolean
}

export interface transactionCancelParamsInterface {
    comment: string
}

export interface transactionOnHoldParamsInterface {
    reason: string
}

export interface getPaymentMethodsByTranIdParamsInterface {
    height: number
}

export interface bankAccountLookupParamsInterface {
    domainName: string
    processorCode: number | string
    processorDescription: string
    processorUrl: string
    transactionId: number
}

export interface domainFindByNameParamsInterface {
    name: string
}

export interface paymentMethodStatusUpdateParamsInterface {
    comment: string
    contraAccount: boolean
    statusId: number
    verified: boolean | null | undefined
}

export interface paymentMethodStatusUpdatemodel {
    comment: string
    contraAccount: boolean
    status: number | undefined
    verifiedModel: number | null | undefined
}
export interface approveTransactionsInterface {
    ids: string
    code?: string
}

export interface bulkTransactionsInterface {
    transactionId: string | number
    accountCode: string
    comment: string
}

export interface transactionsListParamsInterface {
    draw: number
    start: number
    length: number
    autoApproved?: undefined | boolean
    testAccount?: undefined | boolean
    createdDateRangeStart?: undefined | Date
    createdDateRangeEnd?: undefined | Date
    updatedDateRangeStart?: undefined | Date
    updatedDateRangeEnd?: undefined | Date
    registrationStart?: undefined | Date
    registrationEnd?: undefined | Date
    id?: undefined | string
    transactionType?: string | undefined,
    paymentType?: string,
    statuses?: undefined | string[],
    processorReference?: string,
    lastFourDigits?: string,
    [key: string]: any
}

export interface BulkTransactionsRequestInterface {
    failedIds: number[]
    proceedIds: number[]
    [key: string]: any
}

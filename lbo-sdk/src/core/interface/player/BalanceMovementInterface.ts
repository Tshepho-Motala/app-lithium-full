export  interface  BalanceMovementListInterface {
    userGuid: string
    page?: number
    pageSize?:number
    dateRangeStart?: string | number
    dateRangeEnd?: string | number
    domainName?: string
    providerTransId?: string | number
    transactionType?: string
    [key: string]: any
}

export  interface  BalanceMovementTypeListItemInterface {
    code: string
    id: number | string
    version?: number
    [key: string]: any
}
export  interface  BalanceMovementTypeSendInterface {
    userGuid: string
}

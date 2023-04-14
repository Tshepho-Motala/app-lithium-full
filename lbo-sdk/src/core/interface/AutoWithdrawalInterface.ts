export  interface  AutoWithdrawalRuleItem {
    enabled: boolean,
    field: null | number | string,
    operator: null | number | string,
    value: null | number | string | any[],
    value2: null | number | string,
    id?: number | string | null
    [key: string]: any
}

export  interface  AutoWithdrawalItem {
    delay: null | number | string
    delayedStart: boolean
    domain: any
    enabled: boolean
    id?: number | string | null
    name: string
    rules: AutoWithdrawalRuleItem[]
    [key: string]: any
}

export  interface  AutoWithdrawalRulsetUpdate {
    delay: null | number | string
    delayedStart: boolean
    enabled: boolean
    id?: number | string | null
    name: string

    [key: string]: any
}

export  interface  AutoWithdrawalItemValueYesOrNo {
    id?: number | string | null
    name: string
}

export  interface  AutoWithdrawalItemOperator {
    displayName: string
    field: string
    id?: number | string | null
    [key: string]: any
}
export  interface  AutoWithdrawalItemField {
    displayName: string
    operator: string
    id?: number | string | null
    [key: string]: any
}
export  interface  AutoWithdrawalItemCashier {
    code: string
    enabled: boolean
    id?: number | string | null
    name: string
    [key: string]: any
}
export  interface  AutoWithdrawalItemVerification {
    code:string
    level: number
    id?: number | string | null
    [key: string]: any
}

export  interface  AutoWithdrawalItemTag{
    code: string
    description: string
    domain: any
    id?: number | string | null
    name: string
    users: any[]
    [key: string]: any
}

export  interface  DomainDropDown {
    name: string
    pd?: boolean
    selected?: boolean
    [key: string]: any
}

export  interface  SnackbarInterface {
    show: boolean
    text: string
    color?: string
}

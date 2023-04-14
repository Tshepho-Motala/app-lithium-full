export interface MainTab {
    displayName?: string,
    name: string,
    id: number,
    [key: string]: any
}

export interface CashierConfigUser {
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

export interface CashierConfigProfile {
    code?: string | null,
    deleted?: boolean,
    description?: string | null,
    domain?: {
        id: number,
        name: string,
        version: number,
        [key: string]: any
    },
    id: number,
    name: string,
    version?: number,
    [key: string]: any
}

export interface CashierConfigInputGroupInterface {
    [key: string]: IGInterface
}

export interface IGInterface {
    title: string,
    gValue: number | null,
    gText: string | null,
    gHint: string | null,
    gPlaceholder: string | null,
    showDomain: boolean,
    dValue: number | null,
    dText: string | null,
    dHint: string | null,
    dPlaceholder: string | null,
    showProfile: boolean,
    pValue: number | null,
    pText: string | null,
    pHint: string | null,
    pPlaceholder: string | null,
    showUser: boolean,
    uValue: number | null,
    uText: string | null,
    uHint: string | null,
    uPlaceholder: string | null,
    [key: string]: any
}

export interface StrategyOptionInterface {
    label: string,
    value: number | string
}

export interface AccessRuleInterface {
    name: string,
    id: number,
    enabled: boolean,
    domain: {
        id: number,
        name: string,
        version: number
    },
    [key: string]: any
}

export interface CashierConfigProcessor {
    accessRule?: string | null,
    accessRuleOnTranInit?: string | null,
    accountingDay?: string | number | null,
    accountingLastMonth?: string | number | null,
    accountingMonth?: string | number | null,
    accountingWeek?: string | number | null,
    active?: boolean | null,
    changelog?: {
        domainName: string,
        entityId: string | number,
        restService: any,
        reload: number,
        collapsed: boolean,
        [key: string]: any
    },
    deleted?: boolean,
    description?: string | null,
    domainLimits?: null | CashierConfigLimits,
    dmpp?: null | CashierConfigDmpp,
    dmpu?: null | CashierConfigDmpu,
    domainMethod?: null | any,
    enabled?: boolean,
    fees?: null | CashierConfigFees,
    id?: number,
    limits?: null | CashierConfigLimits,
    processor?: null | {
        code: string | null,
        deposit: boolean,
        enabled: boolean,
        fees: CashierConfigFees | null,
        id: number,
        limits: CashierConfigLimits | null,
        name: string | null,
        properties?: CashierConfigProcessorProperty[],
        url: string | null,
        version: number,
        withdraw: boolean | null,
        [key: string]: any
    },
    reserveFundsOnWithdrawal?: null | boolean
    version?: number,
    weight?: number | null,
    [key: string]: any
}

export interface CashierConfigFees {
    flat?: number | null,
    flatDec?: number | null,
    id?: number | null,
    minimum?: number | null,
    minimumDec?: number | null,
    percentage?: number | null,
    strategy?: number | null,
    version?: number | null
    [key: string]: any
}

export interface CashierConfigLimits {
    id?: number | null,
    maxAmount?: number | null,
    maxAmountDec?: number | null,
    maxAmountDay?: number | null,
    maxAmountDayDec?: number | null,
    maxAmountMonth?: number | null,
    maxAmountMonthDec?: number | null,
    maxAmountWeek?: number | null,
    maxAmountWeekDec?: number | null,
    maxTransactionsDay?: number | null,
    maxTransactionsMonth?: number | null,
    maxTransactionsWeek?: number | null,
    minAmount?: number | null,
    minAmountDec?: number | null,
    minFirstTransationAmount?: number | null,
    minFirstTransationAmountDec?: number | null,
    maxFirstTransationAmount?: number | null,
    maxFirstTransationAmountDec?: number | null,
    version?: number | null
    [key: string]: any
}

export interface CashierConfigProcessorProperty {
    id: number | null,
    override: boolean,
    processorProperty: {
        availableForClient: boolean,
        defaultValue: string | number | null,
        description: string | null,
        id: number | null,
        name: string,
        type: string | null,
        version: number,
        [key: string]: any
    },
    value: string | number | null,
    valueOrDefault: string | number | null,
    version: number | null,
    [key: string]: any
}

export interface CashierConfigComment {
    default: boolean | null,
    deposit: boolean,
    domain: {
        id: number,
        name: string,
        version: number,
        [key: string]: any
    },
    domainMethodId: number,
    fees: CashierConfigFees | null,
    limits: CashierConfigLimits | null,
    methodCode: string,
    methodId: number,
    name: string,
    priority: number,
    processor: CashierConfigProcessor,
    processors: CashierConfigProcessor[],
    properties: CashierConfigProcessorProperty[],
    [key: string]: any
}

export interface CashierConfigMethod {
    accessRule: string | null,
    accessRuleOnTranInit: string | null,
    accountingDay?: string | number | null,
    accountingLastMonth?: string | number | null,
    accountingMonth?: string | number | null,
    accountingWeek?: string | number | null,
    deleted: boolean | null,
    deposit: boolean | null,
    description: string | null,
    domain: {
        id: number,
        name: string | null,
        version: number,
        [key: string]: any
    },
    enabled: boolean | null,
    feDefault: boolean | null,
    hasDMPFees?: boolean | null,
    hasDMPLimits?: boolean | null,
    hasDMPPFees?: boolean | null,
    hasDMPPLimits?: boolean | null,
    hasDMPUFees?: boolean | null,
    hasDMPULimits?: boolean | null,
    id: number,
    image: {
        base64: string | null
        [key: string]: any
    },
    domainMethodUser?: any,
    domainMethodProfile?: any,
    method: {
        code: string | null,
        enabled: boolean | null,
        id: number,
        image: {
            base64: string | null,
            [key: string]: any
        },
        name: string | null,
        version: number,
        [key: string]: any
    },
    name: string,
    priority: number,
    version: number,
    [key: string]: any
}

export interface CashierConfigOverride {
    color: string,
    description: string,
    [key: string]: any
}

export interface CashierConfigInputChangesInterface {
    name: string,
    tab: string,
    fields: {
        gValue: number | null,
        pValue: number | null,
        uValue: number | null,
        dValue: number | null
    },
    [key: string]: any
}

export interface CashierConfigDmpu {
    domainMethodProcessor: CashierConfigProcessor | { id: number | null},
    enabled?: boolean | null,
    fees?: CashierConfigFees | null,
    id: number | null,
    limits?: CashierConfigLimits | null,
    user?: {
        guid?: string,
        id?: number | null,
        limits?: CashierConfigLimits | null,
        profile?: CashierConfigProfile | null,
        testAccount?: boolean,
        version?: number | null,
        [key: string]: any
    },
    version?: number | null,
    weight: number,
    [key: string]: any
}

export interface CashierConfigDmpp {
    domainMethodProcessor: CashierConfigProcessor | { id: number | null},
    enabled?: boolean | null,
    fees?: CashierConfigFees | null,
    id: number | null,
    limits?: CashierConfigLimits | null,
    profile?: CashierConfigProfile | { id: number | null},
    version?: number | null,
    weight: number,
    [key: string]: any
}

export interface CashierConfigChangelog {
    additionalInfo: string | null,
    author: string | null,
    authorFullName: string | null,
    authorGuid: string | null,
    categoryName: string | null,
    changeDate: number | null,
    changes: CashierConfigChangelogChange[]
    comments: any,
    dateUpdated: number | null,
    deleted: boolean,
    domainName: string | null,
    entity: string,
    entityRecordId: number | null,
    id: number | null,
    priority: number | null,
    subCategoryName: string | null,
    type: string | null,
    updatedBy: number | null,
    [key: string]: any
}
export interface CashierConfigChangelogChange {
    editedBy: string,
    field: string,
    fromValue: any,
    id: number,
    toValue: any,
    [key: string]: any
}

export interface NewMethodInterface {
    name: string,
    method: string | number,
    enabled: boolean,
    priority: number,
    [key: string]: any
}

// export interface ChangeStatusInterface {
//     status: boolean,
//     id: number | string
//     [key: string]: any
// }

export interface AddedProcessorInterface {
    code: string,
    enabled: boolean,
    id: number,
    image: {
        base64: string | null,
        [key: string]: any
    },
    inApp: any,
    name: string,
    platform: any,
    version: number,
    [key: string]: any
}

export interface NewAddedProcessorInterface {
    description: string,
    domainMethod: CashierConfigMethod,
    enabled: boolean,
    processor: {
        id: number
    },
    weight: number,
    [key: string]: any
}

export interface ProcessorTabInterface {
    type: string,
    title: string,
    roles: string,
    [key: string]: any
}
import AffiliateDropDown from "@/plugin/components/drop-down/AffiliateDropDown.vue";

export  interface  DomainDropDown {
    name: string
    pd: boolean
    selected?: boolean
    [key: string]: any
}

export  interface  TagDropDown {
    domain:string
    id: number | string | null
    name: string
    selected: boolean
    [key: string]: any
}

export  interface  RestrictionDropDown {
    domain:string
    id: number | string | null
    name: string
    selected: boolean
    [key: string]: any
}

export  interface  EcosystemDropDown {
    domain:string
    id: number | string | null
    name: string
    selected: boolean
    [key: string]: any
}

export  interface  StatusDropDown {
    id: number | string | null
    name: string
    selected: boolean
    [key: string]: any
}

export  interface  GameSupplierDropDown {
    id: number | string | null
    name: string
    selected: boolean
    [key: string]: any
}

export  interface  GameProviderDropDown {
    id: number | string | null
    name: string
    selected: boolean
    [key: string]: any
}

export  interface  GameConfigsDropDown {
    id: number | string | null
    name: string
    selected: boolean
    [key: string]: any
}

export interface RewardTypeDropDown {
    id: number | string | null
    name: string
    url: string
    rewardTypeFields: Array<any>
}

export  interface  ProvidersDropDown {
    domain: string
    id: number | string | null
    name: string
    selected: boolean
    [key: string]: any
}
export  interface AffiliateItemInterface {
    id: number
    value: string
    selected?: boolean
}

export  interface AffiliateParamsInterface {
    size: number
    name?: string
    page: number
}

import {CashierTransactionsDetailInterface} from "@/core/interface/cashier/cashierTransactionsListInterface";
import {DomainItemInterface} from "@/plugin/cms/models/DomainItem";


export default interface CashierApiListContract {
    currentPage: number
    data: CashierTransactionsDetailInterface[]
    draw: number | null
    recordsFiltered:number
    recordsTotal:number
    recordsTotalPages:number
}
export interface  CashierPaymentTypeItemInterface {
    id: number
    paymentType: string
}

export interface  CashierTransactionMethodItemInterface {
    deleted: boolean,
    deposit: boolean,
    description:string | null,
    domain: DomainItemInterface,
    enabled: boolean,
    id: number,
    method: {
        code: string
        enabled: boolean
        id: number,
        name:string,
        [key: string]: any
    }
    [key: string]: any
}


export interface  CashierTransactionProcessorsItemInterface {
    deleted: boolean,
    deposit: boolean,
    description:string | null,
    id: number,
    processor: {
        code:string | null,
        deposit: boolean,
        enabled: boolean,
        id: number,
        name:string | null,
        withdraw: boolean,
    }
    [key: string]: any
}

export interface  CashierTransactionStatusItemInterface {
    ableToApprove: boolean
    ableToHold: boolean
    active: boolean
    approved: boolean
    autoApproved: boolean
    autoApprovedDelayed: boolean
    cancelled: boolean
    code: string,
    declined: boolean
    deleted: boolean
    description: null | string
    id:number
    onHold: boolean
    playerCancelled: boolean
    start: boolean
    success: boolean
    waitForApproval: boolean
    waitForProcessor: boolean
}



export  interface CashierTransactionUserInterface {
    bonusCode?: null | number
    cellphoneNumber?: null | number
    comments?: null | string
    domain: DomainItemInterface,
    countryCode: null | string
    createdDate?: undefined | Date | string
    deleted: boolean
    emailValidated: boolean
    firstName: string
    guid: string
    id: number
    lastName: string
    telephoneNumber?: null | number
    updatedDate?: undefined | Date | string
    username: string
    [key: string]: any
}


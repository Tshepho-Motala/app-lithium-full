import {
    CashierConfigDmpp,
    CashierConfigDmpu,
    CashierConfigFees,
    CashierConfigLimits,
    CashierConfigProcessor
} from "@/core/interface/cashierConfig/CashierConfigInterface";

export default class CashierConfigProcessorMock implements CashierConfigProcessor {

    accessRule: string | null  = ""
    accessRuleOnTranInit: string | null = ""
    accountingDay: string | number | null = null
    accountingLastMonth: string | number | null = null
    accountingMonth: string | number | null = null
    accountingWeek: string | number | null = null
    active: boolean | null = null
    changelog?:any = {}
    deleted: boolean = false
    description: string | null = null
    domainLimits: null | CashierConfigLimits = null
    dmpp?: null | CashierConfigDmpp = null
    dmpu?: null | CashierConfigDmpu = null
    domainMethod: null | any = null
    enabled: boolean = false
    fees: null | CashierConfigFees = null
    id: number = 0
    limits: null | CashierConfigLimits = null
    processor: null | any = null
    reserveFundsOnWithdrawal: null | boolean = null
    version: number = 0
    weight: number = 0
}
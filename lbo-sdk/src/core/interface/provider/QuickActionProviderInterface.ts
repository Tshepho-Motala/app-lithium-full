import {CashierConfigUser} from "@/core/interface/cashierConfig/CashierConfigInterface";

export default interface QuickActionProviderInterface {
    menuItems:  {
        label: string,
        items?: {
            label: string,
            method?: () => void,
            visible?: boolean,
            items?:  {
                label: string,
                items?: {
                    label: string,
                    method?: () => void,
                    visible?: boolean,
                    items?:  {
                        label: string,
                        method?: () => void
                    }[]
                }[],
                method?: () => void,
                visible?: boolean,
            }[]
        }[],
        method?: () => void,
        visible?: boolean,
    }[]

    user: CashierConfigUser | null
}
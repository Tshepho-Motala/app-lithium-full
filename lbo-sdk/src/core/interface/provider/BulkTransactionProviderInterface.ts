import {UserInterface} from "@/core/interface/cashier/cashierTransactions";


export default interface BulkTransactionProviderInterface {
    getParams(): Promise<any>
    selectedUser: null | UserInterface
}

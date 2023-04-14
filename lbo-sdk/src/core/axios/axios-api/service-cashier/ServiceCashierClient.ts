import AxiosApiClient from '../../AxiosApiClient'
import {BulkTransactionsRequestInterface} from "@/core/interface/cashier/cashierTransactions";
import CashierApiListContract, {
    CashierPaymentTypeItemInterface,
    CashierTransactionMethodItemInterface,
    CashierTransactionProcessorsItemInterface,
    CashierTransactionStatusItemInterface
} from "@/core/interface/contract-interfaces/service-cashier/CashierApiContractInterfaces";
import {
    CashierTransactionsApiOrderInterface,
    CashierTransactionsApiParamsInterface
} from "@/core/interface/cashier/cashierTransactionsListInterface";

export default class ServiceCashierClient extends AxiosApiClient {
    localPrefix: string = 'service-cashier/'
    livePrefix: string = 'services/' + this.localPrefix

    cancelTransactionsBulkProcessing(ids: string): Promise<BulkTransactionsRequestInterface | null> {
        return this.postWithParameters(
            {
                ids: ids
            },
            'backoffice',
            'cashier',
            'transaction-bulk-processing',
            'cancel-by-id'
        )
    }

    approveTransactionsBulkProcessing(ids: string, code: string): Promise<BulkTransactionsRequestInterface | null> {
        return this.postWithParameters(
            {
                ids: ids,
                code: code
            }, 'cashier',
            'transaction-bulk-processing',
            'proceed-by-ids'
        )
    }

    cashierTransactionList(params: CashierTransactionsApiParamsInterface, paramsSort: CashierTransactionsApiOrderInterface): Promise<CashierApiListContract | null> {
        return this.postWithURLParametersAndParams(
            {
                ...paramsSort
            },
            params, 'cashier',
            'transaction',
            'search'
        )
    }

    cashierTransactionPaymentTypes(): Promise<CashierPaymentTypeItemInterface[] | null> {
        return this.getWithParameter(
            {},
            'cashier',
            'transaction',
            'paymentTypes'
        )
    }


    cashierTransactionMethods(domain: string, method: string): Promise<CashierTransactionMethodItemInterface[] | null> {
        return this.getWithParameter(
            {},
            'cashier',
            'dm', 'domain', domain, method, 'image'
        )
    }

    cashierTransactionProcessorByMethod(method: number): Promise<CashierTransactionProcessorsItemInterface[] | null> {
        return this.getWithParameter(
            {},
            'cashier',
            'dm', method.toString(), 'processors'
        )
    }

    cashierTransactionTagsList(): Promise<string[] | null> {
        return this.getWithParameter(
            {}, 'backoffice',
            'cashier',
            'transaction-tags-list'
        )
    }

    cashierTransactionStatuses(): Promise<CashierTransactionStatusItemInterface[] | null> {
        return this.getWithParameter(
            {},
            'cashier',
            'transaction', 'statuses'
        )
    }
}

import {AxiosResponse} from "axios";
import {
    approveTransactionsInterface,
    bankAccountLookupParamsInterface, bulkTransactionsInterface,
    changeStatusParamsInterface, domainFindByNameParamsInterface, getPaymentMethodsByTranIdParamsInterface,
    lastXCashierTransactionsApiParamsInterface,
    ParamsWorkflowApiInterface, paymentMethodStatusUpdateParamsInterface,
    sendTransactionRemarksParamsInterface,
    transactionCancelParamsInterface, transactionOnHoldParamsInterface, transactionsListParamsInterface,
    withdrawApprovableParamsInterface
} from "@/core/interface/cashier/cashierTransactions";
import {
    BalanceMovementListInterface,
    BalanceMovementTypeSendInterface
} from "@/core/interface/player/BalanceMovementInterface";

export default interface CashierTransactionsApiInterface {
    transactionDetail(id: Number): Promise<AxiosResponse>

    transactionData(id: Number): Promise<AxiosResponse>

    transactionLabels(id: Number): Promise<AxiosResponse>

    lastXCashierTransactions(params: lastXCashierTransactionsApiParamsInterface): Promise<AxiosResponse>

    getTransactionRemarks(id: Number): Promise<AxiosResponse>

    sendTransactionRemarksUrl(id: Number, params: sendTransactionRemarksParamsInterface): Promise<AxiosResponse>

    getWorkflow(id: Number, params: ParamsWorkflowApiInterface): Promise<AxiosResponse>

    transactionAttempt(id: Number, attempt: Number): Promise<AxiosResponse>

    transactionDataPerStage(id: Number, stage: Number): Promise<AxiosResponse>

    retryTransaction(domainName: string, id: Number): Promise<AxiosResponse>

    clearTransaction(domainName: string, id: Number): Promise<AxiosResponse>

    changeStatus(domainName: string, id: Number, status: String, params: changeStatusParamsInterface): Promise<AxiosResponse>

    withdrawApprovable(domainName: string, id: Number, params: withdrawApprovableParamsInterface): Promise<AxiosResponse>

    transactionCancel(domainName: string, id: Number, params: transactionCancelParamsInterface): Promise<AxiosResponse>

    transactionOnHold(domainName: string, id: Number, params: transactionOnHoldParamsInterface): Promise<AxiosResponse>

    getPaymentMethodsByTranId(id: number, params: getPaymentMethodsByTranIdParamsInterface): Promise<AxiosResponse>

    bankAccountLookup(params: bankAccountLookupParamsInterface): Promise<AxiosResponse>

    domainFindByName(params: domainFindByNameParamsInterface): Promise<AxiosResponse>

    domainMethodImage(domainMethodId: number): Promise<AxiosResponse>

    paymentMethodStatuses(): Promise<AxiosResponse>

    paymentMethodStatusUpdate(domainName: String, id, params: paymentMethodStatusUpdateParamsInterface): Promise<AxiosResponse>

    balanceMovementTypes(params: BalanceMovementTypeSendInterface): Promise<AxiosResponse>

    balanceMovementList(params: BalanceMovementListInterface): Promise<AxiosResponse>

    balanceMovementExl(params: BalanceMovementListInterface): Promise<AxiosResponse>

    cashierTransactionBulkList(params: transactionsListParamsInterface): Promise<AxiosResponse>

    cashierSendBulkList(params: approveTransactionsInterface): Promise<AxiosResponse>

    cancelTransactionsBulkProcessing( params: approveTransactionsInterface): Promise<AxiosResponse>

    cancelTransactionsBulkProcessing(params: approveTransactionsInterface): Promise<AxiosResponse>

    cashierTransactionBalanceAdjust( params: bulkTransactionsInterface): Promise<AxiosResponse>
}
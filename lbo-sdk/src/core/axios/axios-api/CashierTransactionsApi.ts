import {AxiosResponse} from "axios";
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import ApiListUrl from "@/core/api/ApiListUrl";
import CashierTransactionsApiInterface from "@/core/interface/axios-api/CashierTransactionsApiInterface";
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
import AxiosBasicClient from '../AxiosBasicClient';


export default class CashierTransactionsApi extends AxiosBasicClient implements CashierTransactionsApiInterface {

    // !IMPORTANT!  We need to send "userService" to this Class
    constructor(userService: UserServiceInterface) {
        super(userService)
    }


    transactionDetail(id: number): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.transactionDetailUrl(id));
    }

    transactionData(id: number): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.transactionDatalUrl(id));
    }

    transactionDataPerStage(id: number, stage: number): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.transactionDataPerStageUrl(id, stage));
    }

    transactionLabels(id: number): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.transactionLabelsUrl(id));
    }

    lastXCashierTransactions(params: lastXCashierTransactionsApiParamsInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.lastXCashierTransactionsUrl(), {params: params});
    }

    getTransactionRemarks(id: number): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.getTransactionRemarksUrl(id));
    }

    sendTransactionRemarksUrl(id: number, params: sendTransactionRemarksParamsInterface): Promise<AxiosResponse> {
        return this.provider.post(ApiListUrl.cashierApi.sendTransactionRemarksUrl(id), params);
    }

    getWorkflow(id: number, params: ParamsWorkflowApiInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.getWorkflowUrl(id), {params: params});
    }

    transactionAttempt(id: number, attempt: number): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.transactionAttemptUrl(id, attempt));
    }

    retryTransaction(domainName: string, id: number): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.retryTransactionUrl(domainName, id));
    }

    clearTransaction(domainName: string, id: number): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.clearTransactionUrl(domainName, id));
    }

    changeStatus(domainName: string, id: number, status: string, params: changeStatusParamsInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.changeStatusUrl(domainName, id, status), {params: params});
    }

    withdrawApprovable(domainName: string, id: number, params: withdrawApprovableParamsInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.withdrawApprovableUrl(domainName, id), {params: params});
    }

    transactionCancel(domainName: string, id: number, params: transactionCancelParamsInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.transactionCancelUrl(domainName, id), {params: params});
    }

    transactionOnHold(domainName: string, id: number, params: transactionOnHoldParamsInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.transactionOnHoldUrl(domainName, id), {params: params});
    }

    getPaymentMethodsByTranId(id: number, params: getPaymentMethodsByTranIdParamsInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.getPaymentMethodsByTranIdUrl(id), {params: params});
    }

    bankAccountLookup(params: bankAccountLookupParamsInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.bankAccountLookupUrl(), {params: params});
    }

    domainFindByName(params: domainFindByNameParamsInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.domainFindByNameUrl(), {params: params});
    }

    domainMethodImage(domainMethodId: number): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.domainMethodImageUrl(domainMethodId))
    }

    paymentMethodStatuses(): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.paymentMethodStatusesUrl())
    }

    paymentMethodStatusUpdate(domainName: String, id, params: paymentMethodStatusUpdateParamsInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.paymentMethodStatusUpdateUrl(domainName, id), {params: params})
    }

    balanceMovementTypes( params: BalanceMovementTypeSendInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.balanceMovementTypesUrl(), {params: params})
    }

    balanceMovementList( params: BalanceMovementListInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.balanceMovementListUrl(), {params: params})
    }

    balanceMovementExl( params: BalanceMovementListInterface): Promise<AxiosResponse> {
        return this.provider.post(ApiListUrl.cashierApi.balanceMovementExlUrl(), null,  {params: params,  responseType: 'arraybuffer'})
    }

    cashierTransactionBulkList( params: transactionsListParamsInterface): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.cashierApi.cashierTransactionBulkListUrl(),  {params: params})
    }

    cashierSendBulkList( params: approveTransactionsInterface): Promise<AxiosResponse> {
        return this.provider.post(ApiListUrl.cashierApi.cashierSendBulkListUrl(), null , {params: params} )
    }

    cancelTransactionsBulkProcessing( params: approveTransactionsInterface): Promise<AxiosResponse> {
        return this.provider.post(ApiListUrl.cashierApi.cancelTransactionsBulkProcessingUrl(), null , {params: params} )
    }

    cashierTransactionBalanceAdjust( params: bulkTransactionsInterface): Promise<AxiosResponse> {
        return this.provider.post(ApiListUrl.cashierApi.cashierTransactionBalanceAdjustUrl(), params)
    }

}

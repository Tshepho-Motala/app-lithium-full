import {AxiosResponse} from "axios";
import {
    getAllRulesetsApiParams,
    sendExportApiApiParams
} from "@/core/interface/api-params/AutoWithdrawalRulesetsApiIParamsInterface";
import {
    AutoWithdrawalItem,
    AutoWithdrawalRuleItem,
    AutoWithdrawalRulsetUpdate
} from "@/core/interface/AutoWithdrawalInterface";

export default interface AutoWithdrawalRulesetsApiInterface {
    getAllRulesets(params: getAllRulesetsApiParams): Promise<AxiosResponse>

    getAutoWithdrawal(id: Number): Promise<AxiosResponse>

    createRuleset(domainName: String, params: AutoWithdrawalItem): Promise<AxiosResponse>

    sendExportApi(params: sendExportApiApiParams): Promise<AxiosResponse>

    sendImportFile(file: File | String): Promise<AxiosResponse>

    submitImportData(params: AutoWithdrawalItem[]): Promise<AxiosResponse>

    getAutoWithdrawalChangeLog(id: Number, params: any): Promise<AxiosResponse>

    findUsersByUsernames(domainName: String, params: any): Promise<AxiosResponse>

    sendQueueprocess(domainName: String, id: Number): Promise<AxiosResponse>

    enabledAutoWithdrawalRuleset(domainName: String, id: Number): Promise<AxiosResponse>

    deleteAutoWithdrawalRuleset(domainName: String, id: Number): Promise<AxiosResponse>

    deleteRule(domainName: String, id: Number, ruleId: Number): Promise<AxiosResponse>

    updateRule(domainName: String, id: Number, ruleId: Number, params: any): Promise<AxiosResponse>

    addRule(domainName: String, id: Number, params: AutoWithdrawalRuleItem): Promise<AxiosResponse>

    updateRuleset(domainName: String, params:AutoWithdrawalRulsetUpdate): Promise<AxiosResponse>

    ruleOperatorData(domainName: String, ruleId: Number): Promise<AxiosResponse>

    ruleFieldsDataUrl(): Promise<AxiosResponse>

    ruleOperatorsList(): Promise<AxiosResponse>
}
import { AxiosResponse } from 'axios'
import UserServiceInterface from '@/core/interface/service/UserServiceInterface'
import AutoWithdrawalRulesetsApiInterface from '@/core/interface/axios-api/AutoWithdrawalRulesetsApiInterface'
import ApiListUrl from '@/core/api/ApiListUrl'
import { getAllRulesetsApiParams, sendExportApiApiParams } from '@/core/interface/api-params/AutoWithdrawalRulesetsApiIParamsInterface'
import { AutoWithdrawalItem, AutoWithdrawalRuleItem, AutoWithdrawalRulsetUpdate } from '@/core/interface/AutoWithdrawalInterface'
import AxiosBasicClient from '../AxiosBasicClient'

interface DataObjectTest {}

export default class AutoWithdrawalRulesetsApiService extends AxiosBasicClient implements AutoWithdrawalRulesetsApiInterface {
  // !IMPORTANT!  We need to send "userService" to Class  "AxiosApiClient"
  constructor(userService: UserServiceInterface) {
    super(userService)
  }

  getAllRulesets(params: getAllRulesetsApiParams): Promise<AxiosResponse> {
    return this.provider.get(ApiListUrl.cashierApi.getAllRulesetsUrl(), { params: params })
  }

  createRuleset(domainName: String, params: AutoWithdrawalItem): Promise<AxiosResponse> {
    return this.provider.post(ApiListUrl.cashierApi.createRulesetUrl(domainName), params)
  }

  sendExportApi(params: sendExportApiApiParams): Promise<AxiosResponse> {
    return this.provider.get(ApiListUrl.cashierApi.sendExportApiUrl, { params: params })
  }

  getAutoWithdrawal(id: Number): Promise<AxiosResponse> {
    return this.provider.get(ApiListUrl.cashierApi.getAutoWithdrawalUrl(id))
  }

  sendImportFile(file: File | string) {
    let formData: FormData = new FormData()
    formData.append('file', file)
    return this.provider.post(ApiListUrl.cashierApi.sendImportFileUrl, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }

  submitImportData(params: AutoWithdrawalItem[]): Promise<AxiosResponse> {
    return this.provider.post(ApiListUrl.cashierApi.submitImportDataUrl, params)
  }

  getAutoWithdrawalChangeLog(id: Number, params: any): Promise<AxiosResponse> {
    return this.provider.get(ApiListUrl.cashierApi.getAutoWithdrawalChangeLogUrl(id), { params: params })
  }

  findUsersByUsernames(domainName: String, params: any): Promise<AxiosResponse> {
    return this.provider.post(ApiListUrl.cashierApi.findUsersByUsernamesUrl(domainName), params)
  }

  sendQueueprocess(domainName: String, id: Number): Promise<AxiosResponse> {
    return this.provider.post(ApiListUrl.cashierApi.queueprocessUrl(domainName, id))
  }

  enabledAutoWithdrawalRuleset(domainName: String, id: Number): Promise<AxiosResponse> {
    return this.provider.post(ApiListUrl.cashierApi.enabledAutoWithdrawalRulesetUrl(domainName, id))
  }

  deleteAutoWithdrawalRuleset(domainName: String, id: Number): Promise<AxiosResponse> {
    return this.provider.post(ApiListUrl.cashierApi.deleteAutoWithdrawalRulesetUrl(domainName, id))
  }

  deleteRule(domainName: String, id: Number, ruleId: Number): Promise<AxiosResponse> {
    return this.provider.post(ApiListUrl.cashierApi.deleteRuleUrl(domainName, id, ruleId))
  }

  updateRule(domainName: String, id: Number, ruleId: Number, params: AutoWithdrawalRuleItem): Promise<AxiosResponse> {
    return this.provider.post(ApiListUrl.cashierApi.updateRuleUrl(domainName, id, ruleId), params)
  }

  addRule(domainName: String, id: Number, params: AutoWithdrawalRuleItem): Promise<AxiosResponse> {
    return this.provider.post(ApiListUrl.cashierApi.addRuleUrl(domainName, id), params)
  }

  updateRuleset(domainName: String, params: AutoWithdrawalRulsetUpdate): Promise<AxiosResponse> {
    return this.provider.post(ApiListUrl.cashierApi.updateRulesetUrl(domainName), params)
  }

  ruleOperatorData(domainName: String, ruleId: Number): Promise<AxiosResponse> {
    return this.provider.get(ApiListUrl.cashierApi.ruleOperatorDataUrl(domainName, ruleId))
  }

  ruleFieldsDataUrl(): Promise<AxiosResponse> {
    return this.provider.get(ApiListUrl.cashierApi.ruleFieldsDataUrl())
  }

  ruleOperatorsList(): Promise<AxiosResponse> {
    return this.provider.get(ApiListUrl.cashierApi.ruleOperatorsListUrl())
  }
}

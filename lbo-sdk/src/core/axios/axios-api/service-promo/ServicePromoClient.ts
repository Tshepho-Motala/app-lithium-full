import { PromotionListContract } from '@/core/interface/contract-interfaces/service-promo/PromotionContract'
import ProviderCategoryContract, { ProviderCategoryListContract } from '@/core/interface/contract-interfaces/service-promo/ProviderCategoryContract'
import PromotionProviderContract, {
  PromotionProviderListContract
} from '@/core/interface/contract-interfaces/service-promo/PromotionProviderContract'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import AxiosApiClient from '../../AxiosApiClient'
import { FieldValueListContract } from '@/core/interface/contract-interfaces/service-promo/FieldValueContract'
import PromotionDraftContract, {
  PromotionDraftListContract,
  PromotionEditDraftContract
} from '@/core/interface/contract-interfaces/service-promo/PromotionDraftContract'
import { PromotionChallengeProviderContract } from '@/core/interface/contract-interfaces/service-promo/PromotionChallengeContract'
import { TableContract } from '../generic/TableContract'

export default class ServicePromoClient extends AxiosApiClient {
  localPrefix: string = 'service-promo/backoffice/'
  livePrefix: string = 'services/service-promo/backoffice/'

  // Removing this as we need to only be using versions
  // createPromotion(contract: PromotionContract) {
  //   return this.postJson(contract, 'promotions', 'create')
  // }

  getPromotion(promotionId: string): Promise<PromotionDraftContract | null> {
    return this.get('promotion', 'v1', promotionId)
  }

  createPromotionDraft(contract: PromotionEditDraftContract): Promise<PromotionDraftContract | null> {
    return this.postJson(contract, 'promotion', 'v1', 'create-draft')
  }

  toggleEnabled(promotionId: string, enabled: string) {
    return this.postWithParameters(
      {
        enabled
      },
      'promotion',
      'v1',
      'toggle-enabled',
      promotionId
    )
  }

  editPromotionDraft(contract: PromotionEditDraftContract): Promise<PromotionDraftContract | null> {
    return this.postJson(contract, 'promotion', 'v1', 'edit-draft')
  }

  publishPromotionDraft(promotionId: string) {
    return this.post({}, 'promotion', 'v1', 'mark-draft-final', promotionId)
  }

  getPromotionTable(domains: DomainItemInterface[], start: string, length: string): Promise<TableContract<PromotionDraftContract> | null> {
    const domainString = domains.map((d) => d.name).join(',')
    return this.getWithParameter(
      {
        domains: domainString,
        draw: '0',
        start,
        length
      },
      'promotions',
      'table'
    )
  }

  getPromotionsBetween(domains: DomainItemInterface[], startDate: string, endDate: string): Promise<PromotionDraftListContract | null> {
    const domainString = domains.map((d) => d.name).join(',')
    return this.getWithParameter(
      {
        domains: domainString,
        startDate,
        endDate
      },
      'promotions',
      'get-promotions-with-events-in-period'
    )
  }

  getProviderCategories(domain: DomainItemInterface): Promise<ProviderCategoryListContract | null> {
    return this.get(domain.name, 'provider', 'categories')
  }

  async getProviders(domain: DomainItemInterface): Promise<PromotionProviderListContract | null> {
    return this.get(domain.name, 'provider', 'list')
  }

  async getProvidersByCategory(domain: DomainItemInterface, category: ProviderCategoryContract): Promise<PromotionProviderListContract | null> {
    return this.get(domain.name, 'provider', 'list-by-category', category.name)
  }

  async getProviderFieldValues(
    domain: DomainItemInterface,
    provider: PromotionProviderContract | PromotionChallengeProviderContract,
    fieldName: string
  ): Promise<FieldValueListContract | null> {
    return this.get(domain.name, 'provider', provider.url, fieldName)
  }

  getOperations(): Promise<string[] | null> {
    return this.get('promotions', 'rule', 'operations')
  }
  //Get a table for disable Promtions
  getPromotionDisableTable(domains: DomainItemInterface[], startDate: string): Promise<PromotionDraftListContract | null> {
    const domainString = domains.map((d) => d.name).join(',')
    return this.postJson(
      {
        domains: [domainString],
        startDate
      },
      'promotion',
      'v1',
      'get-disabled-promotions-between-dates'
    )
  }

  deletePromotion(promotionId: string): Promise<void | null> {
    return this.delete('promotion', 'v1', 'delete', promotionId)
  }
}

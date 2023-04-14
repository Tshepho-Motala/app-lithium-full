import PromotionContract from '@/core/interface/contract-interfaces/service-promo/PromotionContract'
import PromotionDraftContract, { PromotionEditor } from '@/core/interface/contract-interfaces/service-promo/PromotionDraftContract'
import { nanoid } from 'nanoid'

export default class PromotionDraft {
  id = nanoid()

  constructor(public editor: PromotionEditor, public current: PromotionContract | null = null, public edit: PromotionContract | null = null) {}

  static fromContract(contract: PromotionDraftContract): PromotionDraft {
    const item = new PromotionDraft(contract.editor, contract.current, contract.edit)
    item.id = contract.id.toString()
    return item
  }

  toContract(): PromotionDraftContract {
    return {
      ...this,
      id: parseInt(this.id)
    }
  }
}

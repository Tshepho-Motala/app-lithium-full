import PromotionContract from './PromotionContract'

export interface PromotionDraftListContract extends Array<PromotionDraftContract> {}

export default interface PromotionDraftContract {
  id: number
  editor: PromotionEditor
  current: PromotionContract | null
  edit: PromotionContract | null
}

export interface PromotionEditDraftContract {
  /**
   * ID is optional for CREATE
   * ID is required for EDIT
   */
  id?: number
  edit: PromotionContract
}

export interface PromotionEditor {
  id: number | null
  version: number
  guid: string
}

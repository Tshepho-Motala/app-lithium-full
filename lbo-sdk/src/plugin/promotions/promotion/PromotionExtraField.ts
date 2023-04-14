import ExtraFieldContract from '@/core/interface/contract-interfaces/service-promo/ExtraFieldContract'
import { nanoid } from 'nanoid'

export class ExtraField {
  id = nanoid()
  referenceValue: null | string[] = null

  constructor(
    public name: string,
    public dataType: string,
    public description: string | null,
    public fieldType: string,
    public fetchExternalData: boolean,
    public required: boolean
  ) {}

  static fromContract(contract: ExtraFieldContract): ExtraField {
    const item = new this(contract.name, contract.dataType, contract.description, contract.fieldType, contract.fetchExternalData, contract.required)
    item.id = contract.id.toString()
    return item
  }

  toContract(): ExtraFieldContract {
    return {
      ...this,
      id: parseInt(this.id)
    }
  }
}

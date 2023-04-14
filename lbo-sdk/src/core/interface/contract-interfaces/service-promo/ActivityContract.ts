import ExtraFieldContract from "./ExtraFieldContract"

export default interface ActivityContract {
  id: number
  promoProvider: number
  name: string
  requiresValue: boolean
  requiresAllRules: boolean
  extraFields: ExtraFieldContract[]
}

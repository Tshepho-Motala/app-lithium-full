export default interface ExtraFieldContract {
  id: number
  name: string
  dataType: string // "number" | "string"
  description: string | null
  fieldType: string // 'input' | 'singleselect' | 'multiselect'
  fetchExternalData: boolean
  required: boolean
}

export interface ExtraFieldValueContract {
  id: number
  value: string[] // This is always an array
  activityExtraField: ExtraFieldContract
}

export interface ExtraFieldValueListContract extends Array<ExtraFieldValueContract> {}

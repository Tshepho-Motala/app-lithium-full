export default interface FieldValueContract {
  // [key: string]: string
  label: string
  value: string
}

export interface FieldValueListContract extends Array<FieldValueContract> {}

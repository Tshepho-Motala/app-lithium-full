export interface TableContract<T = any> {
  data: T[]
  draw: string
  recordsTotal: number
  recordsFiltered: number
}
// TODO PROM Move this to interfaces
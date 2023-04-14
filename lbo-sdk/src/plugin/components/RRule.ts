export interface RRuleContract {
  rruleString: string
  lengthInDays: string

  dateStart: Date | null
  dateUntil: Date | null
}

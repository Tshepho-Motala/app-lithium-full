import { Promotion } from '../Promotion'

export interface ScheduleEventInterface {
  name: string
  start: Date
  end?: Date
  color?: string
  timed?: boolean
  startDateFormatted?: string
  timezone?: string
  promotion?: Promotion

  [key: string]: any
}

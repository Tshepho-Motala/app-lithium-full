import {customAlphabet, nanoid} from "nanoid"
import {RRule} from "rrule";


class Schedule {
  id: string = nanoid()

  rruleString = ''
  lengthInDays = 1

  dateStart: Date | null = null
  dateUntil: Date | null = null

  singleDay: boolean = true

  setDetailsByRrule() {
    if (!this.rruleString) {
      return
    }

    const rrule = RRule.fromString(this.rruleString)
    this.dateStart = rrule.options.dtstart
    this.dateUntil = rrule.options.until
    this.singleDay = rrule.options.count === null || rrule.options.count <= 1

    return rrule
  }
}

export default class LayoutBannerItem {
  id: number;
  name: string
  url = ''
  image = ''
  display_text = ''
  from = ''
  to = ''
  gameID = ''
  terms_url = ''
  runcount = 0
  schedule: Schedule = new Schedule();

  constructor(name: string,image: string) {
    this.name = name
    this.image = image
    const nanoid = customAlphabet('0123456789', 10)
    this.id = Number(nanoid());
  }
}

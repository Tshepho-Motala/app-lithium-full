<template>
  <v-row>
    <v-col cols="12">
      <v-alert type="info"
               text
               dismissible>
        <span> Tap a date on the calendar or manually type in a start date </span>
      </v-alert>
    </v-col>

    <v-col cols="5">
      <v-sheet>
        <v-toolbar flat
                   dense
                   dark
                   color="primary"
                   class="px-0 mx-0">
          <v-btn outlined
                 class="mr-4"
                 @click="calendarValue = ''"> Today </v-btn>

          <v-btn icon
                 plain
                 rounded
                 @click="$refs.calendar.prev()">
            <v-icon>mdi-chevron-left</v-icon>
          </v-btn>

          <v-btn icon
                 plain
                 rounded
                 @click="$refs.calendar.next()">
            <v-icon>mdi-chevron-right</v-icon>
          </v-btn>

          <v-spacer></v-spacer>

          <v-toolbar-title v-if="$refs.calendar">
            {{ $refs.calendar.title }}
          </v-toolbar-title>
        </v-toolbar>
        <v-calendar ref="calendar"
                    v-model="calendarValue"
                    :events="events"
                    @change="onCalendarChange"
                    @click:day="onCalendarDayClick"
                    @click:date="onCalendarDayClick"
                    :event-height="5"
                    event-name=""></v-calendar>
      </v-sheet>
      <v-form lazy-validation
              ref="form">
        <v-row class="py-4">
          <v-col cols="6">
            <div class="d-flex"
                 style="gap: 10px">
              <div>
                <v-menu v-model="dateStartMenu"
                        :close-on-content-click="false"
                        :nudge-right="40"
                        transition="scale-transition"
                        offset-y
                        min-width="auto">
                  <template v-slot:activator="{ on, attrs }">
                    <v-text-field :success="settingDateStart"
                                  clearable
                                  v-model="dateStart"
                                  label="Start Date *"
                                  hint="When does this event start?"
                                  persistent-hint
                                  prepend-inner-icon="mdi-calendar"
                                  @input="visualiseEvent"
                                  @click:clear="onClearStartDate"
                                  v-bind="attrs"
                                  v-on="on"
                                  outlined
                                  readonly
                                  :rules="rules.required"></v-text-field>
                  </template>
                  <v-date-picker v-model="dateStart"
                                 @input="onStartDateUpdate"></v-date-picker>
                </v-menu>
              </div>
            </div>
          </v-col>
          <v-col cols="6">
            <div class="d-flex"
                 style="gap: 10px">
              <div>
                <v-menu :close-on-content-click="false"
                        :nudge-right="40"
                        transition="scale-transition"
                        offset-y
                        min-width="auto">
                  <template v-slot:activator="{ on }">
                    <v-text-field v-model="timeStart"
                                  label="Start Time"
                                  hint="What time does this event start?"
                                  prepend-inner-icon="mdi-clock"
                                  @input="visualiseEvent"
                                  persistent-hint
                                  outlined
                                  v-on="on"
                                  readonly
                                  clearable></v-text-field>
                  </template>
                  <v-time-picker @input="visualiseEvent"
                                 v-model="timeStart"
                                 ampm-in-title
                                 format="24hr"
                                 type="time"></v-time-picker>
                </v-menu>
              </div>
            </div>
          </v-col>

          <v-col cols="6">
            <v-text-field :success="settingDateEnd"
                          @input="visualiseEvent"
                          outlined
                          type="number"
                          :disabled="!dateStart"
                          v-model="eventLengthInDays"
                          label="Length of promotion in days *"
                          hint="How long does this promotion last?"
                          persistent-hint
                          :rules="rules.gtZero"></v-text-field>
          </v-col>

          <v-col cols="6">
            <v-select label="Repeat frequency"
                      hint="Select how often the event repeats."
                      persistent-hint
                      :items="frequencyList"
                      v-model="frequencySelected"
                      item-text="label"
                      item-value="value"
                      return-object
                      outlined
                      @change="visualiseEvent">
            </v-select>
          </v-col>

          <v-col cols="6">
            <v-text-field @input="visualiseEvent"
                          outlined
                          type="number"
                          label="Repeat amount *"
                          v-model="occurrences"
                          hint="The amount of times this event will repeat."
                          :disabled="dateSingleDay"
                          persistent-hint
                          :rules="rules.gtZero"></v-text-field>
          </v-col>
          <v-col cols="6"
                 v-if="!!frequencySelected">
            <v-text-field @input="visualiseEvent"
                          outlined
                          type="number"
                          label="Delay between repeating *"
                          v-model="interval"
                          hint="The amount of time to wait before the event repeats."
                          persistent-hint
                          :disabled="dateSingleDay"
                          :suffix="frequencySelected.suffix || ''"
                          :rules="rules.gtZero"></v-text-field>
          </v-col>
        </v-row>
      </v-form>
    </v-col>

    <v-col cols="7">
      <v-toolbar flat
                 dense
                 dark
                 color="primary"
                 class="px-0 mx-0"> Schedule Breakdown </v-toolbar>
      <v-card style="width: 100%"
              class="text-center pa-4"
              outlined
              v-if="events.length === 0">
        <span class="text-caption grey--text"> Select a date </span>
      </v-card>
      <v-card v-else
              outlined>
        <v-virtual-scroll :items="events"
                          :height="virtualHeight"
                          item-height="60"
                          class="pt-2"
                          bench="2">
          <template #default="{ index, item }">
            <v-list-item :key="index">
              <div style="width: 100%"
                   class="pt-2">
                <div class="d-flex justify-space-between align-center">
                  <div>
                    <span v-if="item.name"
                          v-text="item.name"></span>
                    <span v-else
                          class="text-caption grey--text">- No name -</span>
                  </div>
                  <div class="d-flex flex-row justify-start"
                       style="gap: 40px">
                    <div>
                      <div>
                        <span class="text-caption grey--text"> From </span>
                      </div>
                      <div>
                        <span v-text="item.start.toLocaleDateString('en-CA')"></span>
                      </div>
                    </div>
                    <div>
                      <div>
                        <span class="text-caption grey--text"> To </span>
                      </div>
                      <div>
                        <span v-text="item.end.toLocaleDateString('en-CA')"></span>
                      </div>
                    </div>
                  </div>
                </div>
                <v-divider class="my-2"
                           v-if="index < events.length - 1"></v-divider>
              </div>
            </v-list-item>
          </template>
        </v-virtual-scroll>
      </v-card>
    </v-col>
</v-row>
</template>

<script lang='ts'>
import { Mixins, Component, Prop } from 'vue-property-decorator'
import { RRule, Frequency, rrulestr } from 'rrule'
import { ALL_WEEKDAYS } from 'rrule/dist/esm/weekday'

import TimeSelector from '@/plugin/components/TimeSelector.vue'
import { ScheduleEventInterface } from '../promotions/schedule/ScheduleEventInterface'
import { RRuleContract } from './RRule'
import differenceInDays from 'date-fns/differenceInDays/index'
import { addYears, isAfter, format } from 'date-fns'
import { addDays } from 'date-fns/esm'
import { } from 'date-fns'
import RulesMixin from '../mixins/RulesMixin'

import { formatInTimeZone } from 'date-fns-tz'

/**
 * Future developer:
 *
 * When creating RRULEs, we do not consider the LENGTH of the event, merely
 * when it STARTS and what the repeat schedule is like.
 *
 * This means the end date is not the end date of a SINGLE event which will
 * be replicated, but rather the end date as a WHOLE, which will end in a
 * results of START dates that fit between the BEGIN and END
 *
 * That start date can then have a "lasts" amount on, showing how long that
 * event will last after the date of commence
 */
@Component({
  components: {
    TimeSelector
  }
})
export default class RRuleComponent extends Mixins(RulesMixin) {
  @Prop({ default: 'Promotion' }) readonly eventTitle!: string
  @Prop({ default: '' }) readonly color!: string
  @Prop({ default: '' }) readonly timezone!: string
  @Prop({ default: null }) readonly schedule!: RRuleContract | null

  calendarStart: null | Date = null
  calendarEnd: null | Date = null
  calendarValue: string = ''
  events: ScheduleEventInterface[] = []

  frequencySelected: {
    label: string
    value: number
    enum: Frequency | null
    suffix: string
  } | null = null

  frequencyList = [
    { label: 'Does not repeat', value: -1, enum: null, suffix: '' },
    { label: 'Daily', value: 3, enum: Frequency.DAILY, suffix: 'day' },
    { label: 'Weekly', value: 2, enum: Frequency.WEEKLY, suffix: 'week' },
    { label: 'Monthly', value: 1, enum: Frequency.MONTHLY, suffix: 'month' },
    { label: 'Yearly', value: 0, enum: Frequency.YEARLY, suffix: 'year' }
    // { label: 'Hourly', value: 4, enum: Frequency.HOURLY },
    // { label: 'Minutely', value: 5, enum: Frequency.MINUTELY },
    // { label: 'Secondly', value: 6, enum: Frequency.SECONDLY }
  ]

  dateStartMenu = false
  dateStart: string = ''
  dateEnd: string | null = null // AKA until

  timeStart: string = ''
  eventLengthInDays = 1
  occurrences: string = '1' // AKA count


  /**
  * <div class="help">The interval between each freq iteration. For example, when using
      <code>RRule.YEARLY</code>, an interval of
      <code>2</code> means once every two years, but with
      <code>RRule.HOURLY</code>, it means once every two hours. The default interval is
      <code>1</code>.
    </div>
  */
  interval: string = '1'

  timezoneSelected = 'Etc/Greenwich' // Default to center
  // Timezone List: https://stackoverflow.com/questions/39263321/javascript-get-html-timezone-dropdown

  weekStartDaySelected = ''
  get weekStartDayList() {
    return ALL_WEEKDAYS
  }

  /**
   * <div class="help">If given, it must be either an integer (
                        <code>0 == RRule.MO</code>), a sequence of integers, one of the weekday constants (
                        <code>RRule.MO</code>,
                        <code>RRule.TU</code>, etc), or a sequence of these constants. When given, these variables will define
                        the weekdays where the recurrence will be applied. It's also possible to use an argument n for the
                        weekday instances, which will mean the nth occurrence of this weekday in the period. For example,
                        with
                        <code>RRule.MONTHLY</code>, or with
                        <code>RRule.YEARLY</code> and
                        <code>BYMONTH</code>, using
                        <code>RRule.FR.clone(+1)</code> in
                        <code>byweekday</code> will specify the first friday of the month where the recurrence happens. Notice
                        that the RFC documentation, this is specified as
                        <code>BYDAY</code>, but was renamed to avoid the ambiguity of that argument.
                      </div>
   */
  whitelistWeekdaySelected = []
  get whitelistWeekdayList() {
    return ALL_WEEKDAYS
  }

  settingDateStart = true
  settingDateEnd = false
  hasSetFrequency = false

  get calculatedDateEnd(): Date | null {
    if (!this.dateEnd || !this.dateStart) {
      return null
    }

    const st = new Date(this.dateStart)
    return addDays(st, this.eventLengthInDays)
  }

  get virtualHeight(): number {
    const maxItems = 5
    const itemHeight = 60
    const buffer = 8

    const maxHeight = maxItems * itemHeight
    const dynamicHeight = this.events.length * itemHeight
    const useDynamicHeight = this.events.length < maxItems

    const height = useDynamicHeight ? dynamicHeight : maxHeight

    return height + buffer
  }

  get dateSingleDay(): boolean {
    if (this.frequencySelected) {
      if (this.frequencySelected.value > -1) {
        return false
      }
    }
    return true
  }

  created() {
    this.frequencySelected = this.frequencyList[0]

    this.timeStart = '00:00'
    if (this.schedule !== null) {
      if (this.schedule.dateStart) {

        this.dateStart = formatInTimeZone(this.schedule.dateStart, this.timezone, 'yyyy-MM-d')
        this.timeStart = formatInTimeZone(this.schedule.dateStart, this.timezone, 'HH:mm')

        this.calendarValue = this.dateStart
      }

      if (this.schedule.dateUntil) {
        this.dateEnd = this.schedule.dateUntil.toLocaleDateString('en-CA')
      }
      this.eventLengthInDays = parseInt(this.schedule.lengthInDays)

      if (this.schedule.rruleString) {
        const obj: RRule = rrulestr(this.schedule.rruleString)

        const { count, freq, interval } = obj.origOptions

        this.occurrences = count?.toString() || ''
        this.interval = interval?.toString() || ''
        this.frequencySelected = this.frequencyList.find((x) => x.enum === freq) || this.frequencyList[0]
      }
    }
  }

  async onCalendarChange({ start, end, title }) {
    this.calendarStart = new Date(start.year, start.month - 1, start.day)
    this.calendarEnd = new Date(end.year, end.month - 1, end.day + 1)
    this.visualiseEvent()
  }

  onStartDateUpdate(date: string | undefined) {
    if (date) {
      this.dateStart = date
    }

    // After setting start date, let next calendar click be end date
    this.settingDateStart = false

    // Check if we need to update the end date after
    let requiresEndSet = true
    if (this.dateEnd) {
      const newDate = addDays(new Date(this.dateStart), this.eventLengthInDays - 1)
      this.dateEnd = newDate.toLocaleDateString('en-CA')

      requiresEndSet = false
    }

    this.settingDateEnd = requiresEndSet

    this.visualiseEvent()
  }

  onEndDateUpdate(date: string | undefined) {
    this.dateEnd = date || null

    // After setting end date, we assume we are done
    this.settingDateStart = false
    this.settingDateEnd = false

    if (date) {
      this.eventLengthInDays = differenceInDays(new Date(date), new Date(this.dateStart)) + 1
    }

    this.visualiseEvent()
  }

  onCalendarDayClick({ date }) {
    if (this.settingDateStart) {
      this.onStartDateUpdate(date)
    } else if (this.settingDateEnd) {
      this.onEndDateUpdate(date)
    } else {
      this.onStartDateUpdate(date)
    }
  }

  onClearStartDate() {
    this.dateStart = ''
    this.settingDateStart = true
    this.settingDateEnd = false
  }

  // Visualisation 1 : The entire PROM length
  // Visualisation 2 : The events
  visualiseEvent() {
    this.events = []

    // this.visualiseRRuleContainer()
    this.visualiseIndividualEvents()
  }

  private visualiseIndividualEvents() {
    if (!this.dateStart) {
      return
    }

    let freq = Frequency.DAILY
    let singleDay: boolean = true
    if (this.frequencySelected && this.frequencySelected.enum) {
      freq = this.frequencySelected.enum
      singleDay = this.frequencySelected.value === -1
    }

    // if (singleDay) {
    //   // We dont care about single days
    //   return
    // }

    if (!this.hasSetFrequency) {
      // If the frequency was not selected, then change event days down to one
      this.hasSetFrequency = true
    }

    const interval = Number.parseInt(this.interval) || 1
    let count = Number.parseInt(this.occurrences)
    if (!count || count < 0) {
      count = 1
    }

    let dtstart = new Date(this.dateStart)

    if (this.timeStart) {
      dtstart = new Date(`${this.dateStart} ${this.timeStart}:00`);
    }

    // const until = this.dateEnd ? new Date(this.dateEnd) : undefined
    // const hasEndDate = !this.dateEnd
    const rule = new RRule({
      dtstart,
      // until,
      freq,
      // tzid: this.timezoneSelected,
      interval,
      count
    })
    this.$emit('change', { rule, length: this.eventLengthInDays, singleDay, start: dtstart, until: null })

    // Get all the events between these two dates

    if (this.calendarStart && this.calendarEnd) {
      const eventStartDates = rule.between(addYears(new Date(), -50), addYears(new Date(), 50))
      for (const startDate of eventStartDates) {
        const end = new Date(startDate)
        const modifier = this.eventLengthInDays - 1 // -1 because 1 day is a 0 addition
        end.setDate(end.getDate() + modifier)

        this.events.push({
          start: startDate,
          end,
          name: this.eventTitle,
          color: this.color
        })
      }
    }
  }
}
</script>

<style scoped></style>
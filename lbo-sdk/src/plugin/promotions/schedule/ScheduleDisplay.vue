<template>
  <v-row>
    <!-- <v-col cols="12">
      {{ events }}
    </v-col> -->
    <v-col cols="12">
      <ScheduleCalendar ref="calendar" :events="events" @change="onCalendarChange" @dayClick="onCreatePromotionClick"
        @dateClick="onCreatePromotionClick" @createClick="onCreatePromotionClick" @edit="onEdit" />
    </v-col>
    <v-col cols="12">
      <v-card>
        <v-sheet color="grey lighten-4 rounded px-4 py-5 d-flex">
          <div>
            <span class="text-h5" style="color: #222">Scheduled Promotions for {{ calendarTitle }}</span>
          </div>
          <v-spacer></v-spacer>
          <div>
            <v-btn outlined @click="() => onCreatePromotionClick()">Create New Promotion</v-btn>
          </div>
        </v-sheet>
        <ScheduleList :promotions="promotions" :events="events" @edit="onEdit" @disable="onDisable" />
      </v-card>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import { RRule } from 'rrule'
import { Vue, Component, Prop, Watch } from 'vue-property-decorator'
import { Promotion } from '../Promotion'
import { ScheduleEventInterface } from './ScheduleEventInterface'

import ScheduleCalendar from './ScheduleCalendar.vue'
import ScheduleList from './ScheduleList.vue'
import { subHours } from 'date-fns'
import { toSimpleDateFormat, toDateTimeFormat, toFormatForZone } from '@/core/utils/dateUtils'

@Component({
  components: {
    ScheduleCalendar,
    ScheduleList
  }
})
export default class ScheduleDisplay extends Vue {
  @Prop({ required: true }) readonly promotions!: Promotion[]

  calendarStart: null | Date = null
  calendarEnd: null | Date = null

  events: ScheduleEventInterface[] = []

  calendarTitle = ''

  @Watch('promotions')
  getEventsForMonth() {
    this.events = []

    const sortThisListOfEvents: ScheduleEventInterface[] = []

    if (this.calendarStart && this.calendarEnd) {
      // Buffer for DST by 1h before start (end is not needed)
      // https://www.timeanddate.com/time/dst/
      // https://en.wikipedia.org/wiki/Daylight_saving_time_by_country
      // Tested on UTC+00 (DST)
      // Tested on UTC+-2 (no DST)
      // This manual workaround is due to JS having no idea about DST, even in the Intl lib
      const bufferStart = subHours(this.calendarStart, 1)

      for (const promotion of this.promotions) {
        const rrule = RRule.fromString(promotion.schedule.rruleString)
        const eventStartDates = rrule.between(bufferStart, this.calendarEnd)

        for (const startDate of eventStartDates) {
          const lengthInDays = parseInt(promotion.schedule.lengthInDays) || 1
          const end = new Date(startDate)
          const modifier = lengthInDays - 1 // -1 because 1 day is a 0 addition
          end.setDate(end.getDate() + modifier)

          const dateStartString = toSimpleDateFormat(startDate)
          let timeStartString = toDateTimeFormat(startDate, 'HH:mm z')

          if (promotion.schedule.dateStart) {
            timeStartString = toFormatForZone(promotion.schedule.dateStartString!, "HH:mm", promotion.domain?.timezone || 'Etc/Greenwich')
          }


          sortThisListOfEvents.push({
            start: startDate,
            end,
            name: promotion.title,
            color: promotion.theme.color,
            startDateFormatted: `${dateStartString} ${timeStartString}`,
            promotion,
            timezone: promotion.domain?.timezone || 'Etc/Greenwich'
          })
        }
      }
    }

    sortThisListOfEvents.sort((a, b) => a.start.getTime() - b.start.getTime())
    this.events = sortThisListOfEvents
  }

  async onCalendarChange({ start, end, title }) {
    this.calendarTitle = title
    this.calendarStart = new Date(start.year, start.month - 1, start.day)
    this.calendarEnd = new Date(end.year, end.month - 1, end.day + 1)

    this.$emit('calendarChange', { start, end })

    // this.getEventsForMonth()
  }

  onCreatePromotionClick(date?: any) {
    this.$emit('create', date)
  }

  onEdit(event: ScheduleEventInterface) {
    this.$emit('edit', event.promotion)
  }

  onDisable(event: ScheduleEventInterface) {
    this.$emit('disable', event.promotion)
  }
}
</script>

<style>
.v-event {
  margin-left: 5px;
}
</style>
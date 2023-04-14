<template>
  <v-row>
    <v-col cols="12" class="pb-0 px-0">
      <v-toolbar flat dense class="px-0 mx-0">
        <v-btn outlined class="mr-4" color="grey darken-2" @click="calToday"> Today </v-btn>

        <v-btn icon plain rounded @click="calPrev">
          <v-icon>mdi-chevron-left</v-icon>
        </v-btn>

        <v-btn icon plain rounded @click="calNext">
          <v-icon>mdi-chevron-right</v-icon>
        </v-btn>

        <v-toolbar-title v-if="$refs.calendar">
          {{ title }}
        </v-toolbar-title>
      </v-toolbar>
    </v-col>

    <v-col cols="12" class="pt-0">
      <v-sheet height="600">
        <v-calendar
          ref="calendar"
          v-model="calendarValue"
          :events="events"
          @change="onChange"
          @click:day="onDayClick"
          @click:date="onDateClick"
          @click:event="onEventClick"
          style="padding-right: 1px"
        >
          <template v-slot:day="args">
            <v-hover v-slot="{ hover }">
              <div @click="(event) => onCreateClick(args, event)" class="px-2 pb-1 cal-new-clicker-holder" style="cursor: pointer">
                <div v-if="hover" class="cal-new-clicker rounded fill-height d-flex">
                  <v-icon left small color="grey">mdi-plus</v-icon>
                  <span class="grey--text">New Promotion</span>
                </div>
              </div>
            </v-hover>
          </template>

          <!-- <template v-slot:day-label>d-lab</template> -->
          <template v-slot:day-month>d-m</template>
        </v-calendar>
      </v-sheet>

      <v-menu v-model="infoDisplayOpen" :close-on-content-click="false" :activator="selectedElement" offset-x>
        <v-card color="grey lighten-4" width="350px" flat>
          <v-toolbar dense flat>
            <!-- <v-toolbar-title v-html="selectedEvent.name"></v-toolbar-title> -->
            <v-spacer></v-spacer>
            <div class="pa-1 pt-2 text-caption error--text">
              <span v-if="hasDraft">Has Draft</span>
            </div>
            <v-icon @click="edit">mdi-pencil</v-icon>
            <div class="px-3"></div>
            <v-icon @click="closeInfoDisplay">mdi-close</v-icon>
          </v-toolbar>
          <v-card-text>
            <EventDisplay :event="selectedEvent" />
          </v-card-text>
          <!-- <v-card-actions>
            <v-btn text color="secondary" @click="closeInfoDisplay"> Cancel </v-btn>
          </v-card-actions> -->
        </v-card>
      </v-menu>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import { Vue, Component, Prop } from 'vue-property-decorator'
import { ScheduleEventInterface } from './ScheduleEventInterface'
import EventDisplay from './EventDisplay.vue'

@Component({
  components: {
    EventDisplay
  }
})
export default class ScheduleCalendar extends Vue {
  @Prop({ default: [] }) readonly events!: ScheduleEventInterface[]
  calendarValue = ''

  infoDisplayOpen = false
  selectedEvent: ScheduleEventInterface | null = null
  selectedElement = null

  get title() {
    return (this.$refs.calendar as any).title
  }

  get hasDraft() {
    if (!this.selectedEvent) {
      return false
    }
    return this.selectedEvent.promotion?.hasDraft
  }

  calToday() {
    this.calendarValue = ''
  }

  calNext() {
    ;(this.$refs.calendar as any).next()
  }

  calPrev() {
    ;(this.$refs.calendar as any).prev()
  }

  onChange({ start, end }) {
    const title = this.title || ''
    this.$emit('change', { start, end, title })
  }

  onDayClick(args) {
    this.$emit('dayClick', args.date)
  }

  onDateClick(args) {
    this.$emit('dateClick', args.date)
  }

  onCreateClick(args, nativeEvent) {
    if (nativeEvent) {
      nativeEvent.preventDefault()
      nativeEvent.stopPropagation()
    }
    this.$emit('createClick')
  }

  onEventClick({ nativeEvent, event }) {
    const open = () => {
      this.selectedEvent = event
      this.selectedElement = nativeEvent.target
      requestAnimationFrame(() => requestAnimationFrame(() => (this.infoDisplayOpen = true)))
    }

    if (this.infoDisplayOpen) {
      this.infoDisplayOpen = false
      requestAnimationFrame(() => requestAnimationFrame(() => open()))
    } else {
      open()
    }

    nativeEvent.preventDefault()
    nativeEvent.stopPropagation()

    this.$emit('eventClick', event)
  }

  closeInfoDisplay() {
    this.infoDisplayOpen = false
  }

  edit() {
    this.closeInfoDisplay()
    this.$emit('edit', this.selectedEvent)
  }
}
</script>

<style>
.v-calendar-weekly__day {
  display: flex;
  flex-direction: column;
}
</style>

<style scoped>
.cal-new-clicker-holder {
  cursor: pointer;
  flex-grow: 1;
}
.cal-new-clicker {
  text-align: center;
  border: 1px dashed #ccc;
  align-items: center;
  justify-content: center;
}
</style>
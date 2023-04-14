<template>
  <div id="EventDisplay">
    <v-list two-line>
      <v-list-item v-for="(point, i) in infoPoints"
                   :key="`info_${i}`">
        <v-list-item-content>
          <v-list-item-title v-html="point.title"></v-list-item-title>
          <v-list-item-subtitle v-html="point.info"></v-list-item-subtitle>
        </v-list-item-content>

      </v-list-item>
      <v-list-item v-if="promotion">
        <v-list-item-content>
          <v-list-item-title>
            Start Date
          </v-list-item-title>
          <TimeViewer :time="promotion.schedule.dateStartString"
                      :timezone="event.timezone" />
        </v-list-item-content>
      </v-list-item>
    </v-list>
</div>
</template>

<script lang='ts'>
import { Vue, Component, Prop } from 'vue-property-decorator'
import { ScheduleEventInterface } from './ScheduleEventInterface'
import TimeViewer from '@/plugin/components/TimeViewer.vue'

@Component({
  components: {
    TimeViewer
  }
})
export default class EventDisplay extends Vue {
  @Prop({ required: true }) readonly event!: ScheduleEventInterface

  get promotion() {
    return this.event.promotion
  }

  get infoPoints() {
    if (!this.promotion) {
      return []
    }
    return [
      {
        title: 'Name',
        info: this.promotion.title
      },
      {
        title: 'Description',
        info: this.promotion.description
      },
      {
        title: 'Challenges',
        info: this.promotion.challengeAmount
      },
      {
        title: 'Name',
        info: this.promotion.title
      }
    ]
  }
}
</script>

<style scoped></style>
<template>
  <div id="ScheduleList">
    <v-data-table :items="events"
                  :headers="headers">
      <template #[`item.start`]="{ item }">
        <TimeViewer :time="item.promotion.schedule.dateStartString"
                    :timezone="item.timezone" />
      </template>
      <template #[`item.color`]="{ item }">
        <div :style="`width: 20px; height: 20px; background: ${item.color}; border-radius: 4px`"></div>
      </template>

      <template #[`item.promotion`]="{ item }">
        <div class="d-flex justify-end">
          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <v-icon v-bind="attrs"
                      v-on="on"
                      :color="item.promotion.hasReward ? 'primary' : 'grey'">mdi-medal</v-icon>
            </template>
            <span>This event has {{ !!item.promotion.reward ? '' : 'no' }}</span> rewards
          </v-tooltip>

          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <v-icon v-bind="attrs"
                      v-on="on"
                      :color="item.promotion.hasChallenges ? 'primary' : 'grey'">mdi-map</v-icon>
            </template>
            <span>This event has {{ item.promotion.hasChallenges ? '' : 'no' }}</span> challenges
          </v-tooltip>
          <div class="pa-4"></div>

          <template v-if="item.promotion.hasDraft">
            <v-tooltip top>
              <template v-slot:activator="{ on, attrs }">
                <v-badge bordered
                         color="error"
                         icon="mdi-note-edit-outline"
                         overlap>
                  <v-btn v-bind="attrs"
                         v-on="on"
                         icon
                         @click="edit(item)">
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                </v-badge>
              </template>

              <span>This promotion has a draft</span>
            </v-tooltip>
          </template>
          <template v-else>
            <v-btn icon
                   @click="edit(item)">
              <v-icon>mdi-pencil</v-icon>
            </v-btn>
          </template>

          <div class="pl-6 pt-1">
            <v-btn color="warning"
                   x-small
                   @click="beginDisable(item)">Disable</v-btn>
          </div>
        </div>
      </template>
    </v-data-table>

    <v-dialog width="400"
              persistent
              v-model="warnDelete">
      <v-card>
        <v-card-title> Are you sure you want to disable this promotion? </v-card-title>
        <v-card-text> This will prevent the reward from being activated. You can enable it later. </v-card-text>
        <v-card-actions>
          <v-btn text
                 @click="cancelDisable">Cancel</v-btn>
          <v-spacer></v-spacer>
          <v-btn color="warning"
                 @click="doDisable">Disable</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang='ts'>
import { Vue, Component, Prop } from 'vue-property-decorator'
import { Promotion } from '../Promotion'
import { ScheduleEventInterface } from './ScheduleEventInterface'
import TimeViewer from '@/plugin/components/TimeViewer.vue'

@Component({
  components: {
    TimeViewer
  }
})
export default class ScheduleList extends Vue {
  @Prop() readonly promotions!: Promotion[]
  @Prop() readonly events!: ScheduleEventInterface[]

  disableItem: ScheduleEventInterface | null = null
  warnDelete = false

  headers = [
    {
      text: '',
      value: 'color'
    },
    {
      text: 'Date',
      value: 'start'
    },
    {
      text: 'Title',
      value: 'promotion.title'
    },
    {
      text: '',
      value: 'promotion',
      align: 'end'
    }
  ]

  onEditClick(p: Promotion) {
    this.$emit('edit', p.id)
  }

  edit(event: ScheduleEventInterface) {
    this.$emit('edit', event)
  }

  beginDisable(event: ScheduleEventInterface) {
    this.disableItem = event
    this.warnDelete = true
  }

  cancelDisable() {
    this.disableItem = null
    this.warnDelete = false
  }

  doDisable() {
    this.$emit('disable', this.disableItem)
    this.cancelDisable()
  }
}
</script>

<style scoped>

</style>
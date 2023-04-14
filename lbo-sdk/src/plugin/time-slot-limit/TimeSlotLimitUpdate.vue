<template>
  <v-row>
    <v-col cols="12" class="pb-0">
      <span class="text-subtitle-1 grey--text text--darken-2"> Set the daily time frame limits for this player. </span>
    </v-col>
    <v-col cols="12">
      <v-row>
        <v-col cols="12" md="6">
          <v-text-field
            label="From Time"
            value=""
            type="time"
            suffix="UCT"
            hint="All time is in UCT"
            persistent-hint
            @change="onFromTimeSelected"
            :disabled="isDeleting"
          ></v-text-field>

          <!-- <TimeSelector label="From Time" @change="onFromTimeSelected" hint="All time is in UCT" /> -->
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            label="To Time"
            value=""
            type="time"
            suffix="UCT"
            hint="All time is in UCT"
            persistent-hint
            @change="onToTimeSelected"
            :disabled="isDeleting"
          ></v-text-field>
        </v-col>
      </v-row>
    </v-col>

    <v-col cols="12" class="pb-0">
      <span class="text-subtitle-1 grey--text text--darken-2"> The current daily time frame limits for this player. </span>
    </v-col>
    <v-col cols="12">
      <v-row>
        <v-col cols="12" md="6">
          <v-text-field outlined readonly disabled label="Current From time" :value="currentFrom"></v-text-field>
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field outlined readonly disabled label="Current To time" :value="currentTo"></v-text-field>
        </v-col>
      </v-row>
    </v-col>

    <v-col cols="12">
      <v-switch
        v-model="isDeleting"
        label="Delete this Time Slot Limit"
        hint="The player will no longer have a Time Slot Limit"
        persistent-hint
        @change="onDeleting"
      ></v-switch>
    </v-col>

    <v-col cols="12">
      <v-divider></v-divider>
    </v-col>

    <v-col cols="12">
      <v-row>
        <v-btn text class="text-none" @click="onCancel">Cancel</v-btn>
        <v-spacer></v-spacer>
        <v-btn v-if="isDeleting" color="error"> <v-icon>mdi-delete</v-icon> Confirm Delete</v-btn>
        <v-btn v-else :disabled="disableModify" color="info">Modify</v-btn>
      </v-row>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import { Component, Vue } from 'vue-property-decorator'
import TimeSelector from '../components/TimeSelector.vue'

@Component({
  components: {
    TimeSelector
  }
})
export default class TimeSlotLimitUpdate extends Vue {
  fromTimeUct: Date | null = null
  toTimeUct: Date | null = null

  currentFrom = 'None set'
  currentTo = 'None set'

  isDeleting = false

  get disableModify() {
    if (this.fromTimeUct === null || this.toTimeUct === null) {
      return true
    }
    return this.fromTimeUct > this.toTimeUct
  }

  onFromTimeSelected(time: string | null) {
    if (time === null) {
      return
    }
    this.fromTimeUct = this.convertStringTimeToUct(time)
  }

  onToTimeSelected(time: string | null) {
    if (time === null) {
      return
    }
    this.toTimeUct = this.convertStringTimeToUct(time)
  }

  onDeleting() {
    this.$emit('deleting', this.isDeleting)
  }

  onModify() {
    this.$emit('change')
  }

  onCancel() {
    this.$emit('cancel')
  }

  private convertStringTimeToUct(time: string) {
    const split = time.split(':')
    const hours = parseInt(split[0])
    const minutes = parseInt(split[1])

    const date = new Date()
    date.setUTCHours(hours, minutes)

    return date
  }
}
</script>
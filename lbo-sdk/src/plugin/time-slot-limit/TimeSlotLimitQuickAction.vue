<template>
  <div>
    <v-btn text outlined class="text-none" @click="onButtonClick">Update Time Frame Limits</v-btn>

    <v-dialog v-model="showDialog" max-width="500" persistent>
      <v-card>
        <v-card-title>
          <v-icon left color="info">{{icon}}</v-icon>
          <span>Time Frame Limits</span>
        </v-card-title>

        <v-card-text>
          <TimeSlotLimitUpdate @cancel="onCancel" @deleting="onDeleting" />
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang='ts'>
import { Component, Vue } from 'vue-property-decorator'

import TimeSlotLimitUpdate from './TimeSlotLimitUpdate.vue'

@Component({
  components: {
    TimeSlotLimitUpdate
  }
})
export default class TimeSlotLimitQuickAction extends Vue {
  showDialog = false
  showDeleteIcon = false

  get icon() {
    return this.showDeleteIcon ? 'mdi-delete' : 'mdi-pencil'
  }

  onButtonClick() {
    this.showDialog = true
  }

  onCancel() {
    this.showDialog = false
  }

  onDeleting(value: boolean) {
    this.showDeleteIcon = value
  }
}
</script>
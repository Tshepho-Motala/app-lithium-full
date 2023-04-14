<template>
  <v-row>
    <v-col cols="12">
      <span class="text-h4">DEMO - External Subscription Call</span>
    </v-col>

    <v-col cols="12">
      <v-divider></v-divider>
    </v-col>

    <v-col cols="12">
      The button below does not directly bind to any function, but rather executes a function within a subscription. The subscription can be set from
      anywhere such as outside the Vue framework, and as long as we match the keys those functions can be called within the Vue framework.
    </v-col>

    <v-col cols="12">
      <v-divider></v-divider>
    </v-col>

    <v-col cols="6">
      <v-btn @click="onClickAmountClick">Update Click Amount</v-btn>
    </v-col>
    <v-col cols="6"> Click amount: {{ clickAmount }} </v-col>

    <v-col cols="6">
      <v-btn @click="onShowGenericDialogClick">Show Generic Dialog</v-btn>
    </v-col>

    <v-col cols="6">
      <v-btn @click="onShowConfirmDialogClick">Show Confirm Dialog</v-btn>
    </v-col>

    <v-col cols="6">
      <v-btn @click="onDeleteClick" color="error"> <v-icon left>mdi-delete</v-icon> Delete example </v-btn>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import ListenerServiceInterface from '@/core/interface/service/ListenerServiceInterface'
import { ConfirmDialogInterface } from '@/plugin/components/dialog/DialogInterface'
import { Component, Inject, Vue } from 'vue-property-decorator'

@Component
export default class ExternalCall extends Vue {
  @Inject('listenerService') readonly listenerService!: ListenerServiceInterface

  clickAmountSubKey = 'demo-click-amount-sub-key'
  clickAmount = 0

  mounted() {
    // Subscribe to listener service with a unique key
    this.listenerService.subscribe(this.clickAmountSubKey, () => {
      this.clickAmount++
    })
  }

  onClickAmountClick() {
    this.listenerService.call(this.clickAmountSubKey)
  }

  onShowGenericDialogClick() {
    this.listenerService.call('dialog-generic')
  }

  onShowConfirmDialogClick() {
    this.listenerService.call('dialog-confirm')
  }

  onDeleteClick() {
    const params: ConfirmDialogInterface = {
      title: 'Please confirm deletion',
      text: 'Click cancel to go back, or click confirm to permanently delete this item.',
      btnPositive: {
        text: 'Confirm',
        onClick: () => {
          console.log('You have confirmed deleting')
        }
      },
      btnNegative: {
        text: 'Cancel',
        flat: true,
        onClick: () => {
          console.log('You have cancelled')
        }
      }
    }
    this.listenerService.call('dialog-confirm', params)
  }
}
</script>
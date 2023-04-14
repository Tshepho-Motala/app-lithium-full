<template>
  <GenericDialog v-model="showDialog" :title="title" :text="text" :overrideSubscription="true" data-test-id="confirm-dialog">
    <template #actions>
      <v-btn :text="btnNegative.flat" @click="btnClick(false)" :color="btnNegative.color">{{btnNegative.text}}</v-btn>
      <v-spacer></v-spacer>
      <v-btn :text="btnPositive.flat" @click="btnClick(true)" :color="btnPositive.color">{{btnPositive.text}}</v-btn>
    </template>
  </GenericDialog>
</template>

<script lang='ts'>
import ListenerServiceInterface from '@/core/interface/service/ListenerServiceInterface'
import LogServiceInterface from '@/core/interface/service/LogServiceInterface'
import { Component, Inject, Vue } from 'vue-property-decorator'
import { ButtonInterface, ConfirmDialogInterface, SpacerInterface } from './DialogInterface'
import GenericDialog from './GenericDialog.vue'

@Component({
  components: {
    GenericDialog
  }
})
export default class ConfirmDialog extends Vue {
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('listenerService') readonly listenerService!: ListenerServiceInterface

  // Local Properties
  showDialog = false
  boundProperty: ConfirmDialogInterface | null = null

  get title():string {
    return this.boundProperty?.title || 'Are you sure?'
  }

  get text(): string {
    return this.boundProperty?.text || 'Please click Confirm to continue, or Cancel to go back.'
  }

  get btnPositive(): ButtonInterface {
    if(this.boundProperty?.btnPositive) {
      return this.boundProperty.btnPositive
    } else {
      return this.createButton('Confirm', { })
    }
  }

  get btnNegative(): ButtonInterface {
    if(this.boundProperty?.btnNegative) {
      return this.boundProperty.btnNegative
    } else {
      return this.createButton('Cancel', { flat: true })
    }
  }

  mounted() {
    this.listenerService.subscribe('dialog-confirm', (param: ConfirmDialogInterface) => {
      this.boundProperty = param || null
      this.showDialog = true
    })
  }

  btnClick(positive: boolean) {
    if(positive) {
      this.btnPositive.onClick()
    } else {
      this.btnNegative.onClick()
    }
    this.showDialog = false
  }

  private createButton(text: string, { color = '', flat = false }): ButtonInterface {
    return {
      text,
      color,
      flat,
      onClick: () => {
        this.logService.log('You clicked ' + text)
      }
    }
  }
}
</script>
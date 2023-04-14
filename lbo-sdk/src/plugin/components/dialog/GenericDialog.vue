<template>
  <v-dialog v-model="visibility" max-width="500" persistent data-test-id="generic-dialog">
    <slot name="template">
      <v-card>
        <v-card-title>
          <slot name="title">
            {{dialogTitle}}
          </slot>
        </v-card-title>
        <v-card-text>
          <slot name="text">
            {{dialogText}}
          </slot>
        </v-card-text>
        <v-card-actions>
          <slot name="actions">
            <template v-for="(control, i) of actionControls">
              <v-spacer v-if="control.spacer" :key="`action-spacer-${i}`"></v-spacer>
              <v-btn v-else @click="btnClick(i)" :color="control.color" :key="`action-button-${i}`" :text="control.flat">{{control.text}}</v-btn>
            </template>
          </slot>
        </v-card-actions>
      </v-card>
    </slot>
  </v-dialog>
</template>

<script lang='ts'>
import { Component, Inject, Prop, VModel, Vue } from 'vue-property-decorator'

import { ButtonInterface, GenericDialogInterface, SpacerInterface } from './DialogInterface'
import ListenerServiceInterface from '@/core/interface/service/ListenerServiceInterface'
import LogServiceInterface from '@/core/interface/service/LogServiceInterface'

@Component
export default class GenericDialog extends Vue {
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('listenerService') readonly listenerService!: ListenerServiceInterface

  @VModel({ type: Boolean, default: false }) visible!: boolean
  // Parameter Properties
  @Prop({ default: null }) title!: string | null
  @Prop({ default: null }) text!: string | null
  @Prop({ default: false }) overrideSubscription!: boolean

  // Local Properties
  showDialog = false
  boundProperty: GenericDialogInterface | null = null

  // Getters
  get visibility(): boolean {
    return this.showDialog || this.visible || false
  }

  set visibility(v: boolean) {
    this.showDialog = v
    this.visible = v
  }

  get dialogTitle(): string {
    return this.title || this.boundProperty?.title || 'Generic Dialog'
  }

  get dialogText(): string {
    return this.text || this.boundProperty?.text || 'Text will be displayed here'
  }

  get actionControls(): (ButtonInterface | SpacerInterface)[] {
    if (this.boundProperty === null || !this.boundProperty.actionControls) {
      return [this.createButton('Close', { flat: true }), { spacer: true }, this.createButton('Accept', { color: 'primary' })] // Default buttons
    }
    return this.boundProperty.actionControls
  }

  // Bindings
  mounted() {
    if (this.overrideSubscription) {
      return
    }
    this.listenerService.subscribe('dialog-generic', (param: GenericDialogInterface) => {
      this.boundProperty = param || null
      this.visibility = true
    })
  }

  btnClick(index: number) {
    (this.actionControls[index] as ButtonInterface).onClick()
    this.visibility = false
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
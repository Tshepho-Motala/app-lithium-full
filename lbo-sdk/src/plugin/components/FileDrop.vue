<template>
  <v-sheet
    id="dropzone"
    ref="dzone"
    tabindex="0"
    title="Click to grap a file from your PC!"
    color="indigo lighten-4"
    width="100%"
    style="cursor: pointer"
    height="120"
    class="pa-2 indigo lighten-4"
    @drop.prevent="dragover = false"
    @dragover.prevent="dragover = true"
    @dragenter.prevent="dragover = true"
    @dragleave.prevent="dragover = false"
    @change="onChange"
    @click="onClick"
    @drop="onDrop"
  >
    <input ref="upload" id="fileUpload" type="file" :accept="accept" style="display: none" />
    <div class="filedrop-row">
      <v-icon v-if="!dragover" color="indigo darken-2" size="75">mdi-cloud-upload-outline</v-icon>
      <v-icon v-if="dragover" color="indigo darken-2" size="75">mdi-book-plus</v-icon>
    </div>
    <div class="filedrop-row">
      <span class="title indigo--text text--darken-2 filedrop-text">Drag'n drop or click to upload file!</span>
    </div>
  </v-sheet>
</template>
<script lang="ts">
import { Component, Emit, Inject, Prop, Vue } from 'vue-property-decorator'

import LogServiceInterface from '@/core/interface/service/LogServiceInterface'

@Component
export default class FileDrop extends Vue {
  @Inject('logService') readonly logService!: LogServiceInterface
  @Prop({ required: false }) accept: any

  dragover: boolean = false

  file: any

  onClick(e: Event) {
    this.logService.log('onClick ', e)
    const fileupload = this.$el.firstElementChild as HTMLElement
    fileupload.click()
  }

  onChange(e: Event) {
    this.logService.log('onChange ', e)
    const target = e.target as HTMLInputElement
    if (target.files) {
      this.filesSelected(target.files)
    }
  }

  onDrop(e: Event) {
    this.logService.log('onDrop ', e)
    const dragevent = e as DragEvent
    if (dragevent.dataTransfer) {
      this.filesSelected(dragevent.dataTransfer.files)
    }
  }

  /**
   * upload event...
   */
  @Emit()
  filesSelected(fileList: FileList) {
    this.dragover = false
  }
}
</script>

<style scoped>
</style>
<template>
  <v-file-input label="Select a file" outlined @change="onFileInput" :hide-details="hideDetails" :disabled="disabled"></v-file-input>
</template>

<script lang='ts'>
import { Vue, Component, Prop } from 'vue-property-decorator'

@Component
export default class FileReaderText extends Vue {
  @Prop({ type: Boolean, default: false }) readonly disabled!: boolean
  @Prop({ type: Boolean, default: false }) readonly hideDetails!: boolean

  onFileInput(file: File) {
    console.time('File Read')
    const reader = new FileReader()

    reader.addEventListener('load', () => {
      console.timeEnd('File Read')
      this.$emit('data', reader.result)
    })

    reader.readAsText(file)
  }
}
</script>

<style scoped>
</style>
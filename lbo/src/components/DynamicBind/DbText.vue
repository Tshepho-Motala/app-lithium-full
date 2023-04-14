<template>
  <div>
      <v-text-field v-model="valueSync"></v-text-field>
  </div>
</template>

<script lang='ts'>
import { Vue, Component, PropSync } from 'vue-property-decorator'

@Component
export default class DbText extends Vue {
  @PropSync('value') valueSync!: string
}
</script>

<style scoped>
</style>

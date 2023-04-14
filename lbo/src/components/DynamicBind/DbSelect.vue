<template>
  <div>
    <v-select :items="items" :item-text="itemText" :item-value="itemValue" v-model="valueSync"></v-select>
  </div>
</template>

<script lang='ts'>
import { Vue, Component, Prop, PropSync } from 'vue-property-decorator'

@Component
export default class DbSelect extends Vue {
  @Prop() readonly items!: any[]
  @Prop() readonly itemText!: string
  @Prop() readonly itemValue!: string

  @PropSync('value') valueSync!: string
}
</script>

<style scoped>
</style>

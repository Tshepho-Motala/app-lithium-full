<template>
  <v-menu
    ref="menu"
    v-model="showMenu"
    :close-on-content-click="false"
    :nudge-right="40"
    :return-value.sync="time"
    transition="scale-transition"
    offset-y
    max-width="290px"
    min-width="290px"
  >
    <template v-slot:activator="{ on, attrs }">
      <v-text-field
        v-model="time"
        :label="label"
        :append-icon="icon"
        v-bind="attrs"
        v-on="on"
        :hint="hint"
        :persistent-hint="persistentHint"
        outlined
      ></v-text-field>
    </template>
    <v-time-picker @change="onTimeChange" v-if="showMenu" v-model="value" full-width @click:minute="$refs.menu.save(time)"></v-time-picker>
  </v-menu>
</template>
<script lang='ts'>
import { Component, Prop, VModel, Vue } from 'vue-property-decorator'

@Component
export default class TimeSelector extends Vue {
  @VModel({ default: null }) time!: string | null
  @Prop({ default: 'Select Time ' }) label!: string
  @Prop({ default: 'mdi-clock-time-four-outline' }) icon!: string
  @Prop({ default: null }) hint!: string | null

  showMenu = false

  get persistentHint() {
    return this.hint !== null
  }

  onTimeChange() {
    this.$emit('change', this.time)
  }
}
</script>
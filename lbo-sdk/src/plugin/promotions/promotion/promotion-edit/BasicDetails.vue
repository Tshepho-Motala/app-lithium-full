<template>
  <v-form lazy-validation ref="form">
    <v-row id="PromotionEditStepTwo" class="pt-2">
      <v-col cols="12">
        <div class="d-flex" style="gap: 16px">
          <div class="flex-grow-1">
            <v-text-field
              v-model="promotion.title"
              label="Name *"
              hint="Create a catchy name that captures the essence of the promotion."
              placeholder="Promotion Name..."
              persistent-hint
              outlined
              :rules="rules.required"
            ></v-text-field>
          </div>
          <v-menu :close-on-content-click="false" offset-y>
            <template v-slot:activator="{ on, attrs }">
              <div v-bind="attrs" v-on="on" :style="`background: ${promotion.theme.color}; height: 55px; width: 55px; border-radius: 5px`"></div>
            </template>

            <v-color-picker v-model="promotion.theme.colorHex"></v-color-picker>
          </v-menu>
        </div>
      </v-col>
      <v-col cols="12">
        <v-text-field
          v-model="promotion.description"
          label="Description *"
          placeholder="Description..."
          outlined
          hint="Provide a short description for the promotion."
          persistent-hint
          :rules="rules.required"
        ></v-text-field>
      </v-col>
    </v-row>
  </v-form>
</template>

<script lang='ts'>
import RulesMixin from '@/plugin/mixins/RulesMixin'
import { Component, Prop, Mixins } from 'vue-property-decorator'
import { Promotion } from '../../Promotion'

@Component
export default class BasicDetails extends Mixins(RulesMixin) {
  @Prop({ required: true }) readonly promotion!: Promotion
}
</script>

<style scoped>
</style>
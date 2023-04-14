<template>
  <v-row no-gutters>
    <v-col cols="12" class="py-4">
      <v-toolbar flat dense dark color="primary" class="px-0 mx-0"> Promotion Redemption for Players </v-toolbar>
    </v-col>
    <v-col cols="12">
      <v-text-field outlined :rules="rules.gtZero" type="number" label="Max Redemption over Promotion *" v-model="promotion.redeemOverPromotion"></v-text-field>
    </v-col>
    <v-col cols="12">
      <v-text-field outlined :rules="rules.gtZero" type="number" label="Max Redemption per Event *" v-model="promotion.redeemOverEvents"></v-text-field>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import RulesMixin from '@/plugin/mixins/RulesMixin'
import { Mixins, Component, Prop } from 'vue-property-decorator'
import { Promotion } from '../../Promotion'

@Component
export default class ActivationDetails extends Mixins(RulesMixin) {
  @Prop({ required: true }) readonly promotion!: Promotion
}
</script>

<style scoped>
</style>
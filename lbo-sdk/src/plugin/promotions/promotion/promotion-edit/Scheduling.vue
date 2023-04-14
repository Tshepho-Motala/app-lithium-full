<template>
  <v-row id="PromotionEditStepThree" class="pt-2">
    <v-col cols="12">
      <RRuleComponent :schedule="promotion.schedule" :color="promotion.theme.color" @change="onRruleChange"
        :eventTitle="eventTitle" :timezone="timezone">
      </RRuleComponent>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import { Vue, Component, Prop } from 'vue-property-decorator'
import { Promotion } from '../../Promotion'

import RRuleComponent from '@/plugin/components/RRule.vue'

@Component({
  components: {
    RRuleComponent
  }
})
export default class Scheduling extends Vue {
  @Prop({ required: true }) readonly promotion!: Promotion

  get eventTitle() {
    return this.promotion.title
  }

  get timezone() {
    return this.promotion.domain?.timezone;
  }

  onRruleChange({ rule, length, singleDay, start, until }) {
    this.promotion.schedule.rruleString = rule.toString()
    this.promotion.schedule.lengthInDays = length
    this.promotion.schedule.singleDay = singleDay
    this.promotion.schedule.dateStart = start
    this.promotion.schedule.dateUntil = until
  }
}
</script>

<style scoped>

</style>
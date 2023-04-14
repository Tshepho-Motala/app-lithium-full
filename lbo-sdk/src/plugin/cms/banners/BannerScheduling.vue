<template>
  <v-row id="banner-scgeduling" class="pt-2">
    <v-col cols="12">
      <RRuleComponent :schedule="schedule" :recurrencePattern="banner.recurrencePattern" @change="onRruleChange" :eventTitle="eventTitle" />
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import { Vue, Component, Prop } from 'vue-property-decorator'

import RRuleComponent from '@/plugin/components/RRule.vue'
import LayoutBannerItem from "@/plugin/cms/models/LayoutBannerItem";
import {Banner} from "@/plugin/cms/models/Banner";
import { Schedule } from '@/plugin/promotions/Promotion';
import { RRuleContract } from '@/plugin/components/RRule';

@Component({
  components: {
    RRuleComponent
  }
})
export default class BannerScheduling extends Vue {
  @Prop({ required: true }) banner!: Banner
  @Prop() schedule!: RRuleContract
  get eventTitle() {
    return this.banner.name;
  }

  onRruleChange(ruleResults: { rule, length, singleDay, start, until }) {
    this.$emit("change", ruleResults);
  }
}
</script>

<style scoped>
</style>
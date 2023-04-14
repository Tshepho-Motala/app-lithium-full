<template>
  <div>
  <v-row>
    <v-col cols="4">
      <SessionHistory />
      <BigWin />
    </v-col>
    <v-col cols="4">
      <PaymentMethodsTransaction/>
    </v-col>
    <v-col cols="4">
      <WagerHistory/>
      <ResponsibleGamblingTool/>
    </v-col>
  </v-row>

  <v-row  class="mt-5">
    <v-col cols="12">
    <BehaviourChange/>
    </v-col>
  </v-row>
  </div>
</template>

<script lang='ts'>
import { Vue, Component } from 'vue-property-decorator'
import SessionHistory from './session-history/SessionHistroy.vue'
import BigWin from './big-wins/BigWin.vue'
import WagerHistory from "@/plugin/player-protection/wager-history/WagerHistory.vue";
import PaymentMethodsTransaction
  from "@/plugin/player-protection/payment-methods-transactions/PaymentMethodsTransaction.vue";
import ResponsibleGamblingTool from "@/plugin/player-protection/responsible-gambling-tool/ResponsibleGamblingTool.vue";
import BehaviourChange from "@/plugin/player-protection/behaviour-change/BehaviourChange.vue";

@Component({
  components: {
    BehaviourChange,
    ResponsibleGamblingTool,
    PaymentMethodsTransaction,
    WagerHistory,
    SessionHistory,
    BigWin
  }
})
export default class PlayerProtection extends Vue {}
</script>

<style scoped>
</style>
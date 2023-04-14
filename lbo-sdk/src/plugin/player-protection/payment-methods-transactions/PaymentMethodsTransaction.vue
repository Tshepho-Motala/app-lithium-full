<template>
  <v-row>
    <v-col cols="12">
      <v-progress-linear v-if="loading" indeterminate></v-progress-linear>
    </v-col>

    <v-col cols="12"> <h4> {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.PAYMENT_METHODS_TRANSACTION.TITLE") }}</h4></v-col>
    <v-col cols="12">
      <v-simple-table dense>
        <template v-slot:default>
          <tbody>
          <tr>
            <td>{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.PAYMENT_METHODS_TRANSACTION.LAST_TWENTY_FOUR") }}</td>
            <td>
              {{ paymentMethodsAndTransactions.count }}
            </td>
            <td>
              <v-btn
                  @click="doWork"
                  x-small
              >
                {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.PAYMENT_METHODS_TRANSACTION.LAST_TWENTY_FOUR_DETAILS") }}
              </v-btn>
            </td>
          </tr>
          <tr>
            <td>{{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.PAYMENT_METHODS_TRANSACTION.LAST_MONTH") }}</td>
            <td>{{ paymentMethodsAndTransactions.failedTransactions }}</td>
            <td>
              <v-btn
                  @click="doWork"
                  x-small
              >
                {{ $translate("UI_NETWORK_ADMIN.PLAYER.PLAYER_PROTECTION.PAYMENT_METHODS_TRANSACTION.LAST_MONTH_DETAILS") }}
              </v-btn>
            </td>
          </tr>
          </tbody>
        </template>
      </v-simple-table>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import {Vue, Component, Mixins} from 'vue-property-decorator'
import {PaymentMethodsAndTransactions} from '../PlayerProtection'
import AssetTabMixin from "@/plugin/cms/mixins/AssetTabMixin";
import TranslationMixin from "@/core/mixins/translationMixin";

@Component
export default class PaymentMethodsTransaction extends  Mixins(AssetTabMixin,TranslationMixin) {
  paymentMethodsAndTransactions = new PaymentMethodsAndTransactions()

  mounted() {
    this.paymentMethodsAndTransactions.getDetails()
  }
}
</script>

<style scoped>
</style>
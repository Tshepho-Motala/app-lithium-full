<template>
  <div data-test-id="cnt-cashier-transaction-statuses">
    <v-chip v-if="transactionDetail.transactionType" class="mr-2" text-color="white"
            :color="getColor(transactionDetail.current.status.code)" :small="tableStatus" label>
      {{ translateStatus(transactionDetail.current.status.code) }}
    </v-chip>
    <v-chip class="mr-2" v-if="transactionDetail.current.status.code && transactionDetail.forcedSuccess"
            text-color="white" color="indigo" :small="tableStatus" label>
      {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.FORCEDSUCCESS') }}
    </v-chip>
    <v-chip v-if="transactionDetail.transactionType && !tableStatus" :small="tableStatus" class="mr-2"
            text-color="white"
            :color="getColor(transactionDetail.transactionType)" label>
      {{ translateStatus(transactionDetail.transactionType) }}
    </v-chip>
    <v-chip class="mr-2" v-if="transactionDetail.transactionType && transactionDetail.manual && !tableStatus"
            text-color="white"
            color="info" label> {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.ISMANUAL') }}
    </v-chip>
    <v-chip class="mr-2" v-if="transactionDetail.transactionType && transactionDetail.autoApproved  && !tableStatus"
            text-color="white"
            color="primary" label>{{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.AUTO_APPROVED') }}
    </v-chip>
  </div>
</template>

<script lang="ts">
import {Component, Inject, Mixins, Prop, Vue} from 'vue-property-decorator'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import {CashierTransactionsDataInterface} from "@/core/interface/cashier/cashierTransactions";


@Component
export default class TransactionStatuses extends Mixins(cashierMixins) {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  @Prop({required: true}) transactionDetail?: CashierTransactionsDataInterface
  @Prop({default: false}) tableStatus!: boolean


  // Translation Methods

  translateStatus(text: string): string {
    return this.translateService.instant('UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.' + text)
  }

  getColor(status: string) {
    if (['SUCCESS', 'APPROVED', 'AUTO_APPROVED', 'DEPOSIT'].includes(status)) {
      return 'success'
    } else if (['DECLINED', 'FATALERROR', 'CANCEL', 'PLAYER_CANCEL'].includes(status)) {
      return 'error'
    } else if (['REVERSAL'].includes(status)) {
      return 'orange'
    } else {
      return 'grey'
    }
  }
}
</script>

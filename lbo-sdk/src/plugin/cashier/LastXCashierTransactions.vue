<template>
  <v-container data-test-id="cnt-last-cashier-transactions">
    <v-row>
      <v-toolbar flat class="bg--gray rounded-t" dark height="42">
        <v-toolbar-title>Last 10 Transactions</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn small @click="onButtonMore()" data-test-id="btn-more"> More </v-btn>
      </v-toolbar>
    </v-row>
    <v-row>
      <div class="last-transactions__wrapper">
        <v-data-table
          data-test-id="tbl-transactions"
          :headers="headers"
          :items="transactions"
          :loading="loadingValue"
          loading-text="Loading... Please wait"
          hide-default-footer
        >
          <template v-slot:item.createdOn="{ item }">
            <span> {{ formatDate(item.createdOn) }} </span>
          </template>
          <template v-slot:item.amount="{ item }">
            <span> {{ formatCurrency(item) }} </span>
          </template>
          <template v-slot:item.status="{ item }">
            <v-chip text-color="white" :color="getColor(item.status)" label>{{
              translateService.instant('UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.' + item.status)
            }}</v-chip>
          </template>
        </v-data-table>
      </div>
    </v-row>

    <LastXBalanceMovementTransactions class="mt-8"></LastXBalanceMovementTransactions>
  </v-container>
</template>

<script lang='ts'>
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import { Component, Inject, Vue } from 'vue-property-decorator'
import { utcToZonedTime, format } from 'date-fns-tz'
import LastXBalanceMovementTransactions from "@/plugin/cashier/LastXBalanceMovementTransactions.vue";

@Component({
  components: {
    LastXBalanceMovementTransactions
  }
})
export default class LastXCashierTransactions extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  transactions: Array<any> = []
  userId: string = ''
  domainName: string = ''
  loadingValue: boolean = true

  headers = [
    {
      text: 'Created',
      sortable: false,
      value: 'createdOn'
    },
    { text: 'Type', sortable: false, value: 'transactionType' },
    { text: 'Processor', sortable: false, value: 'processor' },
    { text: 'Descriptor', sortable: false, value: 'descriptor' },
    { text: 'Amount', sortable: false, value: 'amount' },
    { text: 'Status', sortable: false, value: 'status' }
  ]

  mounted() {
    //for now hardcoded count of transactions is 10
    this.rootScope.provide.cashierProvider.loadLastXCashierTransactions(10).then((result: any) => {
      this.transactions = result.lastXTransactions
      this.userId = result.userId
      this.domainName = result.domainName
      this.loadingValue = false
    })
  }

  async onButtonMore() {
    this.rootScope.provide.cashierProvider.openUserTransactions(this.domainName, this.userId)
  }

  formatDate(millis: number): string {
    const date = new Date(millis)
    const zonedDate = utcToZonedTime(date, 'Etc/GMT')

    return format(zonedDate, 'yyyy-MM-dd HH:mm:ss.SSS')
  }

  formatCurrency(item: any): string {
    if (item.amount != null) {
      return item.currencyCode + item.amount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
    } else {
      return 'Not specified'
    }
  }

  getColor(status: string) {
    if (['SUCCESS', 'APPROVED', 'AUTO_APPROVED'].includes(status)) {
      return 'success'
    } else if (['DECLINED', 'FATALERROR', 'CANCEL', 'PLAYER_CANCEL'].includes(status)) {
      return 'error'
    } else if (['WAITFORAPPROVAL'].includes(status)) {
      return 'info'
    } else {
      return 'default'
    }
  }
}
</script>

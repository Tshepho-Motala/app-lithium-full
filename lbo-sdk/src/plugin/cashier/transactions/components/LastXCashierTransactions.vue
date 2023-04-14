<template>
  <v-container data-test-id="cnt-last-cashier-transactions">
    <v-row>
      <v-toolbar flat class="bg--gray rounded-t" dark height="42">
        <v-toolbar-title>Last 10 Transactions</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn small @click="onButtonMore()" data-test-id="btn-more"> More</v-btn>
      </v-toolbar>
    </v-row>
    <v-row>
      <div class="last-transactions__wrapper">
        <v-data-table
            class="table table-vue table-bordered table-striped"
            data-test-id="tbl-transactions"
            :headers="headers"
            :items="transactions"
            :loading="loadingValue"
            loading-text="Loading... Please wait"
            :footer-props="{
            showFirstLastPage: true,
            itemsPerPageOptions: [3]
          }"
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
              }}
            </v-chip>
          </template>
        </v-data-table>
      </div>
    </v-row>
  </v-container>
</template>

<script lang='ts'>
import {RootScopeInterface} from '@/core/interface/ScopeInterface'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import {Component, Inject, Mixins, Prop, Vue} from 'vue-property-decorator'
import CashierTransactionsApiInterface from "@/core/interface/axios-api/CashierTransactionsApiInterface";
import CashierTransactionsApi from "@/core/axios/axios-api/CashierTransactionsApi";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import {
  lastXCashierTransactionsApiParamsInterface
} from "@/core/interface/cashier/cashierTransactions";

@Component
export default class LastXCashierTransactions extends Mixins(cashierMixins) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({required: true}) transactionId!: number
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
    {text: 'Type', sortable: false, value: 'transactionType'},
    {text: 'Processor', sortable: false, value: 'processor'},
    {text: 'Descriptor', sortable: false, value: 'descriptor'},
    {text: 'Amount', sortable: false, value: 'amount'},
    {text: 'Status', sortable: false, value: 'status'}
  ]

  CashierTransactionsApiService: CashierTransactionsApiInterface = new CashierTransactionsApi(this.userService)

  async mounted() {
    await this.loadTransactionDetailInformation()
  }

  async loadTransactionDetailInformation() {
    if (this.transactionId) {
      //for now hardcoded count of transactions is 10
      const params: lastXCashierTransactionsApiParamsInterface = {
        count: 10,
        trId: this.transactionId
      }
      try {
        const result = await this.CashierTransactionsApiService.lastXCashierTransactions(params)
        if (result?.data?.successful) {

          this.transactions = result.data.data.lastXTransactions
          this.userId = result.data.data.userId
          this.domainName = result.data.data.domainName
          this.loadingValue = false

        }
      } catch (err) {
        this.logService.error(err)
      }
    }
  }

  async onButtonMore() {
    this.rootScope.provide.cashierProvider.openUserTransactions(this.domainName, this.userId)
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

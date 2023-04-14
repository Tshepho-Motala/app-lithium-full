<template>
  <div data-test-id="cnt-last-cashier-balance-transactions">
    <v-row>
      <v-toolbar flat class="bg--gray rounded-t" dark height="42">
        <v-toolbar-title>Last 10 Balance Movement Transactions</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn small @click="onButtonMore()" data-test-id="btn-more"> More</v-btn>
      </v-toolbar>
    </v-row>
    <v-row>
      <div v-if="balanceMovementList" class="last-transactions__wrapper">
        <v-data-table
            data-test-id="tbl-transactions"
            :headers="headers"
            :items="balanceMovementList"
            :loading="loadingValue"
            loading-text="Loading... Please wait"
            hide-default-footer
        >
          <template v-slot:item.date="{ item }">
            <span> {{ formatDate(item.date) }} </span>
          </template>

          <template v-slot:item.amountCents="{ item }">
            <span style="color: green" v-if="item.amountCents > 0">  <b>{{ formatCurrencyNumberCent(item.amountCents)
              }}</b></span>
            <span v-else style="color: red"> <b>{{ formatCurrencyNumberCent(item.amountCents) }}</b>  </span>

          </template>

          <template v-slot:item.postEntryAccountBalanceCents="{ item }">
            <span> {{ formatCurrencyNumberCent(item.postEntryAccountBalanceCents) }} </span>
          </template>
        </v-data-table>
      </div>
    </v-row>
  </div>
</template>

<script lang='ts'>
import {RootScopeInterface} from '@/core/interface/ScopeInterface'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import {Component, Inject, Mixins, Vue} from 'vue-property-decorator'
import {utcToZonedTime, format} from 'date-fns-tz'
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import CashierTransactionsApiInterface from "@/core/interface/axios-api/CashierTransactionsApiInterface";
import CashierTransactionsApi from "@/core/axios/axios-api/CashierTransactionsApi";
import {BalanceMovementListInterface} from "@/core/interface/player/BalanceMovementInterface";
import {
  BalanceMovementTransactionsDataItemInterface,
  CashierTransactionsDataInterface, UserInterface
} from "@/core/interface/cashier/cashierTransactions";
import UserApiInterface from "@/core/interface/axios-api/UserApiInterface";
import UserApi from "@/core/axios/axios-api/UserApi";

@Component
export default class LastXBalanceMovementTransactions extends Mixins(cashierMixins) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  userData: UserInterface | null = null
  transaction: CashierTransactionsDataInterface | undefined = undefined
  loadingValue: boolean = true
  balanceMovementList: BalanceMovementTransactionsDataItemInterface[] | null = null
  headers = [
    {
      text: 'Date',
      align: 'start',
      sortable: false,
      value: 'date',
    },
    {text: 'Transaction ID', sortable: false, value: 'transaction.id'},
    {text: 'Transaction Type', sortable: false, value: 'transaction.transactionType.code'},
    {text: 'Amount', sortable: false, value: 'amountCents'},
    {text: 'Account Balance', sortable: false, value: 'postEntryAccountBalanceCents'},
    {text: 'Provider Tran Id', sortable: false, value: 'details.externalTranId'}
  ]
  CashierTransactionsApiService: CashierTransactionsApiInterface = new CashierTransactionsApi(this.userService)
  UserApiService: UserApiInterface = new UserApi(this.userService)

  mounted() {
    this.transaction = this.rootScope.provide.cashierProvider.transaction
    this.loadData()
  }

  async loadData() {
    await this.loadUser()
  }

  async loadBalanceMovementList() {
    this.loadingValue = true
    if (this.userData) {
      const params: BalanceMovementListInterface = {
        userGuid: this.userData.guid,
        pageSize: 10,
        domainName: this.userData.domain.name,
        dateRangeStart: this.formatDateStart(this.userData.createdDate)

      }
      try {
        const response = await this.CashierTransactionsApiService.balanceMovementList(params)
        if (response?.data?.data) {
          this.balanceMovementList = response.data.data
        }
      } catch (e) {
        this.snackbar = {
          show: true,
          text: e.message,
          color: 'error'
        }
      } finally {
        this.loadingValue = false
      }
    }
  }

  async onButtonMore() {
    if (this.userData) {
      this.rootScope.provide.cashierProvider.openUserBalanceMovementTransactionTransactions(this.userData.domain.name, this.userData.id)
    }
  }


  async loadUser() {
    if (this.transaction?.user?.guid && this.transaction?.domainMethod.domain.name) {
      try {
        const result = await this.UserApiService.userFindFromGuid(this.transaction.domainMethod.domain.name, {guid: this.transaction.user.guid,})
        if (result?.data?.successful) {
          this.userData = result.data.data
          await this.loadBalanceMovementList()
        }
      } catch (err) {
        this.logService.error(err)
      }
    }

  }

  formatDate(millis: number): string {
    const date = new Date(millis)
    const zonedDate = utcToZonedTime(date, 'Etc/GMT')

    return format(zonedDate, 'yyyy-MM-dd HH:mm:ss.SSS')
  }

  formatDateStart(millis: number): string {
    const date = new Date(millis)
    return format(date, 'yyyy-MM-dd')
  }

  formatCurrency(item: any): string {
    if (item.amount != null) {
      return item.currencyCode + item.amount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
    } else {
      return 'Not specified'
    }
  }

}
</script>

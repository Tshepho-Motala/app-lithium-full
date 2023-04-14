<template>
  <div>
    <v-card style="width: 100%; box-shadow: none; border-radius: 0;" class=" w-100 pl-4 pr-4 pt-4 pb-2 mb-4">
      <v-form class="mt-3" data-test-id="frm-auto-withdrawal-rulset-filer">
        <v-row v-if="openFilter">

          <v-col cols="12" sm="12" md="3">
            <v-menu
                v-model="dateStartOpen"
                :close-on-content-click="false" transition="scale-transition" offset-y min-width="auto"
            >
              <template v-slot:activator="{ on, attrs }">
                <v-text-field
                    v-model="dateStart"
                    label="Date: Range Start *"
                    prepend-icon="mdi-calendar"
                    v-bind="attrs"
                    outlined
                    dense
                    v-on="on"
                ></v-text-field>
              </template>
              <v-date-picker
                  v-model="dateStart"
                  @input="dateStartOpen = false"
              ></v-date-picker>
            </v-menu>
          </v-col>
          <v-col cols="12" sm="12" md="3">
            <v-menu
                v-model="dateEndOpen"
                :close-on-content-click="false" transition="scale-transition" offset-y min-width="auto"
            >
              <template v-slot:activator="{ on, attrs }">
                <v-text-field
                    v-model="dateEnd"
                    label="Date: Range End *"
                    prepend-icon="mdi-calendar"
                    v-bind="attrs"
                    outlined
                    dense
                    v-on="on"
                ></v-text-field>
              </template>
              <v-date-picker
                  v-model="dateEnd"
                  @input="dateEndOpen = false"
              ></v-date-picker>
            </v-menu>
          </v-col>
          <v-col cols="12" sm="12" md="3">
            <v-text-field v-model="tranId" label="Provider Tran Id" outlined dense v-on="on"></v-text-field>
          </v-col>
          <v-col cols="12" sm="12" md="3">
            <v-select
                :items="typeLists"
                :menu-props="{ bottom: true, offsetY: true }"
                label="Transaction Type"
                v-model="transactionTypes"
                item-value="code"
                item-text="code"
                multiple
                outlined
                dense
            ></v-select>
          </v-col>
        </v-row>
      </v-form>
      <div class="description d-flex justify-end mb-2">
        {{translateAll('UI_NETWORK_ADMIN.BALANCE.TEMPLATES.LIST.INFO')}}
      </div>
      <div class="d-flex justify-space-between mb-4">
        <v-btn color="primary" dark @click="openFilter = !openFilter">
          <v-icon class="mr-1">mdi-filter</v-icon>
          {{ !openFilter ? translate('PAGE.OPEN-FILTER') : translate('PAGE.CLOSE-FILTER') }}
        </v-btn>

        <div>
          <v-btn @click="clearFilter" data-test-id="btn-auto-withdrawal-list--clear" color="error" dark>
            <v-icon class="mr-1">mdi-delete-outline</v-icon>
            {{ translate('PAGE.CLEAR') }}
          </v-btn>
          <template v-if="openFilter">
            <v-btn @click="filter" data-test-id="btn-auto-withdrawal-list--filter" color="primary"
                   dark>
              <v-icon class="mr-1">mdi-filter</v-icon>
              {{ translate('PAGE.FILTER') }}
            </v-btn>
          </template>
          <template v-else>
            <v-btn @click="loadBalanceMovementList" data-test-id="btn-auto-withdrawal-list--refresh" color="primary"
                   dark>
              <v-icon class="mr-1">mdi-cached</v-icon>
              {{ translate('PAGE.REFRESH') }}
            </v-btn>
          </template>
          <v-btn @click="downloadExel" data-test-id="btn-auto-withdrawal-list--refresh" color="success"
                 dark>
            <v-icon class="mr-1">mdi-file-download</v-icon>
            {{ translateAll('UI_NETWORK_ADMIN.BALANCE.TEMPLATES.LIST.UPLOAD')}}
          </v-btn>

        </div>
      </div>
      <template  v-if="balanceMovementList">
        <v-data-table
            :headers="headers"
            :items="balanceMovementList"
            :loading="loading"
            loading-text="Loading... Please wait"
            :server-items-length="totalItems"
            :options.sync='options'
            :footer-props="{
             'items-per-page': 10,
             'items-per-page-options': [20, 30, 50, 100, 200]
            }"
            class="elevation-1 mt-5 mb-3"
        >
          <template v-slot:item.date="{ item }">
            <span> {{ formatDate(item.date) }} </span>
          </template>

          <template v-slot:item.amountCents="{ item }">
            <span style="color: green" v-if="item.amountCents > 0">  <b>{{ formatCurrencyNumberCent(item.amountCents) }}</b></span>
            <span v-else  style="color: red"> <b>{{ formatCurrencyNumberCent(item.amountCents) }}</b>  </span>

          </template>

          <template v-slot:item.postEntryAccountBalanceCents="{ item }">

            <span> {{ formatCurrencyNumberCent(item.postEntryAccountBalanceCents) }} </span>
          </template>

          <template v-slot:item.details.externalTranId="{ item }">
            <span > {{ item.details.externalTranId }} </span>
          </template>
        </v-data-table>
      </template>

    </v-card>

    <v-snackbar
        v-model="snackbar.show"
        :timeout="2000"
        :color="snackbar.color"
    >
      {{ snackbar.text }}

    </v-snackbar>
  </div>
</template>

<script lang="ts">
import {Component, Mixins, Watch} from 'vue-property-decorator'
import {CashierConfigUser} from "@/core/interface/cashierConfig/CashierConfigInterface";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import CashierTransactionsApiInterface from "@/core/interface/axios-api/CashierTransactionsApiInterface";
import CashierTransactionsApi from "@/core/axios/axios-api/CashierTransactionsApi";
import {format, utcToZonedTime} from "date-fns-tz";
import {
  BalanceMovementListInterface,
  BalanceMovementTypeListItemInterface, BalanceMovementTypeSendInterface
} from "@/core/interface/player/BalanceMovementInterface";
import {BalanceMovementTransactionsDataItemInterface} from "@/core/interface/cashier/cashierTransactions";

@Component
export default class BalanceMovement extends Mixins(cashierMixins) {
  dateStart: string = ''
  dateEnd: string = ''
  tranId: string = ''
  dateStartOpen: boolean = false
  dateEndOpen: boolean = false
  transactionTypes: string[] = []
  searchTable: string = ''
  openFilter: boolean = true
  options:any =  {
    page: 1,
    itemsPerPage: 20
  }
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
  loading:boolean = true
  typeLists:BalanceMovementTypeListItemInterface[] = []
  user: CashierConfigUser | null = null
  balanceMovementList:BalanceMovementTransactionsDataItemInterface[] | null = null
  totalItems:number = 0
  CashierTransactionsApiService: CashierTransactionsApiInterface = new CashierTransactionsApi(this.userService)


  @Watch('options')
  onFieldChange = () => {
    this.loadBalanceMovementList()
  }

  async mounted() {
    this.user = this.rootScope.provide.quickActionProvider.user
    if (this.user) {
      this.addFirstDate()
    }
    await this.loadBalanceMovementTypes()
    await this.loadBalanceMovementList()
  }

  async loadBalanceMovementTypes() {
    if (this.user) {
      const params: BalanceMovementTypeSendInterface = {
        userGuid: this.user.guid
      }
      try {
        const response = await this.CashierTransactionsApiService.balanceMovementTypes(params)
        if (response?.data?.data) {
          this.typeLists = response.data.data
        }
      } catch (e) {
        this.snackbar = {
          show: true,
          text: e.message,
          color: 'error',
        }
      }
    }
  }
  async loadBalanceMovementList() {
    this.loading = true
    if (this.user) {
      const params: BalanceMovementListInterface = {
        userGuid: this.user.guid,
        page: this.options.page - 1,
        pageSize: this.options.itemsPerPage,
        dateRangeStart: this.dateStart,
        dateRangeEnd: this.dateEnd,
        domainName: this.user.domain.name,
        providerTransId: this.tranId,
        transactionType: this.transactionTypes.join(','),
      }
      try {
        const response = await this.CashierTransactionsApiService.balanceMovementList(params)
        if (response?.data?.data) {
          this.balanceMovementList = response.data.data
          this.totalItems =  response.data.recordsFiltered
        }
      } catch (e) {
        this.snackbar = {
          show: true,
          text: e.message,
          color: 'error'
        }
      } finally {
        this.loading = false
      }
    }
  }

  clearFilter() {
    this.$set( this.options , 'page', 1)
    this.addFirstDate()
    this.tranId = ''
    this.transactionTypes = []
    this.loadBalanceMovementList()
  }
  filter(){
    this.$set( this.options , 'page', 1)
    this.loadBalanceMovementList()
  }

  addFirstDate() {
    if (this.user) {
      this.dateStart = this.formatDateCustom(this.user.createdDate)
    }
    let nowDate = Date.now()
    let tomorrowData: any = this.addDays(nowDate, 1)
    this.dateEnd = this.formatDateCustom(tomorrowData)
  }

  formatDateCustom(millis: number): string {
    const date = new Date(millis)
    const zonedDate = utcToZonedTime(date, 'Etc/GMT')
    return format(zonedDate, 'yyyy-MM-dd')
  }

  addDays(date, days) {
    let result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
  }

  async downloadExel(){
    if (this.user) {
      const params: BalanceMovementListInterface = {
        userGuid: this.user.guid,
        dateRangeStart: this.dateStart,
        dateRangeEnd: this.dateEnd,
        domainName: this.user.domain.name,
        providerTransId: this.tranId,
        transactionType: this.transactionTypes.join(','),
        length: 20000,
      }
      try {
        const response = await this.CashierTransactionsApiService.balanceMovementExl(params)
        if(response.data){
          const filename = response.headers['x-filename'];
          const contentType = response.headers['content-type'];
          const blob = new Blob([response.data], { type: contentType });
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement("a");
          a.setAttribute('href', url);
          a.setAttribute('download', filename);
          document.body.appendChild(a);
          a.click();
          a.remove();
        }
      } catch (e) {
        this.snackbar = {
          show: true,
          text: e.message,
          color: 'error'
        }
      } finally {
        this.loading = false
      }
    }
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EXPORT.' + text)
  }
}
</script>

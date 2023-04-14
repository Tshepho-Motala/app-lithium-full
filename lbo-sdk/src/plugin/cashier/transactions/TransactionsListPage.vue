<template>
  <div>
    <!-- Filter form -->
    <TransactionFilterComponent ref="formFilterComponent" v-show="openFilter"></TransactionFilterComponent>

    <div class="d-flex justify-space-between mb-3">
      <div>
        <v-btn @click="openAddLink()" data-test-id="btn-auto-withdrawal-rule--add" v-role="'AUTOWITHDRAWALS_RULESETS_ADD'" color="success"
               dark>
          <v-icon class="mr-1">mdi-plus</v-icon>
          {{ translateAll('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EXPORT.PAGE.ADD') }}
        </v-btn>
        <v-btn color="primary" dark @click="openFilter = !openFilter">
          <v-icon class="mr-1">mdi-filter</v-icon>
          {{ !openFilter ? translateAll('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EXPORT.PAGE.OPEN-FILTER') : translateAll('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EXPORT.PAGE.CLOSE-FILTER') }}
        </v-btn>
      </div>
      <div class="d-flex">
        <v-btn data-test-id="btn-auto-withdrawal-list--clear" color="error" dark @click="clearFilter()">
          <v-icon class="mr-1">mdi-delete-outline</v-icon>
          {{ translateAll('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EXPORT.PAGE.CLEAR') }}
        </v-btn>
        <template v-if="openFilter">
          <v-btn @click="filterLoad" data-test-id="btn-auto-withdrawal-list--filter" color="primary" dark>
            <v-icon class="mr-1">mdi-filter</v-icon>
            {{ translateAll('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EXPORT.PAGE.FILTER') }}
          </v-btn>
        </template>
        <template v-else>
          <v-btn @click="refreshList()" data-test-id="btn-auto-withdrawal-list--refresh" color="primary" dark>
            <v-icon class="mr-1">mdi-cached</v-icon>
            {{ translateAll('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EXPORT.PAGE.REFRESH') }}
          </v-btn>
          <button-export :has-props="true" :params="exportParams"></button-export>
          <withdrawal-bulk :user-prop="user" :has-props="true" :params="paramsApiList"></withdrawal-bulk>
        </template>
      </div>
    </div>
    <v-data-table
        class="mt-4"
        :headers="headers"
        :items="transactionList"
        :loading="loading"
        :loading-text="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.LOAD_TEXT')"
        :server-items-length="totalItems"
        :options.sync='options'
        :footer-props="{
             'items-per-page-options': [20, 30, 50, 100, 200]
            }"
    >
      <template v-slot:item.link="{ item }">
        <v-btn
            small
            color="primary"
            dark
            @click="openLink(item.id)"
        >
          {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.OPEN')}}
        </v-btn>
      </template>
      <template v-slot:item.remarks="{ item }">
        <span v-if="item.hasRemarks">
          <v-icon small color="grey darken-2">
          mdi-message-text
        </v-icon>
        </span>
      </template>

      <template v-slot:item.createdOn="{ item }">
        <span style="min-width: 100px;display: block;"> {{ formatDate(item.createdOn) }} </span>
      </template>
      <template v-slot:item.updatedOn="{ item }">
        <span style="min-width: 100px;display: block;"
              v-if="item && item.current"> {{ formatDate(item.current.timestamp) }} </span>
      </template>
      <template v-slot:item.status="{ item }">
        <v-chip small v-if="item && item.current " text-color="white" :color="getColorStatus(item.current.status.code)"
                label>{{
          translateService.instant('UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.' + item.current.status.code)
          }}
        </v-chip>
      </template>
      <template v-slot:item.amountCents="{ item }">
        <span> {{ item.currencyCode }}{{ formatCurrencyNumberCent(item.amountCents) }} </span>
      </template>
      <template v-slot:item.descriptor="{ item }">
        <span
            v-if="item.paymentMethod && item.paymentMethod.lastFourDigits"> {{ item.paymentMethod.lastFourDigits
          }}</span>
        <span v-else> N/A </span>
      </template>

      <template v-slot:item.declineReason="{ item }">
        <span> {{ reasonParser(item.declineReason) }} </span>
      </template>
      <template v-slot:item.tags="{ item }">
        <div v-if="item.tags.length">
          <v-chip x-small v-for="(tag,i) in item.tags" :key="i" text-color="white" color="info" label>
            {{tag}}
          </v-chip>
        </div>

      </template>
    </v-data-table>
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
import {Component, Inject, Mixins, Watch} from 'vue-property-decorator'
import '@/core/directive/role-check/RoleCheckDirective'
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import {format, utcToZonedTime} from "date-fns-tz";
import TransactionFilterComponent from "@/plugin/cashier/transactions/list-components/TransactionFilterComponent.vue";
import WithdrawalBulk from "@/plugin/cashier/withdrawal-bulk/WithdrawalBulk.vue";
import {
  CashierTransactionsApiOrderInterface, CashierTransactionsApiParamsInterface,
  CashierTransactionsApiSizeInterface,
  CashierTransactionsDetailInterface
} from "@/core/interface/cashier/cashierTransactionsListInterface";
import {DomainItemInterface} from "@/plugin/cms/models/DomainItem";
import ButtonExport from "@/plugin/csv-export/ButtonExport.vue";
import {UserInterface} from "@/core/interface/cashier/cashierTransactions";
import {AxiosApiClientsInterface} from "@/core/axios/AxiosApiClients";

@Component({
  components: {TransactionFilterComponent, WithdrawalBulk, ButtonExport}
})
export default class TransactionsListPage extends Mixins(cashierMixins) {
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface
  $refs!: {
    formFilterComponent: HTMLFormElement
  }
  totalItems: number = 0
  domain: DomainItemInterface | undefined = undefined
  openFilter: boolean = false
  transactionList: CashierTransactionsDetailInterface[] = []
  loading: boolean = false
  options: CashierTransactionsApiSizeInterface = {
    page: 1,
    itemsPerPage: 20
  }
  paramsSort: CashierTransactionsApiOrderInterface = {sort: 'desc', order: 'id'}
  paramsApiList: CashierTransactionsApiParamsInterface | null = null
  user: null | UserInterface = null
  headers = [
    {text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.ID'), sortable: true, value: 'id'},
    {text: '', sortable: false, value: 'link'},
    {text: '', sortable: false, value: 'remarks'},
    {text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.CREATED'), sortable: true, value: 'createdOn'},
    {text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.UPDATED'), sortable: false, value: 'updatedOn'},
    {text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.STATUS'), sortable: true, value: 'status'},
    {text: 'Type', sortable: false, value: 'transactionType'},
    {text: 'Amount', sortable: true, value: 'amountCents'},
    {
      text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.PROCESSOR'),
      sortable: true,
      value: 'current.processor.description'
    },
    {
      text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.METHOD'),
      sortable: true,
      value: 'domainMethod.name'
    },
    {text: 'Payment Type', sortable: true, value: 'transactionPaymentType.paymentType'},
    {text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.PLAYER'), sortable: true, value: 'user.guid'},
    {text: 'Descriptor', sortable: false, value: 'descriptor'},
    {text: 'Decline Reason', width: '200px', sortable: false, value: 'declineReason'},
    {text: 'Processor Ref', width: '150px', sortable: false, value: 'processorReference'},
    {text: 'Additional Ref', sortable: false, value: 'additionalReference'},
    {text: 'Tags', sortable: false, value: 'tags'},
    {
      text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.IS_TEST_ACCOUNT'),
      sortable: true,
      value: 'user.testAccount'
    },
    {
      text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.AUTO_APPROVED'),
      sortable: false,
      value: 'autoApproved'
    },
    {
      text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.REVIEWED_BY'),
      sortable: false,
      value: 'reviewedByFullName'
    },
    {
      text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.DIRECT_WITHDRAWAL'),
      sortable: true,
      value: 'directWithdrawal'
    },
    {
      text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.INITIATED_BY'),
      sortable: false,
      value: 'initiationAuthorFullName'
    },
    {text: 'Runtime', sortable: false, value: 'runtime'},
    {text: 'Account Info', sortable: true, value: 'accountInfo'},
    {text: 'Bonus Code', sortable: true, value: 'bonusCode'},
    {text: 'Bonus ID', sortable: true, value: 'bonusId'},

  ]
  @Watch('options')
  onFieldChange = (val) => {
    if (val && val.sortBy?.length) this.paramsSort.order = val.sortBy[0]
    if (val && val.sortDesc.length && val.sortDesc[0]) {
      this.paramsSort.sort = 'asc'
    } else {
      this.paramsSort.sort = 'desc'
    }
    this.loadTransactionList()
  }

  mounted() {
    const domain: any = window.localStorage.getItem('domain-name')
    this.domain = JSON.parse(domain)
  }

  async loadTransactionList() {
    this.loading = true
    const params = this.$refs.formFilterComponent.apiParams()
    this.paramsApiList = this.$refs.formFilterComponent.apiParams()
    this.paramsSort.page = this.options.page - 1
    this.paramsSort.size = this.options.itemsPerPage
    try {
      const result = await this.apiClients.serviceCashier.cashierTransactionList(params, this.paramsSort)
      if (result !== null) {
        this.transactionList = result.data
        this.totalItems = result.recordsTotal
      }
    } catch (err) {
      this.snackbar = {
        show: true,
        text: this.translateAll(err.message),
        color: 'error'
      }
    } finally {
      this.loading = false
    }
  }

  formatDateCustom(millis: number): string {
    const date: Date = new Date(millis)
    const zonedDate = utcToZonedTime(date, 'Etc/GMT')
    return format(zonedDate, 'yyyy-MM-dd')
  }

  async filterLoad() {
    await this.loadTransactionList()
  }

  async clearFilter() {
    this.$refs.formFilterComponent.clearFilter()
    this.options.page = 1
    await this.loadTransactionList()
  }

  async refreshList() {
    await this.loadTransactionList()
  }

  openLink(id) {
    window.open(`#/dashboard/cashier/${this.domain?.name}/view/${id}`, '_blank');
  }
  openAddLink() {
    if (this.domain?.name){
      this.rootScope.provide.cashierProvider.openTransactionAdd(this.domain?.name)
    }
  }
  reasonParser(data) {
    if (data !== null && data.length > 0) {
      const text = data.replaceAll("\"", "&quot;")
      return text.substring(0, 120) + (text.length > 120 ? '...' : '');
    }
  }
  get exportReference(){

    if (this.user?.guid){
      const reference:any = window.localStorage.getItem(`export_reference_transactions_${this.user.guid}`)
      if(!reference) {
        return null;
      }
      return reference.replace('export_reference_transactions_', '')
    }
    const reference:any = window.localStorage.getItem(`export_reference_transactions_list`)
    if(!reference) {
      return null;
    }
    return reference
  }
  get exportParams(){
    const paramsFilter:any = this.paramsApiList
    if(paramsFilter && paramsFilter.includedTransactionTagsNames) {
      paramsFilter.includedTransactionTagsNames = paramsFilter.includedTransactionTagsNames.join(',')
    }
    if(paramsFilter && paramsFilter.excludedTransactionTagsNames) {
      paramsFilter.excludedTransactionTagsNames = paramsFilter.excludedTransactionTagsNames.join(',')
    }
    if(paramsFilter && paramsFilter.statuses) {
      paramsFilter.statuses = paramsFilter.statuses.join(',')
    }
    const config:any =  {
      domain: this.domain?.name,
      provider: 'service-csv-provider-cashier-transactions',
      page: 0,
      size: 10,
      parameters: paramsFilter,
      reference: this.exportReference
    };
    if (this.user?.guid) config.userGuid = this.user.guid;
    return config
  }
}
</script>

<style scoped>
</style>

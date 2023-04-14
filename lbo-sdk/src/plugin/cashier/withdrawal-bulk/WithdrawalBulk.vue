<template>
  <div>
    <v-tooltip bottom>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
            :style="hasProps ? 'text-transform: capitalize;' : 'height: 34px;border: 1px solid #ddd;box-shadow: none!important; text-transform: capitalize;'"
            elevation="2"
            v-bind="attrs"
            v-on="on"
            @click="openDialog"
        >
          <v-icon left>mdi-format-list-checks</v-icon>
          Bulk Action
        </v-btn>
      </template>
      <span>Review and process all pending withdrawals <span
          v-if="user"> for {{user.firstName}} {{user.lastName}} ({{user.username}})</span></span>
    </v-tooltip>
    <v-dialog
        v-model="dialog"
        width="1350"
        @click:outside="close"
    >
      <v-card>
        <v-toolbar
            color="primary"
            dark
            style="font-size: 22px"
        >
          <div class="d-flex justify-space-between align-content-center align-center" style="width: 100%">
            <span> Pending Withdrawals <span
                v-if="user"> for {{user.firstName}} {{user.lastName}} ({{user.username}})</span> </span>
            <v-chip @click="close" small
                    text-color="white"
                    color="error" label>
              <v-icon>
                mdi-close
              </v-icon>
            </v-chip>
          </div>
        </v-toolbar>
        <v-card-text class="mt-4">
          <div class="last-transactions__wrapper">
            <div v-if="errorTransactions.length">
              <v-alert type="error">
                This transactions that failed, do the refresh and retry the operation
              </v-alert>
              <v-simple-table>
                <template v-slot:default>
                  <thead>
                  <tr>
                    <th class="text-left">ID</th>
                    <th class="text-left">Created</th>
                    <th class="text-left">Type</th>
                    <th class="text-left">Processor</th>
                    <th class="text-left">Amount</th>
                    <th class="text-left">Player</th>
                    <th class="text-left">Status</th>
                    <th class="text-left">Comment</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr
                      v-for="item in errorTransactions"
                      :key="item.id"
                  >
                    <td><span> {{ item.id }} </span></td>
                    <td><span> {{ formatDate(item.createdOn) }} </span></td>
                    <td><span> {{item.transactionType}} </span></td>
                    <td><span> {{item.processorDescription}} </span></td>
                    <td><span> {{ formatCurrency(item) }} </span></td>
                    <td><span> {{ item.user.guid }} </span></td>
                    <td><v-chip text-color="white" color="error" label>Error Bulk</v-chip></td>
                    <td><span> {{ item.comment }} </span></td>
                  </tr>
                  </tbody>
                </template>
              </v-simple-table>
            </div>


            <v-alert
                v-if="approvedTransactions.length && !errorTransactions.length"
                type="success"
            >
              All transactions are successfully approved
            </v-alert>

            <v-data-table
                v-if="!errorTransactions.length"
                :headers="headers"
                v-model="selected"
                :items="transactionList"
                :footer-props="{
                  'items-per-page': 10,
                  'items-per-page-options': [10, 20, 30, 50, -1]
                }"
                :loading="loading"
                loading-text="Loading... Please wait"
                :single-select="singleSelect"
                item-key="id"
                :sort-by="'createdOn'"
                :sort-desc="true"
                show-select
                @toggle-select-all="selectAllToggle"
            >
              <template v-slot:item.data-table-select="{ item, isSelected, select }">
                <v-simple-checkbox
                    :value="isSelected"
                    @input="select($event)"
                ></v-simple-checkbox>
              </template>
              <template v-slot:item.createdOn="{ item }">
                <span> {{ formatDate(item.createdOn) }} </span>
              </template>
              <template v-slot:item.processor="{ item }">
                <span> {{item.processorDescription}} </span>
              </template>
              <template v-slot:item.descriptor="{ item }">
                <span
                    v-if="item.paymentMethod && item.paymentMethod.lastFourDigits"> {{item.paymentMethod.lastFourDigits}}</span>
                <span v-else> N/A </span>
              </template>
              <template v-slot:item.amount="{ item }">
                <span> {{ formatCurrency(item) }} </span>
              </template>
              <template v-slot:item.status="{ item }">
                <v-chip text-color="white" color="info" label>{{
                  translateService.instant('UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.' + item.status.code)
                  }}
                </v-chip>
              </template>
            </v-data-table>

            <v-alert
                border="top"
                colored-border
                type="info"
                elevation="2"
                class="mt-3"
                v-if="isNotApprove"
            >
              You have marked a transaction that cannot be approved
            </v-alert>
            <v-alert
                border="top"
                colored-border
                type="info"
                elevation="2"
                class="mt-3"
                v-if="isNotHold"
            >
              You have marked a transaction that cannot be moved to on hold
            </v-alert>
          </div>

        </v-card-text>

        <v-card-actions>
          <div style="width: 350px;">
            <v-select
                outlined
                dense
                v-model="statuses"
                :label="translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.HEAD.STATUS')"
                :items="statusesList"
                item-value="code"
                item-text="name"
                multiple
                clearable></v-select>
          </div>

          <v-spacer></v-spacer>

          <div>
            <v-btn
                color="blue-grey"
                class="white--text mb-3"
                @click="loadData"
            >
              <v-icon left>
                mdi-cached
              </v-icon>
              {{ translateAll('GLOBAL.ACTION.REFRESH') }}
            </v-btn>

            <v-btn v-bind="attrs"

                   v-on="on" color="error" @click="cancelTransactions"
                   :disabled="selected.length === 0 || loadSend" :loading="loadSend"
                   class="white--text mb-3">
              <v-icon left dark> mdi-cancel</v-icon>
              {{ translateAll('GLOBAL.ACTION.CANCEL') }}
            </v-btn>
            <v-btn v-bind="attrs"
                   data-test-id="btn-cashier-transactions-actions-block-oh-hold"
                   :disabled="isNotHold || selected.length === 0 || loadSend"
                   v-on="on"  @click="approveTransaction('HOLD_PENDING_WITHDRAWALS')" :loading="loadSend" color="primary"

                   class="white--text mb-3" >
              <v-icon left dark> mdi-clock-outline</v-icon>
              {{ translateAll('GLOBAL.ACTION.ON_HOLD') }}
            </v-btn>
            <v-btn
                color="success"
                class="mb-3"
                @click="approveTransaction('APPROVE_WITHDRAWALS')"
                :loading="loadSend"
                :disabled="isNotApprove || selected.length === 0 || loadSend"
            >
              <v-icon left>
                mdi-checkbox-marked-outline
              </v-icon>
              {{ translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.APPROVED') }}
              <template v-slot:loader>
                <span>Loading...</span>
              </template>
            </v-btn>
          </div>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>


<script lang="ts">
import {Component, Mixins, Prop, Watch} from "vue-property-decorator";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import CashierTransactionsApiInterface from "@/core/interface/axios-api/CashierTransactionsApiInterface";
import CashierTransactionsApi from "@/core/axios/axios-api/CashierTransactionsApi";
import {
  approveTransactionsInterface,
  CashierTransactionsDataInterface, transactionsListParamsInterface,
  UserInterface
} from "@/core/interface/cashier/cashierTransactions";
import {CashierTransactionsApiParamsInterface} from "@/core/interface/cashier/cashierTransactionsListInterface";

@Component
export default class WithdrawalBulk extends Mixins(cashierMixins) {
  @Prop({default: false }) hasProps?: Boolean
  @Prop() params?: CashierTransactionsApiParamsInterface | null
  @Prop() userProp?: null | UserInterface
  CashierTransactionsApiService: CashierTransactionsApiInterface = new CashierTransactionsApi(this.userService)
  dialog: boolean = false
  paramsApi: transactionsListParamsInterface | null = null
  transactionList: CashierTransactionsDataInterface[] = []
  loading: boolean = false
  statuses: string[] = ['WAITFORAPPROVAL','ON_HOLD']
  statusesList = [
    {name: 'WAIT FOR APPROVAL', code: 'WAITFORAPPROVAL'},
    {name: 'ON HOLD', code: 'ON_HOLD'},
  ]
  draw: number = 1
  headers = [
    {text: 'ID', sortable: true, value: 'id'},
    {text: 'Created', sortable: true, value: 'createdOn'},
    {text: 'Type', sortable: false, value: 'transactionType'},
    {text: 'Processor', sortable: true, value: 'processor'},
    {text: 'Amount', sortable: true, value: 'amount'},
    {text: 'Player', sortable: true, value: 'user.guid'},
    {text: 'Descriptor', sortable: false, value: 'descriptor'},
    {text: 'Status', sortable: false, value: 'status'},
    {text: 'Comment', sortable: false, value: 'comment'}
  ]
  loadSend: boolean = false
  singleSelect: boolean = false
  selected: CashierTransactionsDataInterface[] = []
  approvedTransactions: Number[] = []
  errorTransactions: CashierTransactionsDataInterface[] = []
  user: null | UserInterface = null

  @Watch('statuses')
  async onStatusesChange() {
    await this.loadData()
  }
  async openDialog() {
    this.dialog = true
    await this.loadData()
  }

  selectAllToggle(props) {
    if(this.selected.length != this.selectableTransactionCount()) {
      this.selected = [];
      const self = this;
      props.items.forEach(item => {
        if(item.canApprove) {
          self.selected.push(item);
        }
      });
    } else this.selected = [];
  }

  selectableTransactionCount() {
    let count = 0;
    this.transactionList.forEach(item => {
      if(item.canApprove) {
        count++;
      }
    });
    return count;
  }

  async loadData() {
    this.approvedTransactions = []
    this.errorTransactions = []
    this.selected = []
    if(this.hasProps && this.userProp !== undefined){
      this.user = this.userProp
    }
    else {
      this.user = await this.rootScope.provide.bulkTransactionProvider.selectedUser
    }

    await this.loadParams()
    await this.loadTransactionList()
  }

  async loadParams() {
    let params: any = null
    if(this.hasProps && this.params !== null){
      params = this.params
    }  else {
      params = await this.rootScope.provide.bulkTransactionProvider.getParams()
    }

    params.draw = this.draw
    params.start = 0
    params.length = 100000
    params.transactionType = 'withdraw'
    if (this.statuses.length){
      params.statuses = this.statuses.join(',')
    } else {
      params.statuses = 'WAITFORAPPROVAL,ON_HOLD'
    }
    this.paramsApi = params
  }

  async loadTransactionList() {
    this.loading = true
    if (this.paramsApi) {

      try {
        const result = await this.CashierTransactionsApiService.cashierTransactionBulkList(this.paramsApi)
        if (result?.data) {
          this.transactionList = result.data.data
          this.draw++
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
  }

  async approveTransaction(code) {
    const ids: number[] = []
    this.selected.forEach((el: CashierTransactionsDataInterface) => ids.push(el.id))
    this.loadSend = true
    const params: approveTransactionsInterface = {
      ids: ids.join(','),
      code: code
    }
    try {
      const result = await this.CashierTransactionsApiService.cashierSendBulkList(params)
      if (result?.data) {
        const errorTransactionsIds = result.data.data.failedIds
        if (errorTransactionsIds.length) {
          errorTransactionsIds.forEach((tran: any) => {
            this.selected.forEach((el: CashierTransactionsDataInterface) => {
              if (el.id === tran) {
                this.errorTransactions.push(el)
              }
            })
          })
        }
        this.approvedTransactions = result.data.data.proceedIds
        await this.loadParams()
        await this.loadTransactionList()
        this.selected = []
      }
    } catch (err) {
      this.errorTransactions = this.selected
      this.snackbar = {
        show: true,
        text: this.translateAll(err.message),
        color: 'error'
      }
    } finally {
      this.loadSend = false
    }
  }

  async cancelTransactions(){
    const ids: number[] = []
    this.selected.forEach((el: CashierTransactionsDataInterface) => ids.push(el.id))
    this.loadSend = true
    const params: approveTransactionsInterface = {
      ids: ids.join(',')
    }
    try {
      const result = await this.CashierTransactionsApiService.cancelTransactionsBulkProcessing(params)
      if (result?.data) {
        const errorTransactionsIds = result.data.data.failedIds
        if (errorTransactionsIds.length) {
          errorTransactionsIds.forEach((tran: any) => {
            this.selected.forEach((el: CashierTransactionsDataInterface) => {
              if (el.id === tran) {
                this.errorTransactions.push(el)
              }
            })
          })
        }
        this.approvedTransactions = result.data.data.proceedIds
        await this.loadParams()
        await this.loadTransactionList()
        this.selected = []
      }
    } catch (err) {
      this.errorTransactions = this.selected
      this.snackbar = {
        show: true,
        text: this.translateAll(err.message),
        color: 'error'
      }
    } finally {
      this.loadSend = false
    }
  }

  get isNotApprove(){
    return  this.selected.some((el) => el.canApprove === false)
  }

  get isNotHold(){
    return  this.selected.some((el) => el.canOnHold === false)
  }

  close() {
    this.approvedTransactions = []
    this.errorTransactions = []
    this.selected = []
    this.dialog = false
  }

  formatCurrency(item: any): string {

    if (item.amountCents != null) {
      const amountValue = Number(item.amountCents) / 100
      return item.currencyCode + amountValue.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
    } else {
      return 'Not specified'
    }
  }
}
</script>

<style scoped></style>

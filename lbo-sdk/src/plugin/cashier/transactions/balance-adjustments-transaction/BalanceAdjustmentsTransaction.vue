<template>
  <div>
    <v-tooltip bottom>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
            style="height: 34px;border: 1px solid #ddd;box-shadow: none!important; text-transform: capitalize;"
            elevation="2"
            v-bind="attrs"
            v-on="on"
            @click="openDialog"
        >
         {{translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.BUTTON_TITLE")}}
        </v-btn>
      </template>
      <span>  {{translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.MODAL_TITLE")}}</span>
    </v-tooltip>
    <v-dialog
        v-model="dialog"
        width="750"
        @click:outside="close"
    >
      <v-card>
        <v-toolbar
            color="primary"
            dark
            style="font-size: 22px"
        >
          <div class="d-flex justify-space-between align-content-center align-center" style="width: 100%">
            <span> {{translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.MODAL_TITLE")}} </span>

            <v-chip @click="close" small
                    text-color="white"
                    color="error" label>
              <v-icon>
                mdi-close
              </v-icon>
            </v-chip>
          </div>
        </v-toolbar>
        <v-card-text v-if="transactionData && transactionData.manualCashierAdjustmentAccountCodes" class="mt-4">
          <div class="last-transactions__wrapper">

            <v-simple-table class="mt-3 mb-3" data-test-id="tbl-cashier-transaction-info">
              <template v-slot:default>
                <tbody>
                <tr>
                  <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                    {{translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.TABLE.AMOUNT")}}
                  </th>
                  <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                    {{ transactionData.currencyCode }} {{ formatCurrencyNumberCent(transactionData.amountCents) }}
                  </td>
                </tr>
                <tr>
                  <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                    {{translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.TABLE.OPERATION")}}
                  </th>
                  <td v-if="transactionData && transactionData.manualCashierAdjustmentAccountCodes[0].debit"
                      style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;"> {{translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.TABLE.DEBIT")}}
                  </td>
                  <td v-else style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;"> {{translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.TABLE.CREDIT")}}</td>
                </tr>
                <tr>
                  <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                    {{translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.TABLE.LINKED")}}
                  </th>
                  <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;"> {{ transactionData.id }}
                  </td>
                </tr>
                <tr>
                  <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                    {{translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.TABLE.ACCOUNT_CODE")}}
                  </th>
                  <td  v-if="transactionData.manualCashierAdjustmentAccountCodes.length > 1" style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                    <v-select
                        :items="transactionData.manualCashierAdjustmentAccountCodes"
                        v-model="accountCode"
                        item-text="code"
                        item-value="code"
                        outlined
                    ></v-select>
                  </td>
                  <td v-else style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                    {{ transactionData.manualCashierAdjustmentAccountCodes[0].code }}
                  </td>

                </tr>
                </tbody>
              </template>
            </v-simple-table>

            <p>
              <span style="display: block">{{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.TRANSACTION_TYPE_DESCRIPTION_TITLE') }}</span>
              <span style="display: block">{{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.ACCOUNT_TYPE_CODE.DESCRIPTION.' + transactionData.manualCashierAdjustmentAccountCodes[0].code) }}</span>
            </p>

            <v-form
                ref="form"
                v-model="form"
            >
              <v-textarea
                  v-model="comment"
                  auto-grow
                  required
                  :rules="[rules.required]"
                  filled
                  color="deep-purple"
                  :label="translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.TABLE.COMMENT')"
                  rows="1"
              ></v-textarea>
            </v-form>
          </div>

        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <p style="font-size: 18px">{{translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.MESSAGE")}}</p>
          <v-spacer></v-spacer>
          <v-btn v-bind="attrs"
                 v-on="on" color="error"
                 @click="close"
                 class="white--text mb-3">
            <v-icon left dark> mdi-cancel</v-icon>
            {{ translateAll('GLOBAL.ACTION.CANCEL') }}
          </v-btn>

          <v-btn
              color='primary'
              class="white--text mb-3"
              @click="send"
              :disabled="loadSend" :loading="loadSend"
          >
            <v-icon left dark> mdi-send</v-icon>
            {{translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.SUBMIT")}}
          </v-btn>


        </v-card-actions>
        <v-snackbar
            v-model="snackbar.show"
            :timeout="2000"
            :color="snackbar.color"
        >
          {{ snackbar.text }}

        </v-snackbar>
      </v-card>
    </v-dialog>
  </div>
</template>


<script lang="ts">
import {Component, Mixins} from "vue-property-decorator";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import CashierTransactionsApiInterface from "@/core/interface/axios-api/CashierTransactionsApiInterface";
import CashierTransactionsApi from "@/core/axios/axios-api/CashierTransactionsApi";
import {
  bulkTransactionsInterface,
  CashierTransactionsDataInterface,
  UserInterface
} from "@/core/interface/cashier/cashierTransactions";

@Component
export default class BalanceAdjustmentsTransaction extends Mixins(cashierMixins) {
  CashierTransactionsApiService: CashierTransactionsApiInterface = new CashierTransactionsApi(this.userService)
  dialog: boolean = false
  transactionID: Number | undefined = undefined
  transactionData: CashierTransactionsDataInterface | undefined = undefined
  loading: boolean = false
  comment: string = ''
  loadSend: boolean = false
  singleSelect: boolean = false
  selected: CashierTransactionsDataInterface[] = []
  approvedTransactions: Number[] = []
  errorTransactions: CashierTransactionsDataInterface[] = []
  user: null | UserInterface = null
  form: boolean = false
  accountCode:string = ''
  $refs!: {
    form: any
  }

  async openDialog() {
    this.dialog = true
    await this.loadData()
  }

  mounted() {
    this.loadData()
  }

  async loadData() {
    await this.loadTransactionDataInformation()
  }

  async loadTransactionDataInformation() {
    this.transactionData = this.rootScope.provide.cashierProvider.transaction
    if( this.transactionData && this.transactionData.manualCashierAdjustmentAccountCodes?.length){
      this.accountCode = this.transactionData.manualCashierAdjustmentAccountCodes[0].code
    }
  }

  async send() {
    this.$refs.form.validate()
    if (this.form && this.transactionData) {
      const params: bulkTransactionsInterface = {
        transactionId: this.transactionData.id,
        accountCode: this.accountCode,
        comment: `${this.comment}`
      }
      try {
        const result = await this.CashierTransactionsApiService.cashierTransactionBalanceAdjust(params)
        if (result?.data?.successful) {
          this.snackbar = {
            show: true,
            text: this.translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.SUCCESS"),
            color: 'success'
          }
          this.rootScope.provide.cashierProvider.refreshTransaction()
          this.close()
        } else {
          this.snackbar = {
            show: true,
            text: result.data.message,
            color: 'error'
          }
        }
      } catch (err) {
        this.logService.error(err)
        let message = this.translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.GENERAL_ERROR")
        if (err.response?.data?.errorCode === "lithium.exceptions.Status415NegativeBalanceException"){
          message = this.translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.NEGATIVE_BALANCE_ERROR")
        }
        this.snackbar = {
          show: true,
          text: message,
          color: 'error'
        }
      }
    }
  }

  close() {
    this.dialog = false
  }


}
</script>

<style scoped></style>

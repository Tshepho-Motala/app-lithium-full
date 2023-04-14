<template>
  <div data-test-id="cnt-cashier-transaction-bank-account">
    <div class="box box-solid box-default">
      <div class="box-header with-border" style="display: flex; align-items: center; height: 42px">
        <p class="pull-left" style="margin: 0">
          {{ translateAll('UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP_TABLE.BANK_ACCOUNT_LOOKUP') }}
        </p>
        <button
            data-test-id="btn-refresh"
            @click="refresh()"
            type="button"
            class="btn btn-default pull-right"
            style="background: #fff; margin-left: auto"
        >
          <i class="fa fa-refresh"></i><span class="ng-binding"> {{ translateAll('GLOBAL.ACTION.REFRESH') }}</span>
        </button>
      </div>
      <div class="box-body table-responsive">
        <v-simple-table data-test-id="tbl-bank-account" v-if="isPending"
                        loading-text="Wait for some time">
          <template v-slot:default>
            <tbody>
            <tr :data-test-id="`tbi-bank-account-${index}`" v-for="(item, index) in items" :key="`name${index}`">
              <td style="font-weight: 600; font-size: 14px; padding: 0 5px; width: 27%">{{ translateAll(item.text) }}
              </td>
              <td>{{ item.value }}</td>
            </tr>
            </tbody>
          </template>
        </v-simple-table>
        <div v-else class="d-flex" style="justify-content: center">
          <v-progress-circular indeterminate color="primary"></v-progress-circular>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Mixins, Prop,} from 'vue-property-decorator'

import CashierTransactionsApiInterface from "@/core/interface/axios-api/CashierTransactionsApiInterface";
import CashierTransactionsApi from "@/core/axios/axios-api/CashierTransactionsApi";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import {
  bankAccountLookupParamsInterface,
  CashierTransactionsDataInterface
} from "@/core/interface/cashier/cashierTransactions";

@Component
export default class TransactionBankAccountLookupTable extends Mixins(cashierMixins) {
  @Prop({required: true}) transactionDetail?: CashierTransactionsDataInterface
  CashierTransactionsApiService: CashierTransactionsApiInterface = new CashierTransactionsApi(this.userService)
  model: any = null
  isPending: Boolean = false
  items: any[] = []

  async mounted() {
    await this.loadDataFromController()
  }

  async loadDataFromController() {
    this.isPending = false
    if (this.transactionDetail?.current?.processor) {
      const params: bankAccountLookupParamsInterface = {
        domainName: this.transactionDetail.domainMethod.domain.name,
        processorCode: this.transactionDetail.current.processor.processor.code,
        processorDescription: this.transactionDetail.current.processor.description,
        processorUrl: this.transactionDetail.current.processor.processor.url,
        transactionId: this.transactionDetail.id,
      }

      try {
        const result = await this.CashierTransactionsApiService.bankAccountLookup(params)
        if (result?.data?.successful) {
          this.model = result.data.data
          this.items = []
          this.items.push({
            text: 'UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP_TABLE.STATUS',
            value: this.model.status
          })

          this.items.push({
            text: 'UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP_TABLE.FAILED_STATUS_REASON',
            value: this.model.failedStatusReasonMessage
          })

          this.items.push({
            text: 'UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP_TABLE.ACCOUNT_NAME',
            value: this.model.accountName
          })

          this.items.push({
            text: 'UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP_TABLE.ACCOUNT_NUMBER',
            value: this.model.accountNumber
          })

          this.items.push({
            text: 'UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP_TABLE.BANK_CODE',
            value: this.model.bankCode
          })

          this.items.push({
            text: 'UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP_TABLE.BANK_NAME',
            value: this.model.bankName
          })
        }
      } catch (err) {
        this.logService.error(err)
      } finally {
        this.isPending = true
      }
    }

  }

  refresh() {
    this.loadDataFromController()
  }
}
</script>
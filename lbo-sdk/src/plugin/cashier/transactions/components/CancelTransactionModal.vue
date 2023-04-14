<template>
  <div>
    <v-dialog
        data-test-id="cnt-cashier-transaction-cancel-modal"
        v-model="dialog"
        max-width="400px"
        @click:outside="close"
    >
      <v-card>
        <v-toolbar
            color="primary"
            dark
            style="font-size: 22px"
        > Cancel transaction
        </v-toolbar>
        <v-card-text class="mt-4">
          <v-select
              v-model="selectReason"
              :items="cancelReasonsList"
              label="Reason for cancellation"
              item-value="value"
              data-test-id="slt-cashier-transaction-cancel-reason"

          >
            <template v-slot:selection="{ item }">
              {{ translateAll(item.text) }}
            </template>
            <template v-slot:item="{ item }">
              {{ translateAll(item.text) }}
            </template>
          </v-select>
          <div v-if="transactionDetail.current.status.code === 'APPROVED'" style="align-items: center">
            <div style="display: flex; justify-content: center;align-items: center;">
              <i class="fa fa-warning mr-2" style="font-size:36px;color:red;"></i>
              <H4>
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.CANCEL_AFTER_APPROVE.CONFIRM_MAIN') }}</H4>
            </div>
            <br>
            <H4>
              {{translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.CANCEL_AFTER_APPROVE.CONFIRM_ADDITIONAL') }}</H4>
          </div>
          <div v-else-if="transactionDetail.current.status.code === 'AUTO_APPROVED'" style="align-items: center">
            <div style="display: flex; justify-content: center;align-items: center;">
              <i class="fa fa-warning mr-2" style="font-size:36px;color:red"></i>
              <H4>
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.CANCEL_AFTER_APPROVE.CONFIRM_MAIN')}}</H4>
              <br>
            </div>
            <br>
            <H4>
              {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.CANCEL_AFTER_APPROVE.CONFIRM_ADDITIONAL')}}</H4>
          </div>
          <div v-else-if="transactionDetail.current.status.code === 'WAITFORPROCESSOR'" style="align-items: center">
            <div style="display: flex; justify-content: center;align-items: center;">
              <i class="fa fa-warning mr-2" style="font-size:36px;color:red"></i>
              <H4>
                {{translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.CANCEL_AFTER_APPROVE.CONFIRM_MAIN')}}</H4>
              <br>
            </div>
            <br>
            <H4>
              {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.CANCEL_AFTER_APPROVE.CONFIRM_ADDITIONAL')}}</H4>
          </div>
          <div v-else style="display: flex; justify-content: center;align-items: center;">
            <H4>
              {{translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.CANCEL.CONFIRM')}}</H4>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
              color="error"
              @click="close"
          >
            Cancel
          </v-btn>
          <v-btn
              @click="submit"
              color='success'
          >
            Submit
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import {Component, Mixins, Prop} from "vue-property-decorator";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import {CashierTransactionsDataInterface} from "@/core/interface/cashier/cashierTransactions";


@Component
export default class CancelTransactionModal extends Mixins(cashierMixins) {
  @Prop({required: true}) transactionDetail?: CashierTransactionsDataInterface
  dialog: Boolean = true
  selectReason = 1
  cancelReasonsList = [
    {
      value: 1,
      text: 'UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.CANCEL.REASON.CLOSED_LOOP'
    },
    {
      value: 2,
      text: 'UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.CANCEL.REASON.UNVERIFIED_ACCOUNT'
    },
    {
      value: 3,
      text: 'UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.CANCEL.REASON.PROMO_ABUSER'
    },
    {
      value: 4,
      text: 'UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.CANCEL.REASON.DUPLICATE_ACCOUNT'
    },
    {
      value: 5,
      text: 'UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.CANCEL.REASON.DEPOSIT_WAGERING'
    },
    {
      value: 6,
      text: 'UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.CANCEL.REASON.TECHNICAL_ERROR'
    },
    {
      value: 7,
      text: 'UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.CANCEL.REASON.NAME_MISMATCHED'
    },
    {
      value: 8,
      text: 'UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.CANCEL.REASON.CUSTOMER_REQUEST'
    },
    {
      value: 9,
      text: 'UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.CANCEL.REASON.WELCOME_BONUS'
    },
    {
      value: 10,
      text: 'UI_NETWORK_ADMIN.ACCOUNTING.TRANSACTIONS.FIELDS.CANCEL.REASON.OTHERS'
    }
  ]

  close() {
    this.$emit('cancel')
  }

  submit() {
    const elem: any = this.cancelReasonsList.find((el: any) => el.value === this.selectReason)
    const comment = this.translateAll(elem.text)
    this.$emit('submit', comment)
  }


}
</script>

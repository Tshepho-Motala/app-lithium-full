<template>
  <v-container v-if="transactionDetail" data-test-id="cnt-cashier-transaction-actions-block">
    <v-row>
      <v-toolbar flat class="bg--gray rounded-t" dark height="42">
        <v-toolbar-title>Transaction Actions</v-toolbar-title>
      </v-toolbar>
    </v-row>
    <v-row>
      <v-card style="width: 100%; box-shadow: none; border-radius: 0;" class=" w-100 pl-4 pr-4 pt-4 pb-2 mb-4">
        <div style="border-bottom: 1px solid #e5e5e5;padding-bottom: 10px;margin-bottom: 9px;">
          <v-btn @click="refreshTransaction" color="blue-grey" class="white--text" small>
            <v-icon left dark> mdi-cached</v-icon>
            {{ translateAll('GLOBAL.ACTION.REFRESH') }}
          </v-btn>
          <v-tooltip v-if="isRetryButton(transactionDetail.current.status.code) && hasRole('CASHIER_APPROVE')"
                     max-width="250px" bottom
                     color="warning">
            <template v-slot:activator="{ on, attrs }">
              <v-btn
                  data-test-id="btn-cashier-transaction-actions-block-retry"
                  @click="openConfirmDialog('retryTransaction', 'UI_NETWORK_ADMIN.CASHIER.TRANSACTION.RETRY.CONFIRM')"
                  v-bind="attrs"
                  v-on="on" color="warning"
                  class="white--text" small>
                <v-icon left dark> mdi-reload</v-icon>
                {{ translateAll('GLOBAL.ACTION.RETRY') }}
              </v-btn>


            </template>
            <span>{{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.RETRY.EXPLANATION') }}</span>
          </v-tooltip>


          <v-tooltip v-if="isClearButton(transactionDetail.current.status.code) && hasRole('CASHIER_APPROVE')"
                     max-width="300px" bottom color="error">
            <template v-slot:activator="{ on, attrs }">
              <v-btn v-bind="attrs"
                     data-test-id="btn-cashier-transaction-actions-block-clear"
                     v-on="on"
                     @click="openConfirmDialog('clearTransaction', 'UI_NETWORK_ADMIN.CASHIER.TRANSACTION.CLEAR.CONFIRM')"
                     color="error"
                     class="white--text" small>
                <v-icon left dark> mdi-delete-sweep-outline</v-icon>
                {{ translateAll('GLOBAL.ACTION.CLEAR') }}
              </v-btn>


            </template>
            <span>{{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.CLEAR.EXPLANATION') }}</span>
          </v-tooltip>


          <v-tooltip v-if="isCancelButton(transactionDetail.current.status.code) && hasRole('CASHIER_CANCEL')"
                     max-width="300px" bottom
                     color="error">
            <template v-slot:activator="{ on, attrs }">
              <v-btn v-bind="attrs"
                     v-on="on" @click="cancelTransaction" color="error"
                     class="white--text" small>
                <v-icon left dark> mdi-cancel</v-icon>
                {{ translateAll('GLOBAL.ACTION.CANCEL') }}
              </v-btn>
            </template>
            <span>{{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.CANCEL.EXPLANATION') }}</span>
          </v-tooltip>


          <v-tooltip v-if="isApproveButton(transactionDetail.current.status.code) && hasRole('CASHIER_APPROVE') "
                     max-width="300px" bottom
                     color="success">
            <template v-slot:activator="{ on, attrs }">
              <v-btn v-bind="attrs"
                     v-on="on"
                     data-test-id="btn-cashier-transaction-actions-block-approve"
                     :disabled="transactionDetail.approveDisabled"
                     @click="openConfirmDialog('markApproved', 'UI_NETWORK_ADMIN.CASHIER.TRANSACTION.APPROVE.CONFIRM')"
                     color="success"
                     class="white--text" small>
                <v-icon left dark> mdi-checkbox-marked-circle-outline</v-icon>
                {{ translateAll('GLOBAL.ACTION.APPROVE') }}
              </v-btn>
            </template>
            <span>{{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.APPROVE.EXPLANATION') }}</span>
          </v-tooltip>


          <v-tooltip v-if="isHoldButton(transactionDetail.current.status.code)" max-width="300px" bottom
                     color="primary">
            <template v-slot:activator="{ on, attrs }">
              <v-btn v-bind="attrs"
                     data-test-id="btn-cashier-transaction-actions-block-oh-hold"
                     :disabled="transactionDetail.approveDisabled"
                     v-on="on" @click="setOnHold" color="primary"
                     class="white--text" small>
                <v-icon left dark> mdi-clock-outline</v-icon>
                {{ translateAll('GLOBAL.ACTION.ON_HOLD') }}
              </v-btn>
            </template>
            <span>{{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.ON_HOLD.EXPLANATION') }}</span>
          </v-tooltip>

          <div v-if="isApproveMessage(transactionDetail.current.status.code) && transactionDetail.approveDisabled"><p
              class="danger-text" style="padding-top: 5px; margin-top: 10px; ">
            {{ transactionDetail.approveMessage }}</p></div>

        </div>

        <div class="row"
             v-if="transactionDetail.current && transactionDetail.current.status && transactionDetail.current.status.code !== 'SUCCESS'">
          <div class="col-lg-5">
            <v-text-field
                label="Transaction Amount"
                data-test-id="txt-cashier-transaction-actions-block-approve"
                filled
                type="number"
                :value="Number(tranAmount).toFixed(2)"
                @input="tranAmountUpdate"
                :disabled="!model.isWithdrawalAndApprovedInWorkflow"
                shaped

            ></v-text-field>
            <p style="margin-top: -20px; font-size: 13px;"> Alter the transaction amount or leave unchanged</p>

            <v-btn data-test-id="btn-cashier-transaction-actions-block-approve-mark"
                   v-if="hasRole('CASHIER_STATUS_UPDATE')"
                   @click="openConfirmDialog('markSuccess', 'UI_NETWORK_ADMIN.CASHIER.TRANSACTION.SUCCESS.CONFIRM')"
                   color="success" class="white--text">
              <v-icon left dark> mdi-check</v-icon>
              {{ translateAll('GLOBAL.ACTION.MARKSUCCESS') }}
            </v-btn>
          </div>

          <div class="col-lg-7">
            <v-alert
                style="font-size: 13px"
                border="top"
                colored-border
                type="info"
                elevation="2"
                v-if="transactionDetail.transactionType === 'DEPOSIT'"
            >
              {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.SUCCESS.EXPLANATION') }}
            </v-alert>
            <v-alert
                style="font-size: 13px"
                border="top"
                colored-border
                type="info"
                elevation="2"
                v-if="transactionDetail.transactionType === 'WITHDRAWAL'"
            >
              {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.SUCCESS.WITHDRAWAL.EXPLANATION') }}
            </v-alert>

          </div>
        </div>


      </v-card>
    </v-row>
  </v-container>

</template>

<script lang="ts">
import {Component, Inject, Mixins, Prop} from 'vue-property-decorator'
import TransactionStatuses from "@/plugin/cashier/transactions/components/TransactionStatuses.vue";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import {ConfirmDialogInterface} from "@/plugin/components/dialog/DialogInterface";
import ListenerServiceInterface from "@/core/interface/service/ListenerServiceInterface";
import {
  CashierTransactionsDataInterface,
  ModelTransactionInterface
} from "@/core/interface/cashier/cashierTransactions";


@Component({
  components: {
    TransactionStatuses,
  }
})
export default class TransactionActionBlock extends Mixins(cashierMixins) {
  @Inject('listenerService') readonly listenerService!: ListenerServiceInterface
  @Prop({required: true}) transactionDetail?: CashierTransactionsDataInterface
  @Prop({required: true}) model?: ModelTransactionInterface
  @Prop({required: true}) tranAmount?: number

  tranAmountUpdate(event) {
    const value = event
    this.$emit('update:tranAmount', Number(value).toFixed(2))
  }

  openConfirmDialog(emmit: string, text: string) {
    const params: ConfirmDialogInterface = {
      title: 'Please confirm',
      text: this.translateAll(text),
      btnPositive: {
        text: 'Confirm',
        color: 'success',
        onClick: async () => {
          this.$emit(emmit)
        }
      },
      btnNegative: {
        text: 'Cancel',
        color: 'error',
        flat: true,
        onClick: () => {
        }
      }
    }
    this.listenerService.call('dialog-confirm', params)
  }

  refreshTransaction() {
    this.$emit('refreshTransaction')
  }

  cancelTransaction() {
    this.$emit('cancelTransaction')
  }

  setOnHold() {
    this.$emit('setOnHold')
  }

  isRetryButton(status: string) {
    if (['VALIDATEINPUT', 'WAITFORPROCESSOR', 'WAITFORAPPROVAL', 'FATALERROR', 'DECLINED'].includes(status)) {
      return true
    } else if (['APPROVED'].includes(status) && this.transactionDetail?.showRertyButtonInApprovedState) {
      return true
    }
    return false
  }

  isApproveButton(status: string) {
    if (['WAITFORAPPROVAL', 'ON_HOLD', 'AUTO_APPROVED_DELAYED'].includes(status)) {
      return true
    }
    return false
  }

  isHoldButton(status: string) {
    if (['WAITFORAPPROVAL', 'AUTO_APPROVED_DELAYED'].includes(status)) {
      return true
    }
    return false
  }

  isClearButton(status: string) {
    if (['VALIDATEINPUT', 'WAITFORPROCESSOR', 'WAITFORAPPROVAL', 'FATALERROR', 'DECLINED'].includes(status)) {
      return true
    }
    return false
  }

  isCancelButton(status: string) {
    if (['VALIDATEINPUT', 'WAITFORPROCESSOR', 'WAITFORAPPROVAL', 'ON_HOLD', 'AUTO_APPROVED_DELAYED', 'PENDING_CANCEL'].includes(status)) {
      return true
    } else if (['APPROVED'].includes(status) && this.transactionDetail?.showRertyButtonInApprovedState) {
      return true
    }
    return false
  }

  isApproveMessage(status: string) {
    if (['WAITFORAPPROVAL', 'ON_HOLD', 'AUTO_APPROVED_DELAYED'].includes(status)) {
      return true
    }
    return false
  }
}
</script>

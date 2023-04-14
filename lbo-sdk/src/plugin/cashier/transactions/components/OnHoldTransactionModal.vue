<template>
  <div>
    <v-dialog
        data-test-id="dialog-transaction-on-hold"
        v-model="dialog"
        max-width="400px"
        @click:outside="close"
    >
      <v-card>
        <v-toolbar
            color="primary"
            dark
            style="font-size: 22px"
        ><i class="fa fa-pencil mr-2"></i>
          <span>{{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.ON_HOLD.TITLE') }} </span>
        </v-toolbar>
        <v-card-text class="mt-8">
          <v-text-field
              data-test-id="txt-cashier-transaction-on-hold-comment"
              label="Reason for the hold"
              v-model="comment"
              required
              filled
          ></v-text-field>
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
              :disabled="!comment"
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
export default class OnHoldTransactionModal extends Mixins(cashierMixins) {
  @Prop({required: true}) transactionDetail?: CashierTransactionsDataInterface
  dialog: Boolean = true
  comment = ''

  close() {
    this.$emit('cancel')
  }

  submit() {
    const reason = this.translateAll('UI_NETWORK_ADMIN.CASHIER.TRANSACTION.ON_HOLD.REMARK') + this.comment
    this.$emit('submit', reason)
  }


}
</script>

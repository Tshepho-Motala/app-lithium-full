<template>
  <div>
    <v-dialog
        data-test-id="dialog-cashier-transaction-edit-payment-modal"

        v-model="dialog"
        max-width="600px"
        @click:outside="close"
    >
      <v-card>
        <v-toolbar
            color="primary"
            dark
            style="font-size: 22px"
        >
          <span>{{ translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.EDIT.TITLE') }} </span>


        </v-toolbar>
        <v-card-text v-if="paymentEditMethod" class="mt-8">

          <div class="mb-4">
            <span class="d-flex mb-2">{{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.METHOD')}}</span>
            <div class="d-flex " style="align-content: center; align-items: center; ">
              <img class="mr-3" v-if="paymentEditMethod.processorIcon"
                   style="width: 100px; height: auto; object-fit: contain;"
                   :src="`data:image/png;base64, ${paymentEditMethod.processorIcon.base64}`"
                   alt="">
              <span>{{paymentEditMethod.name}}</span>
            </div>
          </div>

          <v-form class="mt-8" v-model="valid">
            <v-select
                data-test-id="slt-cashier-transaction-edit-payment-status"
                class="mt-4"
                :items="statuses"
                :label="translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.STATUS') + '*'"
                v-model="model.status"
                item-text="name"
                item-value="id"
                :hint="translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.EDIT.STATUS.DESCRIPTION')"
                required
                persistent-hint
            ></v-select>


            <v-select
                data-test-id="slt-cashier-transaction-edit-payment-verified"
                class="mt-6"
                :items="verifiedModelOptions"
                :label="translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.VERIFIED') + '*'"
                v-model="model.verifiedModel"
                item-text="label"
                item-value="value"
                required
            ></v-select>


            <v-checkbox
                data-test-id="chk-cashier-transaction-edit-payment-contra"
                class="mt-2"
                v-model="model.contraAccount"
                label="Contra Account?"
            ></v-checkbox>

            <v-textarea
                data-test-id="txt-cashier-transaction-edit-payment-comment"
                class="mt-2"
                name="input-7-1"
                height="150px"
                v-model="model.comment"
                :label="translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.EDIT.COMMENT.NAME') + '*'"
                outlined
                :hint="translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.EDIT.COMMENT.DESCRIPTION')"
                persistent-hint
                :rules="nameRules"
                required
            ></v-textarea>
          </v-form>
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
              :disabled="!valid"
              data-test-id="btn-cashier-transaction-edit-payment-send"
          >
            Submit
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
import {Component, Mixins, Prop} from "vue-property-decorator";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import {
  CashierTransactionsDataInterface, paymentMethodStatusUpdatemodel,
  paymentMethodStatusUpdateParamsInterface
} from "@/core/interface/cashier/cashierTransactions";
import CashierTransactionsApiInterface from "@/core/interface/axios-api/CashierTransactionsApiInterface";
import CashierTransactionsApi from "@/core/axios/axios-api/CashierTransactionsApi";


@Component
export default class PaymentEditModal extends Mixins(cashierMixins) {
  @Prop({required: true}) transactionDetail?: CashierTransactionsDataInterface
  @Prop({required: true}) paymentEditMethod: any
  CashierTransactionsApiService: CashierTransactionsApiInterface = new CashierTransactionsApi(this.userService)


  dialog: Boolean = true
  statuses: any[] = []
  model: paymentMethodStatusUpdatemodel = {
    status: undefined,
    verifiedModel: null,
    contraAccount: false,
    comment: ''
  }
  valid: boolean = false
  nameRules: any = [
    v => !!v || 'Field is required',
    v => (v && v.length > 5) || 'Comment must be less than 5 characters',
  ]
  verifiedModelOptions: any[] = [
    {value: 0, label: 'Success'},
    {value: 1, label: 'Fail'},
  ]

  async mounted() {
    this.model.status = this.paymentEditMethod.status.id
    this.model.verifiedModel = this.paymentEditMethod.verified === true ? 0 : this.paymentEditMethod.verified === false ? 1 : 2
    this.model.contraAccount = this.paymentEditMethod.contraAccount
    if (this.paymentEditMethod.verifiedModel === 2) {
      this.verifiedModelOptions.push({value: 2, label: 'Unverified'});
    }
    await this.loadAllStatuses()

  }

  close() {
    this.$emit('cancel')
  }

  async loadAllStatuses() {
    try {
      const result = await this.CashierTransactionsApiService.paymentMethodStatuses()
      if (result?.data?.successful) {
        this.statuses = result.data.data
        this.statuses = this.statuses.filter((el: any) => el.name !== "HISTORIC")
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  getVerified() {
    if (this.model.verifiedModel === undefined || this.model.verifiedModel === null) return null;
    switch (this.model.verifiedModel) {
      case 0:
        return true; // Success
      case 1:
        return false; // Fail
      case 2:
        return null; // Unverified
    }
  }

  async submit() {
    if (this.model?.status && this.model?.status) {
      const params: paymentMethodStatusUpdateParamsInterface = {
        comment: this.model.comment,
        contraAccount: this.model.contraAccount,
        statusId: this.model.status,
        verified: this.getVerified()
      }

      if (this.model.contraAccount && !this.getVerified()) {
        this.snackbar = {
          show: true,
          text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.EDIT.ERROR.UNVERIFIED_CONTRA_ACCOUNT'),
          color: 'error'
        }
      } else if (this.transactionDetail) {
        try {
          const result = await this.CashierTransactionsApiService.paymentMethodStatusUpdate(this.transactionDetail.domainMethod.domain.name, this.paymentEditMethod.id, params)
          if (result?.data?.successful) {
            this.snackbar = {
              show: true,
              text: "Status update success",
              color: 'success'
            }
            this.$emit('cancel')
            this.$emit('reload')

          } else {
            this.snackbar = {
              show: true,
              text: this.translateAll(result.data.message),
              color: 'error'
            }

          }
        } catch (err) {
          this.logService.error(err)
          this.snackbar = {
            show: true,
            text: this.translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.EDIT.ERROR.UNABLE_TO_SAVE'),
            color: 'error'
          }
        }
      }
    }

  }


}
</script>

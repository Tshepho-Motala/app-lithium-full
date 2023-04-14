<template>
  <v-container data-test-id="cnt-cashier-transactions-payment-methods-list">
    <v-row>
      <v-toolbar flat class="bg--gray rounded-t" dark height="42">
        <v-toolbar-title>{{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.TITLE_MANY')}}</v-toolbar-title>
      </v-toolbar>
    </v-row>
    <v-row>
      <v-card style="width: 100%; box-shadow: none; border-radius: 0;" class=" w-100   mb-4">

        <v-simple-table data-test-id="tbl-cashier-transactions-payment-methods-list"
                        class="table table-vue table-bordered table-striped">
          <template v-slot:default>
            <tbody>
            <tr>
              <th></th>
              <th>{{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.METHOD')}}</th>
              <th>{{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.PAYNAME')}}</th>
              <th v-if="showVerification">{{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.VERIFIED') }}
              </th>
              <th v-if="showVerification">
                {{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.CONTRA_ACCOUNT')}}
              </th>
              <th>{{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.DEPOSIT')}}</th>
              <th>{{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.WITHDRAWAL')}}</th>
              <th>{{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.NET_DEPOSIT')}}</th>
              <th>{{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.STATUS')}}</th>
              <th v-if="showVerification">
                {{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.VERIFICATION_ERROR')}}
              </th>
              <th v-if="hasRole('CASHIER_PAYMENT_METHOD_STATUS_EDIT')">
                {{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.ACTION')}}
              </th>
            </tr>
            <template v-if="paymentMethods.length">


              <tr v-for="pm in paymentMethods" :key="pm.id">
                <td>
                  <img v-if="pm.processorIcon" style="width: 40px; height: 40px;  object-fit: contain;" height="32"
                       width="32"
                       :src="`data:image/png;base64, ${pm.processorIcon.base64}`"
                       alt="">
                </td>
                <td>{{pm.name}}</td>
                <td>{{pm.nameOnPayEntry}}</td>
                <td v-if="showVerification">
                  {{pm.verified ? "Success" : (pm.verified === false ? "Fail" : "Unverified")}}
                </td>
                <td v-if="showVerification">{{pm.contraAccount ? "Yes" : "No" }}</td>

                <td>{{formatCurrencyNumber(pm.depositSum) + pm.currencyCode}}</td>
                <td>{{formatCurrencyNumber(pm.withdrawSum) + pm.currencyCode}}</td>
                <td>{{formatCurrencyNumber(pm.netDeposit) + pm.currencyCode}}</td>
                <td v-if="pm.status">

                  <span v-if="pm.status.name === 'ACTIVE'" class="badge badge-wrap bg-grey">{{pm.status.name}}</span>
                  <span v-if="pm.status.name === 'BLOCKED'" class="badge badge-wrap bg-red">{{pm.status.name}}</span>
                  <span v-if="pm.status.name === 'DISABLED'"
                        class="badge badge-wrap bg-yellow">{{pm.status.name}}</span>
                  <span v-if="pm.status.name === 'DEPOSIT_ONLY'"
                        class="badge badge-wrap bg-yellow">{{pm.status.name}}</span>
                  <span v-if="pm.status.name === 'WITHDRAWAL_ONLY'"
                        class="badge badge-wrap bg-yellow">{{pm.status.name}}</span>

                </td>
                <td v-if="showVerification" :title="pm.verificationError">
                  <template v-if="pm.verificationError">
                    {{pm.verificationError.substring(0, 50) + (pm.verificationError.length > 50 ? '...' : '')}}
                  </template>

                </td>

                <td v-if="hasRole('CASHIER_PAYMENT_METHOD_STATUS_EDIT')">
                  <v-btn data-test-id="btm-cashier-transactions-payment-method-edit"
                         v-if="pm.status && pm.status.name !== 'HISTORIC'" @click="openPaymentEditModal(pm)"
                         color="primary"
                         class="white--text" small>
                    Edit
                  </v-btn>

                </td>
              </tr>
              <tr style="background-color: #acebce;">
                <td></td>
                <td><b>{{translateAll('UI_NETWORK_ADMIN.CASHIER.PAYMENT_METHOD.GRAND_TOTAL')}}</b></td>
                <td></td>
                <td v-if="showVerification"></td>
                <td v-if="showVerification"></td>
                <td><b>{{ formatCurrencyNumber(depositTotal) }} {{ currencyCode}}</b></td>
                <td><b>{{formatCurrencyNumber(withdrawalTotal)}} {{ currencyCode}}</b></td>
                <td><b>{{formatCurrencyNumber(netTotal) }} {{ currencyCode}}</b></td>
                <td></td>
                <td v-if="hasRole('CASHIER_PAYMENT_METHOD_STATUS_EDIT')"></td>
                <td></td>
              </tr>
            </template>
            <template v-else>
              <tr>
                <td colspan="7" class="text-center">{{ translateAll('GLOBAL.DATATABLE.EMPTYTABLE')}}</td>
              </tr>
            </template>


            </tbody>
          </template>
        </v-simple-table>
      </v-card>
    </v-row>
  </v-container>

</template>

<script lang="ts">
import {Component, Mixins, Prop} from 'vue-property-decorator'
import TransactionStatuses from "@/plugin/cashier/transactions/components/TransactionStatuses.vue";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import CashierTransactionsApiInterface from "@/core/interface/axios-api/CashierTransactionsApiInterface";
import CashierTransactionsApi from "@/core/axios/axios-api/CashierTransactionsApi";
import {
  CashierTransactionsDataInterface,
  getPaymentMethodsByTranIdParamsInterface
} from "@/core/interface/cashier/cashierTransactions";

@Component({
  components: {
    TransactionStatuses,
  }
})
export default class PaymentMethodsList extends Mixins(cashierMixins) {

  @Prop({required: true}) transactionDetail?: CashierTransactionsDataInterface
  CashierTransactionsApiService: CashierTransactionsApiInterface = new CashierTransactionsApi(this.userService)
  showVerification = true
  paymentMethods: any[] = []
  statusMap: any[] = [
    {name: "ACTIVE", id: 1},
    {name: "BLOCKED", id: 2},
    {name: "DISABLED", id: 3},
    {name: "DEPOSIT_ONLY", id: 4},
    {name: "WITHDRAWAL_ONLY", id: 5}
  ];
  depositTotal = 0
  withdrawalTotal = 0
  netTotal = 0
  currencyCode = ''

  async created() {
    await this.loadPaymentMethods()
  }

  async loadPaymentMethods() {
    const params: getPaymentMethodsByTranIdParamsInterface = {
      height: 32
    }
    if (this.transactionDetail) {
      try {
        const result = await this.CashierTransactionsApiService.getPaymentMethodsByTranId(this.transactionDetail.id, params)
        if (result?.data?.successful) {
          this.paymentMethods = result.data.data
          this.depositTotal = this.sum(this.paymentMethods, 'depositSum');
          this.withdrawalTotal = this.sum(this.paymentMethods, 'withdrawSum');
          this.netTotal = this.sum(this.paymentMethods, 'netDeposit');
          this.currencyCode = this.paymentMethods && this.paymentMethods.length > 0 ? this.paymentMethods[0].currencyCode : null;
        }
      } catch (err) {
        this.logService.error(err)
      }
    }

  }

  openPaymentEditModal(method) {
    this.$emit('openPaymentEditModal', method)
  }

  sum(items, prop) {
    return items.reduce(function (a, b) {
      return a + b[prop];
    }, 0);
  };

}
</script>

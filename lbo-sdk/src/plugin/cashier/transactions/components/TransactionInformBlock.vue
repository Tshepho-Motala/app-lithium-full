<template>
  <v-container v-if="transactionDetail && feesData" data-test-id="cnt-cashier-transaction-detail">
    <v-row>
      <v-toolbar flat class="bg--gray rounded-t" dark height="42">
        <v-toolbar-title>Transaction Detail</v-toolbar-title>
      </v-toolbar>
    </v-row>
    <v-row>
      <v-card style="width: 100%; box-shadow: none; border-radius: 0;" class=" w-100 pl-4 pr-2 pt-4 pb-2 mb-4">
        <v-simple-table data-test-id="tbl-cashier-transaction-info">
          <template v-slot:default>
            <tbody>
            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.TRANID')}}
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;"> {{ transactionDetail.id }}
              </td>
            </tr>
            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.STATUS')}}
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                <transaction-statuses class="mt-1 mb-1" :table-status="true"
                                      :transaction-detail="transactionDetail"></transaction-statuses>
              </td>
            </tr>
            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.AUTO_APPROVED')}}
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{transactionDetail.autoApproved}}
              </td>
            </tr>
            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.REVIEWED_BY')}}
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{transactionDetail.reviewedByFullName}}
              </td>
            </tr>

            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.DIRECT_WITHDRAWAL')}}
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{transactionDetail.directWithdrawal}}
              </td>
            </tr>

            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.INITIATED_BY')}}
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{transactionDetail.initiationAuthorFullName}}
              </td>
            </tr>

            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.TABLE.DECLINE_REASON')}}
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{transactionDetail.declineReason}}
              </td>
            </tr>


            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.CREATED')}}
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{formatDate(transactionDetail.createdOn) }}
              </td>
            </tr>

            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.AMOUNT')}}
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{formatCurrency(transactionDetail.currencyCode, feesData.depositAmount) }}
              </td>
            </tr>


            <tr v-if="transactionDetail.current.status.code === 'SUCCESS'">
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                Player Amount (After Fees)
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{formatCurrency(transactionDetail.currencyCode, feesData.playerAmount) }}
              </td>
            </tr>

            <tr v-if="transactionDetail.current.status.code === 'SUCCESS'">
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                Fee
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{formatCurrency(transactionDetail.currencyCode, feesData.feeAmount)}}
                <div v-if="feesData.minimumUsed">(minimum fee used)</div>
              </td>
            </tr>


            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.CURRENCY')}}
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{ transactionDetail.currencyCode }}
              </td>
            </tr>


            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.METHOD')}}
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                <template v-if="transactionDetail.domainMethod"> {{ transactionDetail.domainMethod.name }}</template>
              </td>
            </tr>

            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                Last Processor
              </th>

              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;"> {{ processor }}</td>
            </tr>


            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                Payment Type
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                <template v-if="transactionDetail.transactionPaymentType">
                  {{ transactionDetail.transactionPaymentType.paymentType }}
                </template>
              </td>
            </tr>


            <tr v-if="transactionDetail.processorReference">
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                Processor Reference
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{ transactionDetail.processorReference }}
              </td>
            </tr>


            <tr v-if="transactionDetail.additionalReference">
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                Additional Reference
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{ transactionDetail.additionalReference }}
              </td>
            </tr>


            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.PROFILE')}}
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                <template v-if="transactionDetail.user && transactionDetail.user.profile">
                  {{ transactionDetail.user.profile.name }}
                </template>
              </td>

            <tr v-if="transactionDetail.bonusCode">
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                Bonus Code
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{ transactionDetail.bonusCode }}
              </td>
            </tr>

            <tr v-if="transactionDetail.bonusId">
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                Bonus ID
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;"> {{ transactionDetail.bonusId
                }}
              </td>
            </tr>

            <tr v-if="transactionDetail.accountInfo">
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                Account Info
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                {{ transactionDetail.accountInfo }}
              </td>
            </tr>


            <tr v-if="transactionDetail.linkedTransaction">
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                Linked Transaction
              </th>

              <!--            @TODO ADD HREF LINK-->
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                <a>{{ transactionDetail.linkedTransaction.id}}</a></td>
            </tr>


            <tr>
              <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                Transaction runtime (sec)
              </th>
              <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;"> {{ transactionDetail.runtime
                }}
              </td>
            </tr>

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
import {CashierFeesInterface, CashierTransactionsDataInterface} from "@/core/interface/cashier/cashierTransactions";

@Component({
  components: {
    TransactionStatuses,
  }
})
export default class TransactionInformBlock extends Mixins(cashierMixins) {

  @Prop({required: true}) transactionDetail?: CashierTransactionsDataInterface
  @Prop({required: true}) feesData?: CashierFeesInterface
  @Prop({required: false, default: ''}) processor?: string


}
</script>

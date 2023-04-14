<template>
  <v-container class="mt-2" data-test-id="cnt-cashier-transaction-player">
    <v-row>
      <v-toolbar flat class="bg--gray rounded-t" dark height="42">
        <v-toolbar-title>Player Details</v-toolbar-title>
      </v-toolbar>
    </v-row>
    <v-row>
      <v-card style="width: 100%; box-shadow: none; border-radius: 0;" class=" w-100 mb-4">
        <v-col lg="7">
          <v-simple-table data-test-id="tbl-cashier-transaction-player-first">
            <template v-slot:default>
              <tbody>
              <tr>
                <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                  {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.PLAYER') }}
                </th>
                <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                  <!--                  @TODO Href-->
                  <a href="">{{ user.guid }}</a>
                </td>
              </tr>
              <tr>
                <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                  {{ translateAll('UI_NETWORK_ADMIN.ECOSYSTEMS.FIELDS.ECOSYSTEM_STATUS') }}
                </th>
                <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">

                  <v-chip class="mr-2" text-color="white"
                          :color="user.status.userEnabled ? 'success' : 'error'" label small>
                    {{ user.status.name }}
                  </v-chip>

                  <v-chip v-if="user.statusReason" class="mr-2" text-color="white"
                          color="grey" label small>
                    {{ user.statusReason.description }}
                  </v-chip>

                  <v-chip v-if="user.testAccount" class="mr-2" text-color="white"
                          color="error" label small>
                    TEST
                  </v-chip>
                </td>
              </tr>
              <tr>
                <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                  {{ translateAll('UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.VERIFICATIONSTATUS.LABEL') }}
                </th>
                <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">

                  <v-chip v-if="user.verificationStatus === 1" text-color="white"
                          color="warning" label small>
                    {{ translateAll('UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATIONSTATUS.CODES.UNVERIFIED') }}
                  </v-chip>
                  <v-chip v-else-if="user.verificationStatus === 2" text-color="white"
                          color="blue" label small>
                    {{ translateAll('UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATIONSTATUS.CODES.MANUALLY_VERIFIED') }}
                  </v-chip>

                  <v-chip v-else-if="user.verificationStatus === 3" text-color="white"
                          color="blue" label small>
                    {{ translateAll('UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATIONSTATUS.CODES.EXTERNALLY_VERIFIED') }}
                  </v-chip>

                  <v-chip v-else-if="user.verificationStatus === 4" text-color="white"
                          color="blue" label small>
                    {{ translateAll('UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATIONSTATUS.CODES.SOF_VERIFIED') }}
                  </v-chip>
                  <v-chip v-else-if="user.verificationStatus === 5" text-color="white"
                          color="blue" label small>
                    {{ translateAll('UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATIONSTATUS.CODES.UNDERAGED') }}
                  </v-chip>
                  <v-chip v-else-if="user.verificationStatus === 6" text-color="white"
                          color="blue" label small>
                    {{ translateAll('UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATIONSTATUS.CODES.AGE_ONLY_VERIFIED') }}
                  </v-chip>

                  <v-chip v-else text-color="primary"
                          color="error" label small>
                    {{ translateAll('UI_NETWORK_ADMIN.USER.FIELDS.VERIFICATIONSTATUS.CODES.NOT_SETUP') }}
                  </v-chip>
                </td>
              </tr>

              <tr>
                <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                  {{ translateAll('GLOBAL.FIELDS.FULLNAME') }}
                </th>
                <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                  {{ user.firstName }} {{ user.lastName }}
                </td>
              </tr>

              <tr>
                <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                  Registration Date
                </th>
                <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                  {{ formatDate(user.createdDate) }}
                </td>
              </tr>

              <tr>
                <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                  {{ translateAll('UI_NETWORK_ADMIN.LIST.PLAYERS.FIELDS.FILTER.TAGS.LABEL') }}
                </th>
                <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                  <template v-if="user.userCategories.length">
                     <span v-for="(uc, index) in user.userCategories" :key="index">
                       <v-chip class="mr-2" text-color="white"
                               color="primary" label small>
                         {{ uc.name }}
                      </v-chip>

                    </span>
                  </template>

                  <span v-if="user.userCategories.length === 0">No tags specified</span>
                </td>
              </tr>

              <tr>
                <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                  {{ translateAll('UI_NETWORK_ADMIN.PLAYER.RESTRICTIONS.TITLE') }}
                </th>
                <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                  <template v-if="restrictions.length">
                    <span v-for="(restriction, index) in restrictions" :key="index">
                       <v-chip class="mr-2 text-uppercase  mt-1" text-color="white"
                               :color="restriction.active ? 'error' : 'warning'" label x-small>
                         {{ restriction.set.name }}
                        </v-chip>
                    </span>
                  </template>
                  <template v-else>
                    No restrictions specified
                  </template>
                </td>
              </tr>
              </tbody>
            </template>
          </v-simple-table>
        </v-col>


        <v-col lg="5">
          <v-simple-table data-test-id="tbl-cashier-transaction-player-balance">
            <template v-slot:default>
              <tbody>
              <tr>
                <th style="height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                  {{ translateAll('UI_NETWORK_ADMIN.PLAYER.ACCOUNTING.BALANCEADJUST.FIELDS.CURRENT.LABEL') }}
                </th>
                <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                  {{ formatCurrencyCent(transactionDetail.currencyCode, balance.currentBalance) }}
                </td>
              </tr>
              <tr>
                <th style="height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                  {{ translateAll('UI_NETWORK_ADMIN.PLAYER.INFO-HEADER.LT-DEPOSITS') }}
                </th>
                <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                  <span style="color: green;"> {{
                    formatCurrencyCent(transactionDetail.currencyCode, balance.ltDeposits)
                    }} </span>

                </td>
              </tr>
              <tr>
                <th style="height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                  {{ translateAll('UI_NETWORK_ADMIN.PLAYER.INFO-HEADER.LT-WITHDRAWALS') }}
                </th>
                <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                  <span style="color: red;"> {{
                    formatCurrencyCent(transactionDetail.currencyCode, balance.ltWithdrawals)
                    }} </span>

                </td>
              </tr>
              <tr>
                <th style="height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                  {{ translateAll('UI_NETWORK_ADMIN.PLAYER.INFO-HEADER.PENDING-WITHDRAWALS') }}
                </th>
                <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                  <span style="color: red;"> {{
                    formatCurrencyCent(transactionDetail.currencyCode, balance.pendingWithdrawals)
                    }} </span>

                </td>
              </tr>
              <tr>
                <th style="height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                  {{ translateAll('UI_NETWORK_ADMIN.PLAYER.INFO-HEADER.ESCROW_BALANCE') }}
                </th>
                <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;">
                  <span style="color: red"> {{
                    formatCurrencyCent(transactionDetail.currencyCode, balance.escrowBalance)
                    }} </span>
                </td>
              </tr>
              </tbody>
            </template>
          </v-simple-table>
        </v-col>
      </v-card>
    </v-row>
  </v-container>

</template>

<script lang="ts">
import {Component, Mixins, Prop} from 'vue-property-decorator'
import TransactionStatuses from "@/plugin/cashier/transactions/components/TransactionStatuses.vue";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import {UserInterface} from "@/core/interface/cashier/cashierTransactions";


@Component({
  components: {
    TransactionStatuses,
  }
})
export default class PlayerDetailsBlock extends Mixins(cashierMixins) {
  @Prop({required: true}) user?: UserInterface
  @Prop({required: true}) transactionDetail: any
  @Prop({required: true}) restrictions: any
  @Prop({required: true}) balance: any

}
</script>

<template>
  <v-container data-test-id="cnt-cashier-transactions-remark">
    <v-row>
      <v-toolbar flat class="bg--gray rounded-t" dark height="42">
        <v-toolbar-title>{{translateAll('UI_NETWORK_ADMIN.CASHIER.REMARKS.TITLE')}}</v-toolbar-title>
      </v-toolbar>
    </v-row>
    <v-row>
      <v-card style="width: 100%; box-shadow: none; border-radius: 0;" class=" w-100 mb-4">
        <v-list class="overflow-y-auto" max-height="160" v-if="remarks.length">
          <v-list-item v-for="remark in remarks" :key="remark.id">
            <v-list-item-content class="pt-1 pb-1">
              <v-list-item-title style="align-items: center" class="d-flex justify-space-between"><span
                  style="font-size: 14px;" class=" font-weight-bold">{{remark.authorName}}</span> <span
                  style="font-size: 12px; align-items: center" class=" d-flex align-content-center"><v-icon
                  color="grey lighten-1" class="mr-1">mdi-clock-outline</v-icon> {{formatDate(remark.timestamp)}}</span>
              </v-list-item-title>
              <span> {{remark.message}} </span>
            </v-list-item-content>
          </v-list-item>


        </v-list>

        <p class="pa-4 mb-0" v-else>
          {{translateAll('UI_NETWORK_ADMIN.CASHIER.REMARKS.NOREMARKS')}}
        </p>


        <v-form v-model="valid">
          <v-container pb-0 mb-0>
            <v-row>
              <v-col
                  cols="12 mb-0"
              >
                <v-text-field
                    label="Type message..."
                    single-line
                    v-model="messageText"
                    filled
                    height="40"
                    :append-icon=" 'mdi-email-send-outline'"
                    @click:append="sendMessage"
                    :rules="messageRules"
                    required
                    :disabled="loadSendMessage"
                    data-test-id="txt-cashier-transactions-remark"
                ></v-text-field>
              </v-col>


            </v-row>
          </v-container>
        </v-form>

      </v-card>
    </v-row>
  </v-container>
</template>

<script lang='ts'>

import {Component, Mixins, Prop} from 'vue-property-decorator'
import CashierTransactionsApiInterface from "@/core/interface/axios-api/CashierTransactionsApiInterface";
import CashierTransactionsApi from "@/core/axios/axios-api/CashierTransactionsApi";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import {sendTransactionRemarksParamsInterface} from "@/core/interface/cashier/cashierTransactions";

@Component
export default class TransactionRemarks extends Mixins(cashierMixins) {
  @Prop({required: true}) transactionId!: number
  remarks: any[] = []
  loadSendMessage: boolean = false
  valid: boolean = false
  messageText: string = ''
  CashierTransactionsApiService: CashierTransactionsApiInterface = new CashierTransactionsApi(this.userService)
  messageRules = [
    v => !!v || '',
  ]

  async mounted() {
    await this.loadTransactionRemarks()
  }

  async loadTransactionRemarks() {
    if (this.transactionId) {
      try {
        const result = await this.CashierTransactionsApiService.getTransactionRemarks(this.transactionId)
        if (result?.data?.successful) {
          this.remarks = result.data.data
        }
      } catch (err) {
        this.logService.error(err)
      }
    }
  }

  async sendMessage() {
    if (this.messageText) {
      this.loadSendMessage = true
      const params: sendTransactionRemarksParamsInterface = {
        message: this.messageText
      }

      try {
        const result = await this.CashierTransactionsApiService.sendTransactionRemarksUrl(this.transactionId, params)
        if (result?.data?.successful) {
          await this.loadTransactionRemarks()
        }

      } catch (err) {
        this.logService.error(err)
      } finally {
        this.loadSendMessage = false
        this.messageText = ''
      }
    }
  }

}
</script>

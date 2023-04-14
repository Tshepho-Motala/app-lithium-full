<template>
  <div data-test-id="cnt-cashier-transactions-workfow">
    <v-toolbar flat class="bg--gray rounded-t" dark height="42">
      <v-toolbar-title>{{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.WORKFLOW.TITLE') }}</v-toolbar-title>
    </v-toolbar>

    <v-card v-if="workflowList.length" style="width: 100%; box-shadow: none; border-radius: 0;"
            class=" w-100 pl-4 pr-4 pt-4 pb-2 mb-4">
      <div class="mb-3" v-for="workflow in workflowList" :key="workflow.id">
        <v-card style="width: 100%;   padding: 0 20px; border: 1px solid #efefef; box-shadow: none;">
          <div class="d-flex mt-5 mb-4 justify-space-between" style="align-items: center">
            <div class="d-flex cbp_tmlabel " style="align-items: center">
              <div v-if="workflow.status.code === 'START'" class="cbp_tmicon vue-icon fa fa-play bg-blue"></div>
              <div v-else-if="workflow.status.code === 'VALIDATEINPUT'"
                   class="cbp_tmicon vue-icon fa fa-check-square-o bg-blue"></div>
              <div v-else-if="workflow.status.code === 'WAITFORPROCESSOR'"
                   class="cbp_tmicon vue-icon fa fa-hourglass-start bg-blue"></div>
              <div v-else-if="workflow.status.code === 'FATALERROR'"
                   class="cbp_tmicon vue-icon fa fa-exclamation-circle bg-red"></div>
              <div v-else-if="workflow.status.code === 'DECLINED'" class="cbp_tmicon vue-icon fa fa-ban bg-red"></div>
              <div v-else-if="workflow.status.code === 'CANCEL'"
                   class="cbp_tmicon vue-icon fa fa-exclamation-circle bg-red"></div>
              <div v-else-if="workflow.status.code === 'SUCCESS'"
                   class="cbp_tmicon vue-icon fa fa-check bg-green"></div>
              <div v-else-if="workflow.status.code === 'REVERSALAPPROVED'"
                   class="cbp_tmicon vue-icon fa fa-check bg-green"></div>
              <div v-else-if="workflow.status.code === 'REVERSALDECLINED'"
                   class="cbp_tmicon vue-icon fa fa-ban bg-red"></div>
              <div v-else-if="workflow.status.code === 'RESERVED_FUNDS_REVERSAL_FAILURE'"
                   class="cbp_tmicon vue-icon fa fa-ban bg-red"></div>

              <div v-else class="cbp_tmicon vue-icon fa fa-clock-o bg-blue"></div>
              <div class="d-flex flex-column justify-center">
                <h3 class="ma-0">
                  {{ translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.TRANSACTION.STATUS.' + workflow.status.code) }}</h3>
                <span
                    v-if="workflow.attempt && workflow.attempt.workflowFrom && workflow.attempt.workflowFrom.status && workflow.attempt.workflowFrom.status.code"
                    class="description">Previous State: {{ workflow.attempt.workflowFrom.status.code }}</span>
                <span v-if="workflow.authorName">Author: {{ workflow.authorName }}</span>
              </div>

            </div>
            <div class="d-flex flex-column justify-center">
              <span style="font-size: 15px; align-items: center" class="d-flex align-content-center mb-1"><v-icon
                  color="grey lighten-1" class="mr-1">mdi-clock-outline</v-icon> {{
                formatDate(workflow.timestamp)
                }}</span>
              <div class="d-flex  justify-end">
                <v-chip class="mr-2" text-color="white"
                        color="info" label small>
                  {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.WORKFLOW.STAGE') }}:{{ workflow.stage }}
                </v-chip>
                <v-chip v-if="workflow.processor" text-color="white"
                        color="success" label small> {{ workflow.processor.description }}
                </v-chip>
              </div>
            </div>


          </div>

          <v-tabs
              fixed-tabs
              background-color="blue-grey lighten-4"
              slider-color="black"
              color="black"
              class="mt-2"
          >
            <v-tab>
              {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INFO.TITLE') }}
            </v-tab>

            <v-tab v-if="workflow.attempt && workflow.attempt.iodata && workflow.attempt.iodata.length > 0">
              Input/Output
            </v-tab>

            <v-tab v-if="workflow.attempt && workflow.attempt.processorRawRequest">
              Request
            </v-tab>

            <v-tab v-if="workflow.attempt && workflow.attempt.processorRawResponse">
              Response
            </v-tab>

            <!--  Information -->
            <v-tab-item>
              <v-card class="mb-3 mt-2" flat>
                <v-simple-table data-test-id="tbl-cashier-transactions-workflow-information">
                  <template v-slot:default>
                    <tbody>
                    <tr v-if="workflow.comments && workflow.comments.length > 0">
                      <th style="width: 40%;">Comments</th>
                      <td>
                        <div v-for="(comment,i) in workflow.comments" :key="i">
                          <span> by <b>{{ comment.author.guid }}</b> on <b>{{
                            formatDate(comment.timestamp)
                            }}</b></span>

                          <p> {{ comment.comment }}&quot;</p>
                        </div>
                      </td>
                    </tr>
                    <tr v-if="workflow.accountingReference">
                      <th style="width: 40%;">Accounting Reference</th>
                      <td>{{ workflow.accountingReference }}</td>
                    </tr>
                    <tr v-if="workflow.billingDescriptor">
                      <th style="width: 40%;">Billing Descriptor</th>
                      <td>{{ workflow.billingDescriptor }}</td>
                    </tr>
                    <tr v-if="workflow.attempt.processorReference">
                      <th style="width: 40%;">Processor Reference</th>
                      <td>{{ workflow.attempt.processorReference }}</td>
                    </tr>
                    <tr v-if="workflow.attempt.processorMessages">
                      <th style="width: 40%;">Processor Message</th>
                      <td>{{ workflow.attempt.processorMessages }}</td>
                    </tr>
                    <tr v-if="workflow.source" style="background-color: #f0ad4e;">
                      <th style="width: 40%;">Source</th>
                      <td>{{ workflow.source }}</td>
                    </tr>
                    <tr v-if="!workflow.source" style="background-color: #f0ad4e;">
                      <th style="width: 40%;">Source</th>
                      <td>Customer</td>
                    </tr>
                    </tbody>
                  </template>
                </v-simple-table>

              </v-card>
            </v-tab-item>

            <!-- Input/Output-->

            <v-tab-item v-if="workflow.attempt && workflow.attempt.iodata && workflow.attempt.iodata.length > 0">
              <v-card class="mb-3 mt-2" flat>
                <v-simple-table data-test-id="tbl-cashier-transactions-workflow-input">
                  <template v-slot:default>
                    <tbody>
                    <tr>
                      <th style="font-size: 14px">{{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.IO.FIELD') }}
                      </th>
                      <th style="font-size: 14px">{{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.IO.VALUE') }}
                      </th>
                    </tr>
                    <tr v-for="(io, i)  in workflow.attempt.iodata" :key="i">
                      <th style="width: 40%; height: 30px;">
                        <div class="pr-3 d-flex justify-space-between" style="align-items: center;">
                          <span style="font-weight: bold">{{ io.field }}</span>
                          <v-chip v-if="io.output === true" class="mr-2" text-color="white"
                                  color="info" label small>
                            {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.OUTPUT') }}
                          </v-chip>

                          <v-chip v-if="io.output === false" class="mr-2" text-color="white"
                                  color="grey" label small>
                            {{ translateAll('UI_NETWORK_ADMIN.CASHIER.TRANS.VIEW.INPUT') }}
                          </v-chip>
                        </div>


                      </th>
                      <td style="height: 30px;">{{ io.value }}</td>
                    </tr>
                    </tbody>
                  </template>
                </v-simple-table>
              </v-card>
            </v-tab-item>

            <!-- Request  -->

            <v-tab-item v-if="workflow.attempt && workflow.attempt.processorRawRequest">
              <v-card class="mt-2 mb-2" flat>
                <pre id="processorRawRequest" class="attempt-data">{{ workflow.attempt.processorRawRequest }}</pre>
              </v-card>
            </v-tab-item>

            <!-- Response-->
            <v-tab-item v-if="workflow.attempt && workflow.attempt.processorRawResponse">
              <v-card class="mt-2 mb-2" flat>
                <pre id="processorRawResponse" class="attempt-data">{{ workflow.attempt.processorRawResponse }}</pre>
              </v-card>
            </v-tab-item>
          </v-tabs>
        </v-card>

      </div>

      <v-row justify="center">
        <v-col cols="8">
          <v-pagination
              data-test-id="cnt-cashier-transactions-workflow-pagination"
              v-model="paginationActive"
              class="my-4"
              @input="paginationClick"
              :length="paramsWorkflowApi.totalPages"
          ></v-pagination>
        </v-col>
      </v-row>

    </v-card>

  </div>

</template>

<script lang="ts">
import {Component, Mixins, Prop} from 'vue-property-decorator'
import TransactionStatuses from "@/plugin/cashier/transactions/components/TransactionStatuses.vue";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import CashierTransactionsApiInterface from "@/core/interface/axios-api/CashierTransactionsApiInterface";
import CashierTransactionsApi from "@/core/axios/axios-api/CashierTransactionsApi";
import {format, utcToZonedTime} from "date-fns-tz";
import {ParamsWorkflowApiInterface} from "@/core/interface/cashier/cashierTransactions";

@Component({
  components: {
    TransactionStatuses,
  }
})
export default class TransactionWorkflow extends Mixins(cashierMixins) {
  CashierTransactionsApiService: CashierTransactionsApiInterface = new CashierTransactionsApi(this.userService)
  @Prop({required: true}) workflowList: any
  @Prop({required: true}) paramsWorkflowApi?: ParamsWorkflowApiInterface

  paginationActive:number = 1

  formatDate(millis: number): string {
    const date = new Date(millis)
    const zonedDate = utcToZonedTime(date, 'Etc/GMT')
    return format(zonedDate, 'yyyy-MM-dd HH:mm:ss.SSS')
  }

  paginationClick() {
    this.$emit('workflowPagination', this.paginationActive)
  }
}
</script>

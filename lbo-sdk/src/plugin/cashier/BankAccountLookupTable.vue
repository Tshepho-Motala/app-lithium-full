<template>
  <div data-test-id="cnt-bank-account-lookup-table">
    <div class="box box-solid box-default">
      <div class="box-header with-border" style="display: flex; align-items: center; height: 42px">
        <p class="pull-left" style="margin: 0">
          {{ translateService.instant('UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP_TABLE.BANK_ACCOUNT_LOOKUP') }}
        </p>
        <button
          data-test-id="btn-refresh"
          @click="refresh()"
          type="button"
          class="btn btn-default pull-right"
          style="background: #fff; margin-left: auto"
        >
          <i class="fa fa-refresh"></i><span class="ng-binding"> {{ translateService.instant('GLOBAL.ACTION.REFRESH') }}</span>
        </button>
      </div>
      <div class="box-body table-responsive">
        <v-simple-table data-test-id="tbl-bank-account" v-if="isPending" :loading="true" loading-text="Wait for some time">
          <template v-slot:default>
            <tbody>
              <tr :data-test-id="`tbi-bank-account-${index}`" v-for="(item, index) in items" :key="`name${index}`">
                <td style="font-weight: 600; font-size: 14px; padding: 0 5px; width: 27%">{{ translateService.instant(item.text) }}</td>
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
import { Component, Inject, Vue } from 'vue-property-decorator'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'

@Component
export default class BankAccountLookupTable extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  model: any = null
  isPending: Boolean = false
  items: any[] = []

  mounted() {
    this.loadDataFromController()
  }

  async loadDataFromController() {
    await this.rootScope.provide.bankAccountLookupGeneration
      .getModel()
      .then((res) => (this.model = res))

      .then(() => (this.isPending = true))

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

    this.isPending = true
  }

  refresh() {
    this.rootScope.provide.bankAccountLookupGeneration.refresh()
  }
}
</script>
<style scoped>
</style>
<template>
  <div>
    <v-row>
      <v-col cols="12" sm="12" md="6">
        <v-btn data-test-id="btn-auto-withdrawal-rule--add" v-role="'AUTOWITHDRAWALS_RULESETS_ADD'" color="success" dark
               @click="showEditItemDialog = true">
          <v-icon class="mr-1">mdi-plus</v-icon>
          {{ translate('PAGE.ADD') }}
        </v-btn>

        <AutoWithdrawalsExportRulesTable :is-has-data="true" :data-items="data" v-role="'AUTOWITHDRAWALS_RULESETS_ADD'"/>
        <AutoWithdrawalsImportRules v-role="'AUTOWITHDRAWALS_RULESETS_ADD'"/>
        <edit-loaded-rules-item @saveItem="saveItem" @cancel="closeEditItemDialog" v-if="showEditItemDialog" :isNewItem="true"></edit-loaded-rules-item>
        <v-snackbar v-model="showAddMessage" timeout="3000" :color="addMessageError ? 'error' : 'success'">
          {{ addMessage }}
        </v-snackbar>
      </v-col>

      <v-col cols="12" sm="12" md="6">
        <div class="d-flex  justify-end mb-3">
          <DomainSelect @changeDomain="changeDomain" :has-domains="true" :domains-list="domains"/>
        </div>

      </v-col>
    </v-row>

    <v-card class="mb-5">
      <v-card-text>
        <v-form data-test-id="frm-auto-withdrawal-rulset-filer">
          <v-row v-if="openFilter">
            <v-col cols="3" sm="12" md="4">
              <v-text-field v-model="filter.name" label="Name" outlined dense v-on="on"></v-text-field>
            </v-col>

            <v-col cols="3" sm="12" md="4">
              <v-select
                  :items="enabledFilterItems"
                  v-model="enabledFilter"
                  label="Enabled"
                  @change="OnEnabledFilter(enabledFilter)"
                  outlined
                  dense
              ></v-select>
            </v-col>
            <v-col cols="6" sm="12" md="4">
              <v-menu v-model="lastUpdatedEndOpen" :close-on-content-click="false" transition="scale-transition"
                      offset-y min-width="auto">
                <template v-slot:activator="{ on, attrs }">
                  <v-text-field
                      v-model="dateRangeText"
                      label="Last Updated Date Range "
                      prepend-icon="mdi-calendar"
                      v-bind="attrs"
                      outlined
                      dense
                      v-on="on"
                  ></v-text-field>
                </template>
                <v-date-picker scrollable actions v-model="dates" color="blue darken-2" full-width :max="setMaxDate"
                               :min="setMinDate" reactive range>
                  <v-card-actions style="width: 100%" class="d-flex justify-space-between">
                    <v-btn style="width: 32%" color="error" @click="dateRangeChangeCancel()">Cancel</v-btn>
                    <v-btn style="width: 32%" color="primary" @click="dateRangeChangeClear()">Clear date</v-btn>
                    <v-btn style="width: 32%" color="success" @click="dateRangeChangeSave()">OK</v-btn>
                  </v-card-actions>
                </v-date-picker>
              </v-menu>
            </v-col>
          </v-row>
        </v-form>
        <div class="d-flex justify-space-between mb-3">
          <v-btn color="primary" dark @click="openFilter = !openFilter">
            <v-icon class="mr-1">mdi-filter</v-icon>
            {{ !openFilter ? translate('PAGE.OPEN-FILTER') : translate('PAGE.CLOSE-FILTER') }}
          </v-btn>

          <div>
            <v-btn data-test-id="btn-auto-withdrawal-list--clear" color="error" dark @click="clearFilter()">
              <v-icon class="mr-1">mdi-delete-outline</v-icon>
              {{ translate('PAGE.CLEAR') }}
            </v-btn>
            <template v-if="openFilter">
              <v-btn data-test-id="btn-auto-withdrawal-list--filter" color="primary" dark
                     @click="loadAutoWithdrawals()">
                <v-icon class="mr-1">mdi-filter</v-icon>
                {{ translate('PAGE.FILTER') }}
              </v-btn>
            </template>
            <template v-else>
              <v-btn data-test-id="btn-auto-withdrawal-list--refresh" color="primary" dark
                     @click="loadAutoWithdrawals()">
                <v-icon class="mr-1">mdi-cached</v-icon>
                {{ translate('PAGE.REFRESH') }}
              </v-btn>
            </template>
          </div>
        </div>
        <v-data-table
            data-test-id="tbl-auto-withdrawal-list-ruleset"
            :headers="headers"
            :items="data"
            :loading="loading"
            loading-text="Loading... Please wait"
            item-key="id"
            :footer-props="{
            'items-per-page': 20,
            'items-per-page-options': [20, 30, 50, 100, -1]
          }"
        >
          <template v-slot:top>
            <v-row class="flex-row-reverse mt-3">
              <v-col cols="4" sm="12" md="3">
                <v-text-field v-model="searchTable" label="Search" @input="OnSearchTable()" outlined
                              dense></v-text-field>
              </v-col>
            </v-row>
          </template>

          <template v-slot:[`item.name`]="{ item }">
            <span class="link-name" @click="goToDetailPage(item.id)">{{ item.name }}</span>
          </template>
          <template v-slot:[`item.enabled`]="{ item }">
            <v-btn v-if="item.enabled" x-small color="success" dark>
              {{ translate('BUTTONS.ENABLED') }}
            </v-btn>
            <v-btn v-else x-small color="error" dark>
              {{ translate('BUTTONS.DISABLED') }}
            </v-btn>
          </template>
          <template v-slot:[`item.lastUpdated`]="{ item }">
            {{ timestampToDate(item.lastUpdated) }}
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </div>
</template>

<script lang="ts">
import {Component, Mixins, Inject} from 'vue-property-decorator'
import AssetTabMixin from '@/plugin/cms/mixins/AssetTabMixin'
import AutoWithdrawalsExportRulesTable from '@/plugin/cashier/autowithdrawals/AutoWithdrawalsExportRulesTable.vue'
import EditLoadedRulesItem from '@/plugin/cashier/autowithdrawals/components/EditLoadedRulesItem.vue'
import AutoWithdrawalsImportRules from '@/plugin/cashier/autowithdrawals/AutoWithdrawalsImportRules.vue'
import {AutoWithdrawalItem} from '@/core/interface/AutoWithdrawalInterface'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import DomainSelect from '@/plugin/domain-select/DomainSelect.vue'
import {DomainDropDown} from '@/core/interface/DropDownMenuInterface'
import '@/core/directive/role-check/RoleCheckDirective'
import AutoWithdrawalRulesetsApiService from '@/core/axios/axios-api/AutoWithdrawalRulesetsApi'
import AutoWithdrawalRulesetsApiInterface from '@/core/interface/axios-api/AutoWithdrawalRulesetsApiInterface'
import {getAllRulesetsApiParams} from '@/core/interface/api-params/AutoWithdrawalRulesetsApiIParamsInterface'

@Component({
  components: {
    AutoWithdrawalsExportRulesTable,
    AutoWithdrawalsImportRules,
    DomainSelect,
    EditLoadedRulesItem
  }
})
export default class AutoWithdrawalPage extends Mixins(AssetTabMixin) {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  drawTable = 1
  lastUpdatedStartOpen: boolean = false
  filter: any = {
    lastUpdatedStart: '',
    lastUpdatedEnd: '',
    enabled: '',
  }
  dates = []
  enabledFilter = ''
  lastUpdatedEndOpen: boolean = false
  searchTable: string = ''
  enabledFilterItems = ['Both', 'Enabled', 'Disabled']
  openFilter: boolean = false
  domains: DomainDropDown[] = []
  headers = [
    {
      text: 'Name',
      align: 'start',
      sortable: true,
      value: 'name',
      width: 400
    },
    {text: 'Domain', sortable: true, value: 'domain.name'},
    {text: 'Enabled', sortable: false, value: 'enabled'},
    {text: 'Last Updated', sortable: true, value: 'lastUpdated'},
    {text: 'Last Updated By', sortable: true, value: 'lastUpdatedBy'}
  ]
  data: AutoWithdrawalItem[] = []
  domainsSelected: String = ''
  loading: boolean = true
  editItem: AutoWithdrawalItem | null = null
  showEditItemDialog: boolean = false
  addMessage = ''
  showAddMessage: boolean = false
  addMessageError = false

  //  transfer the userService in ApiClass
  AutoWithdrawalRulesetsApiService: AutoWithdrawalRulesetsApiInterface = new AutoWithdrawalRulesetsApiService(this.userService)

  created() {
    this.loadFirstMethod()
  }

  async loadFirstMethod() {
    await this.loadDomainList()
    await this.loadAutoWithdrawals()
  }

  goToDetailPage(id) {
    window.location.href = `/#/dashboard/cashier/autowithdrawals/rulesets/${id}/view`
  }

  async loadDomainList() {
    const domainList: DomainDropDown[] = this.userService.domainsWithAnyRole(['ADMIN', 'AUTOWITHDRAWALS_*']) as any
    if (domainList) {
      domainList.forEach((el: any) => {
        el.selected = true
      })
      this.domains = domainList
      this.domainsSelected = this.domainToComaSeparator(this.domains)
    }
  }

  async loadAutoWithdrawals() {
    this.loading = true
    if (this.dates) {
      this.filter.lastUpdatedStart = this.dates[0]
      if (this.dates.length > 1) {
        this.filter.lastUpdatedEnd = this.dates[1]
      }
    }
    const data: getAllRulesetsApiParams = {
      draw: this.drawTable,
      start: 0,
      length: 99999999,
      domains: this.domainsSelected,
      name: this.searchTable,
      ...this.filter
    }
    try {
      const result = await this.AutoWithdrawalRulesetsApiService.getAllRulesets(data)
      if (result?.data?.data.length) {
        this.data = result.data.data
      } else {
        this.data = []
      }
      this.drawTable++
      this.loading = false
    } catch (err) {
      this.logService.error(err)
    }
  }

  dateRangeChangeSave() {
    this.lastUpdatedEndOpen = false
  }

  dateRangeChangeCancel() {
    this.lastUpdatedEndOpen = false
    this.dates = []
  }

  dateRangeChangeClear() {
    this.dates = []
  }

  get setMaxDate() {
    return new Date().toISOString().slice(0, 10)
  }

  get setMinDate() {
    if (this.dates.length > 0) {
      return this.dates[0]
    }
    return null
  }

  async clearFilter() {
    this.filter = {
      lastUpdatedStart: '',
      lastUpdatedEnd: '',
      enabled: '',
      name: ''
    }
    this.dates = []
    this.searchTable = ''
    this.enabledFilter = ''
    await this.loadAutoWithdrawals()
  }

  get dateRangeText() {
    return this.dates.join(' ~ ')
  }

  async OnSearchTable() {
    await this.loadAutoWithdrawals()
  }

  OnEnabledFilter(val: string) {
    if (!val) this.filter.enabled = ''
    if (val === 'Both') this.filter.enabled = ''
    else if (val === 'Enabled') this.filter.enabled = true
    else if (val === 'Disabled') this.filter.enabled = false
  }

  domainToComaSeparator(domains: DomainDropDown[]) {
    const domainNames: String[] = []
    domains.forEach((el: any) => {
      domainNames.push(el.name)
    })
    const joinArrayDomain: String = domainNames.join(',')
    return joinArrayDomain
  }

  async changeDomain(data: DomainDropDown[]) {
    this.domainsSelected = this.domainToComaSeparator(data)
    await this.loadAutoWithdrawals()
  }

  closeEditItemDialog() {
    this.showEditItemDialog = false
  }

  async saveItem(item: AutoWithdrawalItem) {
    this.addMessage = ''
    this.showAddMessage = false
    const data: AutoWithdrawalItem = JSON.parse(JSON.stringify(item))
    if (data?.domain) {
      delete data.domain.pd
      delete data.domain.selected
    }
    if (data?.rules.length) {
      data?.rules.forEach((el: any) => {
        delete el.id
      })
    }
    try {
      const result = await this.AutoWithdrawalRulesetsApiService.createRuleset(data.domain.name, data)
      if (result?.data?.status === 500 || result?.data?.successful === false) {
        this.logService.error()
        this.addMessage = result.data.message
        this.showAddMessage = true
        this.addMessageError = true
      } else {
        this.showEditItemDialog = false
        await this.loadAutoWithdrawals()
        this.addMessage = 'Added successfully'
        this.showAddMessage = true
        this.addMessageError = false
      }
    } catch (err: any) {
      this.logService.error("Can't load list " + err)
      this.addMessage = err
      this.showEditItemDialog = true
      this.addMessageError = true
    }
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EXPORT.' + text)
  }
}
</script>

<style scoped>
</style>

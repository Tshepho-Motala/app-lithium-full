<template>
  <div>
    <v-form :disabled="formLoading" class="mt-3" data-test-id="frm-auto-withdrawal-rulset-filer">
      <v-row>
        <v-col cols="12" sm="12" md="3">
          <v-select
              :items="transactionTypeList"
              clearable
              :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.METHOD_TYPE')"
              v-model="model.transactionType"
              item-value="type"
              item-text="tran"
              outlined
              dense
          ></v-select>
        </v-col>

        <v-col cols="12" sm="12" md="3">
          <v-select
              :items="transactionMethods"
              clearable
              :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.METHOD')"
              v-model="model.selectedMethod"
              item-value="id"
              item-text="name"
              outlined
              dense
          ></v-select>
        </v-col>

        <v-col cols="12" sm="12" md="3">
          <v-select
              :items="transactionProcessors"
              clearable
              :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.PROCESSOR')"
              v-model="model.selectedProcessor"
              item-value="id"
              item-text="description"
              outlined
              dense
          ></v-select>
        </v-col>

        <v-col cols="12" sm="12" md="3">
          <v-select
              outlined
              dense
              v-model="model.paymentType"
              :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.PAYMENT_TYPE')"
              :items="paymentTypes"
              item-value="paymentType"
              item-text="paymentType"
              clearable></v-select>
        </v-col>
        <!-- Create DATE  -->
        <v-col cols="12" sm="12" md="3">
          <v-menu
              v-model="dateStartOpen"
              :close-on-content-click="false" transition="scale-transition" offset-y min-width="auto"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                  v-model="model.createdDateRangeStart"
                  :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.CREATED_DATE')"
                  prepend-inner-icon="mdi-calendar"
                  v-bind="attrs"
                  outlined
                  dense
                  v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker
                v-model="model.createdDateRangeStart"
                @input="dateStartOpen = false"
            >
              <v-spacer></v-spacer>
              <v-btn
                  color="error"
                  @click="dateStartOpen = false"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.CANCEL')}}
              </v-btn>
              <v-btn
                  color="primary"
                  @click="model.createdDateRangeStart = undefined"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.CLEAR')}}
              </v-btn>

              <v-btn
                  color="success"
                  @click="model.createdDateRangeStart = datePicker"
              >
                Today
              </v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>
        <v-col cols="12" sm="12" md="3">
          <v-menu
              v-model="dateEndOpen"
              :close-on-content-click="false" transition="scale-transition" offset-y min-width="auto"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                  v-model="model.createdDateRangeEnd"
                  :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.CREATED_DATE_END')"
                  prepend-inner-icon="mdi-calendar"
                  v-bind="attrs"
                  outlined
                  dense
                  v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker
                v-model="model.createdDateRangeEnd"
                @input="dateEndOpen = false"
            >
              <v-spacer></v-spacer>
              <v-btn
                  color="error"
                  @click="dateEndOpen = false"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.CANCEL')}}
              </v-btn>

              <v-btn
                  color="primary"
                  @click="model.createdDateRangeEnd = undefined"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.CLEAR')}}
              </v-btn>

              <v-btn
                  @click="model.createdDateRangeEnd = datePicker"
                  color="success"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.TODAY')}}
              </v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>


        <v-col cols="12" sm="12" md="3">
          <v-select
              outlined
              dense
              v-model="model.statuses"
              :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.STATUS')"
              :items="statusesList"
              item-value="code"
              item-text="code"
              multiple
              clearable></v-select>
        </v-col>

        <v-col cols="12" sm="12" md="3">
          <v-autocomplete
              v-model="model.selectedUser"
              :items="userList"
              :loading="userSearchLoading"
              :search-input.sync="userSearch"
              :placeholder="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.USER_PLACEHOLDER')"
              clearable
              hide-no-data
              hide-selected
              item-text="username"
              :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.USER')"
              outlined
              prepend-inner-icon="mdi-account"
              dense
              return-object
          >
          </v-autocomplete>

        </v-col>
        <!-- Update DATE  -->
        <v-col cols="12" sm="12" md="3">
          <v-menu
              v-model="lastUpdatedStartOpen"
              :close-on-content-click="false" transition="scale-transition" offset-y min-width="auto"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                  v-model="model.updatedDateRangeStart"
                  :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.UPDATE_DATE')"
                  prepend-inner-icon="mdi-calendar"
                  v-bind="attrs"
                  outlined
                  dense
                  v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker
                v-model="model.updatedDateRangeStart"
                @input="lastUpdatedStartOpen = false"
            >
              <v-spacer></v-spacer>
              <v-btn
                  color="error"
                  @click="lastUpdatedStartOpen = false"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.CANCEL')}}
              </v-btn>
              <v-btn
                  color="primary"
                  @click="model.updatedDateRangeStart = undefined"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.CLEAR')}}
              </v-btn>

              <v-btn
                  color="success"
                  @click="model.updatedDateRangeStart = datePicker"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.TODAY')}}
              </v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>
        <v-col cols="12" sm="12" md="3">
          <v-menu
              v-model="lastUpdatedEndOpen"
              :close-on-content-click="false" transition="scale-transition" offset-y min-width="auto"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                  v-model="model.updatedDateRangeEnd"
                  :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.UPDATE_DATE_END')"
                  prepend-inner-icon="mdi-calendar"
                  v-bind="attrs"
                  outlined
                  dense
                  v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker
                v-model="model.updatedDateRangeEnd"
                @input="lastUpdatedEndOpen = false"
            >
              <v-spacer></v-spacer>
              <v-btn
                  color="error"
                  @click="lastUpdatedEndOpen = false"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.CANCEL')}}
              </v-btn>
              <v-btn
                  color="primary"
                  @click="model.updatedDateRangeEnd = undefined"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.CLEAR')}}
              </v-btn>

              <v-btn
                  color="success"
                  @click="model.updatedDateRangeEnd = datePicker"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.TODAY')}}
              </v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>

        <!-- Registration DATE  -->
        <v-col cols="12" sm="12" md="3">
          <v-menu
              v-model="registrationStartOpen"
              :close-on-content-click="false" transition="scale-transition" offset-y min-width="auto"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                  v-model="model.registrationStart"
                  :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.REGISTRATION_DATE')"
                  prepend-inner-icon="mdi-calendar"
                  v-bind="attrs"
                  outlined
                  dense
                  v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker
                v-model="model.registrationStart"
                @input="registrationStartOpen = false"
            >
              <v-spacer></v-spacer>
              <v-btn
                  color="error"
                  @click="registrationStartOpen = false"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.CANCEL')}}
              </v-btn>
              <v-btn
                  color="primary"
                  @click="model.registrationStart = undefined"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.CLEAR')}}
              </v-btn>

              <v-btn
                  color="success"
                  @click="model.registrationStart = datePicker"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.TODAY')}}
              </v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>
        <v-col cols="12" sm="12" md="3">
          <v-menu
              v-model="registrationEndOpen"
              :close-on-content-click="false" transition="scale-transition" offset-y min-width="auto"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-text-field
                  v-model="model.registrationEnd"
                  :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.REGISTRATION_DATE_END')"
                  prepend-inner-icon="mdi-calendar"
                  v-bind="attrs"
                  outlined
                  dense
                  v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker
                v-model="model.registrationEnd"
                @input="registrationEndOpen = false"
            >
              <v-spacer></v-spacer>
              <v-btn
                  color="error"
                  @click="registrationEndOpen = false"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.CANCEL')}}
              </v-btn>
              <v-btn
                  color="primary"
                  @click="model.registrationEnd = undefined"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.CLEAR')}}
              </v-btn>

              <v-btn
                  color="success"
                  @click="model.registrationEnd = datePicker"
              >
                {{translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.BUTTONS.TODAY')}}
              </v-btn>
            </v-date-picker>
          </v-menu>
        </v-col>


        <v-col cols="12" sm="12" md="3">
          <v-text-field
              outlined
              dense
              v-model="model.processorReference"
              :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.PROCESSOR_REF')"
          ></v-text-field>
        </v-col>

        <v-col cols="12" sm="12" md="3">
          <v-text-field
              outlined
              dense
              v-model="model.lastFourDigits"
              :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.DESCRIPTOR')"
          ></v-text-field>
        </v-col>
        <v-col cols="12" sm="12" md="3">
          <v-text-field
              outlined
              dense
              v-model="model.id"
              type="number"
              min="100"
              :hint="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.ID_HINT')"
              :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.ID')"
          ></v-text-field>
        </v-col>


        <v-col cols="12" sm="12" md="3">
          <v-select
              outlined
              dense
              v-model="model.autoApproved"
              :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.AUTO_APPROVED')"
              :items="AutoApprovedList"
              item-value="value"
              item-text="label"
              clearable></v-select>
        </v-col>

        <v-col cols="12" sm="12" md="3">
          <v-select
              outlined
              dense
              v-model="model.testAccount"
              :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.TEST_ACCOUNT')"
              :items="AutoApprovedList"
              item-value="value"
              item-text="label"
              clearable></v-select>
        </v-col>

        <v-col cols="12" sm="12" md="3">
          <v-select
              outlined
              dense
              v-model="model.transactionTagsNames"
              :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.TAGS')"
              :items="tagList"
              item-text="label"
              multiple
              return-object
              clearable></v-select>
        </v-col>
        <v-col cols="12" sm="12" md="6">
          <v-row>
            <v-col cols="12" sm="12" md="4" class="mr-0 pr-0">
              <v-select
                  outlined
                  dense
                  filled
                  :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.OPERATOR')"
                  v-model="model.transactionRuntimeQuery.operator"
                  :items="runtimeOptions"
              >
                <template v-slot:append>
                  <v-tooltip
                      bottom
                  >
                    <template v-slot:activator="{ on }">
                      <v-icon v-on="on">
                        mdi-help-circle-outline
                      </v-icon>
                    </template>
                    {{ textHintRuntime }}
                  </v-tooltip>
                </template>
              </v-select>
            </v-col>
            <v-col cols="12" sm="12" md="8" class="ml-0 pl-0">
              <v-text-field
                  outlined
                  dense
                  v-model="model.transactionRuntimeQuery.value"
                  type="number"
                  :label="translateAll('UI_NETWORK_ADMIN.SERVICE_CASHIER.LIST_PAGE.FILTER.TRANSACTION_RUNTIME')"
              >

              </v-text-field>
            </v-col>
          </v-row>
        </v-col>
      </v-row>
    </v-form>
    <v-snackbar
        v-model="snackbar.show"
        :timeout="2000"
        :color="snackbar.color"
    >
      {{ snackbar.text }}

    </v-snackbar>
  </div>
</template>

<script lang="ts">
import {Component, Inject, Mixins, Watch} from 'vue-property-decorator'


import '@/core/directive/role-check/RoleCheckDirective'
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";
import UserApiInterface from "@/core/interface/axios-api/UserApiInterface";
import UserApi from "@/core/axios/axios-api/UserApi";
import {DomainItemInterface} from "@/plugin/cms/models/DomainItem";
import {

  CashierTransactionsApiParamsInterface
} from "@/core/interface/cashier/cashierTransactionsListInterface";
import {AxiosApiClientsInterface} from "@/core/axios/AxiosApiClients";
import {
  CashierTransactionUserInterface,
  CashierPaymentTypeItemInterface,
  CashierTransactionMethodItemInterface,
  CashierTransactionProcessorsItemInterface,
  CashierTransactionStatusItemInterface
} from "@/core/interface/contract-interfaces/service-cashier/CashierApiContractInterfaces";
import {format} from "date-fns-tz";

@Component
export default class TransactionFilterComponent extends Mixins(cashierMixins) {
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface
  UserApiService: UserApiInterface = new UserApi(this.userService)
  lastUpdatedStartOpen: boolean = false
  lastUpdatedEndOpen: boolean = false
  registrationStartOpen: boolean = false
  registrationEndOpen: boolean = false
  dateStartOpen: boolean = false
  dateEndOpen: boolean = false
  enabledFilter: string = ''
  transactionTypeList = [
    {type: "deposit", translate: "UI_NETWORK_ADMIN.CASHIER.TYPES.DEPOSIT", tran: "Deposit"},
    {type: "withdraw", translate: "UI_NETWORK_ADMIN.CASHIER.TYPES.WITHDRAWAL", tran: "Withdrawal"}
  ]
  datePicker: any = (new Date(Date.now() - (new Date()).getTimezoneOffset() * 60000)).toISOString().substr(0, 10)
  domain: DomainItemInterface | undefined = undefined
  formLoading: boolean = false
  paymentTypes: CashierPaymentTypeItemInterface[] = []
  statusesList: CashierTransactionStatusItemInterface[] = []
  transactionMethods: CashierTransactionMethodItemInterface[] = []
  transactionProcessors: CashierTransactionProcessorsItemInterface[] = []
  userList: CashierTransactionUserInterface[] = []
  tagList: string[] = []
  userSearch: string = ''
  userSearchLoading: boolean = false
  runtimeOptions: string[] = ['>', '<', "!=", '=', '>=', '<=']
  AutoApprovedList = [
    {value: true, label: 'Yes'},
    {value: false, label: 'No'},
  ]
  textHintRuntime: string = 'Please use one of the following operators: <, >, =, <=, >=, != followed by the duration in seconds'
  model: CashierTransactionsApiParamsInterface = {
    autoApproved: undefined,
    testAccount: undefined,
    selectedMethod: undefined,
    selectedProcessor: undefined,
    selectedUser: undefined,
    createdDateRangeStart: this.yesterdayDate,
    createdDateRangeEnd: undefined,
    updatedDateRangeStart: undefined,
    updatedDateRangeEnd: undefined,
    registrationStart: undefined,
    registrationEnd: undefined,
    id: undefined,
    transactionRuntimeQuery: {
      operator: '=',
      value: undefined
    },
    transactionType: '',
    paymentType: '',
    statuses: undefined,
    transactionTagsNames: '',
    processorReference: '',
    lastFourDigits: '',
  };

  mounted() {
    const domain: any = window.localStorage.getItem('domain-name')
    this.domain = JSON.parse(domain)
    this.loadStartData()
  }

  @Watch('model.transactionType')
  onTransactionTypeChange(val) {
    this.transactionMethods = []
    this.transactionProcessors = []
    this.$set(this.model, 'selectedMethod', undefined)
    this.$set(this.model, 'selectedProcessor', undefined)
    if (this.domain) this.loadMethodsTypes(this.domain.name, val)
  }

  @Watch('model.selectedMethod')
  onTransactionMethodChange(val) {
    this.transactionProcessors = []
    this.$set(this.model, 'selectedProcessor', undefined)
    this.loadProcessorsByMethod(val)
  }

  @Watch('userSearch')
  onSelectedUserChange(val) {
    if (val !== null) {
      this.loadUsersList(val)
    }
  }

  async loadStartData() {
    await this.loadPaymentTypes()
    await this.loadStatuses()
    await this.loadTagsList()
  }

  async loadPaymentTypes() {
    try {
      const result = await this.apiClients.serviceCashier.cashierTransactionPaymentTypes()
      if (result !== null) {
        this.paymentTypes = result
      }
    } catch (err) {
      this.snackbar = {
        show: true,
        text: this.translateAll(err.message),
        color: 'error'
      }
    }
  }

  apiParams() {
    const params: any = {}
    if (this.domain) params.domain = this.domain.name
    this.model.selectedMethod ? params.dm = this.model.selectedMethod : params.dm = -1
    this.model.selectedProcessor ? params.dmp = this.model.selectedProcessor : params.dmp = -1
    if (this.model.transactionType) params.transactionType = this.model.transactionType
    if (this.model.statuses !== undefined && this.model.statuses.length) params.statuses = this.model.statuses;
    if (this.model.paymentType) params.paymentType = this.model.paymentType;
    if (this.model.selectedUser !== undefined && this.model.selectedUser?.guid) params.guid = this.model.selectedUser.guid
    if (this.model.createdDateRangeStart) {
      const date = new Date(this.model.createdDateRangeStart)
      params.cresd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
    }
    if (this.model.createdDateRangeEnd) {
      const date = new Date(this.model.createdDateRangeEnd)
      params.creed = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 23, 59, 59, 999);
    }
    if (this.model.updatedDateRangeStart) {
      const date = new Date(this.model.updatedDateRangeStart)
      params.updsd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
    }
    if (this.model.updatedDateRangeEnd) {
      const date = new Date(this.model.updatedDateRangeEnd)
      params.upded = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 23, 59, 59, 999);
    }
    if (this.model.registrationStart) {
      const date = new Date(this.model.registrationStart)
      params.registrationStart = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
    }
    if (this.model.registrationEnd) {
      const date = new Date(this.model.registrationEnd)
      params.registrationEnd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 23, 59, 59, 999);
    }
    if (this.model.processorReference) params.processorReference = this.model.processorReference;
    if (this.model.lastFourDigits) params.lastFourDigits = this.model.lastFourDigits;
    if (this.model.id) params.id = this.model.id;
    if (this.model.transactionRuntimeQuery?.value && this.model.transactionRuntimeQuery?.operator) params.transactionRuntimeQuery = this.model.transactionRuntimeQuery.operator + this.model.transactionRuntimeQuery.value;
    if (this.model.autoApproved !== undefined) params.autoApproved = this.model.autoApproved
    if (this.model.testAccount !== undefined) params.testAccount = this.model.testAccount;
    if (this.model.transactionTagsNames !== undefined && this.model.transactionTagsNames.length) {
      let includedTransactionTags = this.model.transactionTagsNames.filter(item => item.include === true);
      let excludedTransactionTags = this.model.transactionTagsNames.filter(item => item.include === false);
      params.includedTransactionTagsNames = includedTransactionTags.map(item => item.val);
      params.excludedTransactionTagsNames = excludedTransactionTags.map(item => item.val);
    } else {
      delete params.includedTransactionTagsNames
      delete params.excludedTransactionTagsNames
    }
    return params
  }

  async loadMethodsTypes(domain: string, method: string) {
    if (method) {
      this.formLoading = true
      try {
        const result = await this.apiClients.serviceCashier.cashierTransactionMethods(domain, method)
        if (result !== null) {
          this.transactionMethods = result
        }
      } catch (err) {
        this.snackbar = {
          show: true,
          text: this.translateAll(err.message),
          color: 'error'
        }
      } finally {
        this.formLoading = false
      }
    } else {
      this.transactionMethods = []
    }
  }

  async loadProcessorsByMethod(method: number) {
    if (method) {
      this.formLoading = true
      try {
        const result = await this.apiClients.serviceCashier.cashierTransactionProcessorByMethod(method)
        if (result !== null) {
          this.transactionProcessors = result
        }
      } catch (err) {
        this.snackbar = {
          show: true,
          text: this.translateAll(err.message),
          color: 'error'
        }
      } finally {
        this.formLoading = false
      }
    } else {
      this.transactionProcessors = []
    }
  }

  async loadUsersList(search) {
    const params = {
      search: search,
    }
    if (this.userSearchLoading || !this.domain) return
    this.userSearchLoading = true
    try {
      const result = await this.UserApiService.userList(this.domain.name, params)
      if (result?.data) {
        this.userList = result.data.data
      }
    } catch (err) {

    } finally {
      this.userSearchLoading = false
    }
  }

  async loadStatuses() {
    try {
      const result = await this.apiClients.serviceCashier.cashierTransactionStatuses()
      if (result !== null) {
        this.statusesList = result
      }
    } catch (err) {
      this.snackbar = {
        show: true,
        text: this.translateAll(err.message),
        color: 'error'
      }
    }
  }

  async loadTagsList() {
    try {
      const result = await this.apiClients.serviceCashier.cashierTransactionTagsList()
      if (result !== null && result.length) {
        let list: any[] = [];
        const data = result
        for (var i = 0; i < data.length; i++) {
          list.push({value: i, include: true, val: data[i], label: data[i]});
        }
        for (var b = 0; b < data.length; b++) {
          list.push({value: b + data.length, include: false, val: data[b], label: "NOT_" + data[b]});
        }
        this.tagList = list
      }
    } catch (err) {
      this.snackbar = {
        show: true,
        text: this.translateAll(err.message),
        color: 'error'
      }
    }
  }
  get yesterdayDate(){
    let date1:any = new Date();
    date1.setDate(date1.getDate() - 1);
    return format(date1, 'yyyy-MM-dd')
  }

  clearFilter() {
    this.model = {
      autoApproved: undefined,
      testAccount: undefined,
      selectedMethod: undefined,
      selectedProcessor: undefined,
      selectedUser: undefined,
      createdDateRangeStart: undefined,
      createdDateRangeEnd: undefined,
      updatedDateRangeStart: undefined,
      updatedDateRangeEnd: undefined,
      registrationStart: undefined,
      registrationEnd: undefined,
      id: undefined,
      transactionRuntimeQuery: {
        operator: '=',
        value: undefined
      },
      transactionType: '',
      paymentType: '',
      statuses: undefined,
      transactionTagsNames: '',
      processorReference: '',
      lastFourDigits: '',
    };
  }
}
</script>

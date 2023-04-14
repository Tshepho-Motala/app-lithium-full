<template>
  <v-row data-test-id="bank-account-lookup">
    <v-col>
      <h3>{{ translate('INPUT_FIELDS.PAYMENT_PROVIDER') }}</h3>
      <v-radio-group row data-test-id="rdo-payment-provider">
        <v-radio
          :data-test-id="`rdi-payment-provider-${i}`"
          v-on:click="onPaymentProviderClick(dmp.domainMethod.name)"
          v-for="(dmp, i) in domainMethodProcessors"
          :key="dmp.id"
          :label="dmp.domainMethod.name"
          :value="dmp.domainMethod.name"
        ></v-radio>
      </v-radio-group>
      <v-form v-if="isPaymentProviderSelected()" data-test-id="frm-payment-provider">
        <div v-if="bankAccountFlag">
          <h3>{{ translate('INPUT_FIELDS.BANK_ACCOUNT.TITLE') }}</h3>
          <v-text-field
            data-test-id="txt-bank-account"
            v-model="bankAccountInput"
            :rules="bankAccountRules"
            :maxlength="10"
            @change="checkRules()"
            :label="translate('INPUT_FIELDS.BANK_ACCOUNT.PLACEHOLDER')"
            :hint="translate('INPUT_FIELDS.BANK_ACCOUNT.DESCRIPTION')"
            persistent-hint
            required
          >
          </v-text-field
          ><br />
        </div>
        <div v-if="bankCodeFlag">
          <h3>{{ translate('INPUT_FIELDS.BANK_CODE.TITLE') }}</h3>
          <v-text-field
            data-test-id="txt-bank-code"
            v-model="bankCodeInput"
            :rules="bankCodeRules"
            :items="banks"
            @change="defineBankName()"
            :label="translate('INPUT_FIELDS.BANK_CODE.PLACEHOLDER')"
            :hint="translate('INPUT_FIELDS.BANK_CODE.DESCRIPTION')"
            persistent-hint
            required
          >
          </v-text-field
          ><br />
        </div>
        <div v-if="bankNameFlag">
          <h3>{{ translate('INPUT_FIELDS.BANK_NAME.TITLE') }}</h3>
          <v-select
            data-test-id="slt-bank-name"
            v-model="bankNameInput"
            :rules="bankNameRules"
            :items="banks"
            @change="defineBankCode()"
            item-text="name"
            :label="translate('INPUT_FIELDS.BANK_NAME.PLACEHOLDER')"
            :hint="translate('INPUT_FIELDS.BANK_NAME.DESCRIPTION')"
            persistent-hint
            required
          >
          </v-select
          ><br />
        </div>
        <div v-if="phoneNumberFlag">
          <h3>{{ translate('INPUT_FIELDS.OPAY_MOBILE_NUMBER.TITLE') }}</h3>
          <v-text-field
            data-test-id="txt-mobile-number"
            v-model="phoneNumberInput"
            :rules="phoneNumberRules"
            :maxlength="13"
            @change="checkRules()"
            :label="translate('INPUT_FIELDS.OPAY_MOBILE_NUMBER.PLACEHOLDER')"
            :hint="translate('INPUT_FIELDS.OPAY_MOBILE_NUMBER.DESCRIPTION')"
            persistent-hint
            required
          >
          </v-text-field
          ><br />
        </div>
      </v-form>
      <div v-if="isPaymentProviderSelected()" class="text-right">
        <v-btn data-test-id="btn-clear" v-on:click="clear()">{{ translate('INPUT_FIELDS.CLEAR_BUTTON') }}</v-btn>
        <v-btn data-test-id="btn-lookup" v-on:click="lookup()" color="#0080FF" class="white--text" :disabled="!(checkRules() && unblockLookupBtn)">{{
          translate('INPUT_FIELDS.LOOKUP_BUTTON')
        }}</v-btn>
      </div>
    </v-col>
    <v-divider vertical inset class="divider"></v-divider>
    <v-col>
      <h3>{{ translate('OUTPUT_FIELDS.RESULTS') }}</h3>
      <div v-if="!isBankAccountLookupResponseReceived()">
        <v-row>
          <v-col>
            {{ translate('OUTPUT_FIELDS.NO_DATA') }}
          </v-col>
        </v-row>
      </div>
      <div v-if="isBankAccountLookupResponseReceived()">
        <v-row>
          <v-col>{{ translate('OUTPUT_FIELDS.PAYMENT_PROVIDER') }}</v-col>
          <v-col>{{ bankAccountLookupRequest.domainMethodName }}</v-col>
        </v-row>
        <v-row v-if="bankAccountLookupResponse.status !== null">
          <v-col>{{ translate('OUTPUT_FIELDS.STATUS') }}</v-col>
          <v-col>{{ bankAccountLookupResponse.status }}</v-col>
        </v-row>
        <v-row v-if="bankAccountLookupResponse.message !== null">
          <v-col>{{ translate('OUTPUT_FIELDS.MESSAGE') }}</v-col>
          <v-col>{{ bankAccountLookupResponse.message }}</v-col>
        </v-row>
        <v-row v-if="bankAccountLookupResponse.failedStatusReasonMessage !== null">
          <v-col>{{ translate('OUTPUT_FIELDS.FAILED_STATUS_REASON_MESSAGE') }}</v-col>
          <v-col>{{ bankAccountLookupResponse.failedStatusReasonMessage }}</v-col>
        </v-row>
        <br />
        <div v-if="bankAccountLookupResponse.status !== 'Failed'">
          <h3>{{ translate('OUTPUT_FIELDS.DATA_FROM_PROVIDER') }}</h3>
          <v-row v-for="d in dataResponse" v-bind:key="d.key">
            <v-col>{{ d.key }}</v-col>
            <v-col>{{ d.value }}</v-col>
          </v-row>
          <v-dialog v-model="showManualModal" width="900">
            <template v-slot:activator="{ on, attrs }">
              <v-btn data-test-id="btn-create-manual-withdrawal" color="red primary mt-4" dark v-bind="attrs" v-on="on">
                {{ translate('INPUT_FIELDS.CREATE_MANUAL_WITHDRAWAL') }}
              </v-btn>
            </template>
            <v-card>
              <v-card-title class="text-h5 grey lighten-2 pt-3">
                {{ translate('MANUAL_WITHDRAWAL.TITLE') }}
              </v-card-title>
              <v-card-text class="py-3 px-5">
                <v-autocomplete
                  data-test-id="sel-user"
                  v-model="selectedUser"
                  :items="items"
                  clearable
                  dense
                  label="User"
                  :search-input.sync="search"
                  :placeholder="translate('MANUAL_WITHDRAWAL.START_TYPING')"
                  prepend-icon="mdi-database-search"
                  @change="searchUser"
                  item-text="username"
                  item-value="username"
                  return-object
                ></v-autocomplete>
                <v-divider></v-divider>
                <v-row>
                  <v-col>{{ translate('OUTPUT_FIELDS.PAYMENT_PROVIDER') }}</v-col>
                  <v-col>{{ bankAccountLookupRequest.domainMethodName }}</v-col>
                </v-row>
                <v-row v-if="bankAccountLookupResponse.status !== null">
                  <v-col>{{ translate('OUTPUT_FIELDS.STATUS') }}</v-col>
                  <v-col>{{ bankAccountLookupResponse.status }}</v-col>
                </v-row>
                <v-row v-if="bankAccountLookupResponse.accountName !== null">
                  <v-col>{{ translate('OUTPUT_FIELDS.ACCOUNT_NAME') }}</v-col>
                  <v-col>{{ bankAccountLookupResponse.accountName }}</v-col>
                </v-row>
                <v-row v-if="bankAccountLookupResponse.accountNumber !== null">
                  <v-col>{{ translate('OUTPUT_FIELDS.ACCOUNT_NUMBER') }}</v-col>
                  <v-col>{{ bankAccountLookupResponse.accountNumber }}</v-col>
                </v-row>
                <v-row v-if="bankAccountLookupResponse.bankCode !== null">
                  <v-col>{{ translate('OUTPUT_FIELDS.BANK_CODE') }}</v-col>
                  <v-col>{{ bankAccountLookupResponse.bankCode }}</v-col>
                </v-row>
                <v-row v-if="bankAccountLookupResponse.bankName !== null">
                  <v-col>{{ translate('OUTPUT_FIELDS.BANK_NAME') }}</v-col>
                  <v-col>{{ bankAccountLookupResponse.bankName }}</v-col>
                </v-row>
                <v-row v-if="bankAccountLookupResponse.message !== null">
                  <v-col>{{ translate('OUTPUT_FIELDS.MESSAGE') }}</v-col>
                  <v-col>{{ bankAccountLookupResponse.message }}</v-col>
                </v-row>
                <v-row v-if="bankAccountLookupResponse.bankId !== null">
                  <v-col>{{ translate('OUTPUT_FIELDS.BANK_ID') }}</v-col>
                  <v-col>{{ bankAccountLookupResponse.bankId }}</v-col>
                </v-row>
                <div v-if="balanceIsAvailable">
                  <v-divider></v-divider>
                  <p>{{ translate('MANUAL_WITHDRAWAL.PAYMENT_METHOD') }}</p>
                  <v-text-field
                    data-test-id="txt-manual-withdrawal-amount"
                    :label="translate('MANUAL_WITHDRAWAL.AMOUNT_LABEL')"
                    v-model="manualAmount"
                    :hint="translate('MANUAL_WITHDRAWAL.FILL_AMOUNT')"
                    :prefix="currencySymbol"
                    @input="changeAmount"
                    type="number"
                    :rules="[notNegative]"
                  ></v-text-field>
                  <v-alert dense outlined type="error" v-if="showAdjustWarning">
                    {{ translate('MANUAL_WITHDRAWAL.MORE_THAN_BALANCE') }}
                  </v-alert>
                  <v-row class="mx-0">
                    <v-col>
                      <v-btn color="primary" text @click="adjustSum(5)" outlined> + 5.00 </v-btn>
                    </v-col>
                    <v-col>
                      <v-btn color="primary" text @click="adjustSum(10)" outlined> + 10.00 </v-btn>
                    </v-col>
                    <v-col>
                      <v-btn color="primary" text @click="adjustSum(50)" outlined> + 50.00 </v-btn>
                    </v-col>
                    <v-col>
                      <v-btn color="primary" text @click="adjustSum(null)" outlined>
                        {{ translate('MANUAL_WITHDRAWAL.CURRENT_BALANCE') }}
                      </v-btn>
                    </v-col>
                  </v-row>
                  <v-text-field
                    data-test-id="txt-manual-withdrawal-current-balance"
                    :label="translate('MANUAL_WITHDRAWAL.CURRENT_BALANCE')"
                    v-model="manualCurrentBalance"
                    readonly
                    :hint="translate('MANUAL_WITHDRAWAL.CURRENT_BALANCE_HINT')"
                    :prefix="currencySymbol"
                    type="number"
                    min="0"
                  ></v-text-field>
                  <v-text-field
                    data-test-id="txt-manual-withdrawal-new-balance"
                    :label="translate('MANUAL_WITHDRAWAL.NEW_BALANCE')"
                    v-model="manualNewBalance"
                    readonly
                    :hint="translate('MANUAL_WITHDRAWAL.NEW_BALANCE_HINT')"
                    :prefix="currencySymbol"
                    type="number"
                  ></v-text-field>
                  <v-text-field
                    data-test-id="txt-manual-withdrawal-comment"
                    :label="translate('MANUAL_WITHDRAWAL.COMMENT')"
                    v-model="manualComment"
                    :hint="translate('MANUAL_WITHDRAWAL.COMMENT_HINT')"
                    :rules="[notEmptyComment]"
                  ></v-text-field>
                  <div>
                    <v-alert outlined type="error" dense>
                      <p>{{ translate('MANUAL_WITHDRAWAL.WARNING') }}</p>
                      <p>{{ translate('MANUAL_WITHDRAWAL.RESTRICTION') }}</p>
                    </v-alert>
                  </div>
                  <v-checkbox
                    data-test-id="chk-manual-withdrawal-redirect"
                    v-model="manualRedirect"
                    :label="translate('MANUAL_WITHDRAWAL.REDIRECT')"
                    color="primary"
                    hide-details
                  ></v-checkbox>
                  <v-divider></v-divider>
                </div>
                <div v-if="awaitingForResponse">
                  <v-row no-gutters align="center" justify="center">
                    <v-progress-circular indeterminate color="primary" class="mt-4"></v-progress-circular>
                  </v-row>
                </div>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn
                    data-test-id="btn-create-manual-withdrawal"
                    color="primary"
                    text
                    @click="
                      createManualWithdrawal()
                      showManualModal = false
                    "
                    :disabled="!(manualAmount && manualComment && !showAdjustWarning && +manualAmount > 0)"
                  >
                    {{ translate('MANUAL_WITHDRAWAL.SUBMIT') }}
                  </v-btn>
                  <v-btn data-test-id="btn-cancel-manual-withdrawal" text @click="showManualModal = false">
                    {{ translate('MANUAL_WITHDRAWAL.CANCEL') }}
                  </v-btn>
                </v-card-actions>
              </v-card-text>
            </v-card>
          </v-dialog>
        </div>
      </div>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import { Component, Inject, Vue, Watch } from 'vue-property-decorator'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'

@Component
export default class BankAccountLookup extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  domainMethodProcessors: any = []
  banks: any = []
  currentDomainMethodProcessor: any = undefined
  paymentProviderFlag: boolean = false

  bankAccountFlag: boolean = false
  bankCodeFlag: boolean = false
  bankNameFlag: boolean = false
  phoneNumberFlag: boolean = false

  bankAccountInput: string = ''
  bankCodeInput: string = ''
  bankNameInput: string = ''
  phoneNumberInput: string = ''

  bankAccountLookupRequest = {
    domainMethodProcessorProperties: undefined,
    domainMethodName: '',
    accountNumber: '',
    bankCode: '',
    bankName: ''
  }
  bankAccountLookupResponse: any = undefined
  bankAccountLookupResponseFlag: boolean = false

  dataResponse: any = []
  dataResponseKeys: any = []
  dataResponseValues: any = []

  showManualModal: any = false
  showAdjustWarning: any = false
  awaitingForResponse: any = false
  balanceIsAvailable: any = false
  manualAmount: number | string = (0).toFixed(2)
  manualCurrentBalance: any = (0).toFixed(2)
  manualNewBalance: number | string = (0).toFixed(2)
  manualComment: string = ''
  manualRedirect: boolean = false
  search: string = ''
  selectedUser: any = null
  items: any = []
  currencySymbol: string = ''
  unblockLookupBtn: boolean = true

  bankAccountRules = [
    (v: any) => !!v || this.translate('RULES.FIELD_REQUIRED'),
    (v: any) => !/[^0-9\.]+/g.test(v) || this.translate('RULES.NAME_MUST_CONTAIN_ONLY_DIGITS'),
    (v: any) => v.length == 10 || this.translate('RULES.NAME_MUST_CONTAIN_10_DIGITS')
  ]
  bankCodeRules = [(v: any) => !!v || this.translate('RULES.FIELD_REQUIRED')]
  bankNameRules = [(v: any) => !!v || this.translate('RULES.FIELD_REQUIRED')]
  phoneNumberRules = [
    (v: any) => !!v || this.translate('RULES.FIELD_REQUIRED'),
    (v: any) => !/[^0-9\.]+/g.test(v) || this.translate('RULES.NAME_MUST_CONTAIN_ONLY_DIGITS'),
    (v: any) => v.length == 13 || this.translate('RULES.NAME_MUST_CONTAIN_13_DIGITS')
  ]

  mounted() {
    this.loadTypes()
  }

  async loadTypes() {
    await this.rootScope.provide.bankAccountLookupGeneration
      .domainMethodProcessors()
      .then((result: any) => {
        this.domainMethodProcessors = result.plain()
      })
      .catch((err: any) => {
        console.log("Can't get domain methods due ", err)
      })

    await this.rootScope.provide.bankAccountLookupGeneration
      .getCurrencySymbol()
      .then((result: any) => {
        this.currencySymbol = result
      })
      .catch((err: any) => {
        console.log("Can't get currency symbol due ", err)
      })
  }

  // Payment Provider Methods
  onPaymentProviderClick(selectedDomainMethodName: string) {
    this.paymentProviderFlag = true
    this.bankCodeInput = ''
    this.bankNameInput = ''
    this.bankAccountInput = ''
    this.phoneNumberInput = ''
    this.currentDomainMethodProcessor = this.domainMethodProcessors.find((dmp: any) => dmp.domainMethod.name === selectedDomainMethodName)
    this.viewBankAccountInputFields()
    this.getBankList()
  }

  viewBankAccountInputFields() {
    this.bankAccountFlag = this.getProperty('bank_account_lookup_bank_account_flag')
    this.bankCodeFlag = this.getProperty('bank_account_lookup_bank_code_flag')
    this.bankNameFlag = this.getProperty('bank_account_lookup_bank_name_flag')
    this.phoneNumberFlag = this.getProperty('bank_account_lookup_phone_number_flag')
  }

  getProperty(property: string) {
    const prop = this.currentDomainMethodProcessor.processor.properties.find(function (dmpProperty: any) {
      if (dmpProperty.name === property) return true
    })
    return prop !== undefined
  }

  getBankList() {
    if (!this.bankNameFlag) return
    this.rootScope.provide.bankAccountLookupGeneration
      .banks(this.currentDomainMethodProcessor.properties, this.currentDomainMethodProcessor.processor.url)
      .then((result: any) => {
        this.banks = result.plain()
      })
      .catch((err: any) => {
        this.banks = []
        console.log("Can't get banks due ", err)
      })
  }

  // Bank Code and Name Input Fields Checkings
  defineBankCode() {
    const bankName = this.bankNameInput
    const bank = this.banks.find(function (b: any) {
      if (b.name === bankName) return true
    })
    this.bankCodeInput = bank !== undefined ? bank.code : ''
    this.checkRules()
  }

  defineBankName() {
    const bankCode = this.bankCodeInput
    const bank = this.banks.find(function (b: any) {
      if (b.code === bankCode) return true
    })
    this.bankNameInput = bank !== undefined ? bank.name : ''
    this.checkRules()
  }

  // Validation Methods
  isPaymentProviderSelected(): boolean {
    return this.paymentProviderFlag
  }

  isBankAccountLookupResponseReceived(): boolean {
    return this.bankAccountLookupResponseFlag
  }

  checkRules() {
    if (this.phoneNumberFlag) return this.phoneNumberInput !== '' && !/[^0-9\.]+/g.test(this.phoneNumberInput) && this.phoneNumberInput.length === 13
    return (
      this.bankAccountInput !== '' &&
      !/[^0-9\.]+/g.test(this.bankAccountInput) &&
      this.bankAccountInput.length === 10 &&
      this.bankCodeInput !== '' &&
      this.bankNameInput !== ''
    )
  }

  // Buttons
  async lookup() {
    this.clearData()
    this.payloadBankAccountLookupRequest()
    this.unblockLookupBtn = false
    return await this.rootScope.provide.bankAccountLookupGeneration
      .lookup(this.bankAccountLookupRequest, this.currentDomainMethodProcessor.processor.url)
      .then((result: any) => {
        this.bankAccountLookupResponse = result.plain()
        this.bankAccountLookupResponseFlag = true
        console.log('Bank Account Lookup Response:', this.bankAccountLookupResponse)
        this.pushDataToBankAccountLookupResponses(Object.entries(this.bankAccountLookupResponse))
      })
      .catch((err: any) => {
        console.log("Can't execute bank account lookup due ", err)
      })
      .finally(() => (this.unblockLookupBtn = true))
  }

  clearData() {
    this.bankAccountLookupResponse = undefined
    this.dataResponse = []
    this.dataResponseKeys = []
    this.dataResponseValues = []
    this.bankAccountLookupResponseFlag = false
  }

  payloadBankAccountLookupRequest() {
    this.bankAccountLookupRequest.accountNumber = this.getAccountNumber()
    this.bankAccountLookupRequest.bankCode = this.bankCodeInput
    this.bankAccountLookupRequest.bankName = this.bankNameInput
    this.bankAccountLookupRequest.domainMethodName = this.currentDomainMethodProcessor.domainMethod.name
    this.bankAccountLookupRequest.domainMethodProcessorProperties = this.currentDomainMethodProcessor.properties
  }

  getAccountNumber() {
    if (this.phoneNumberFlag) return this.phoneNumberInput
    return this.bankAccountInput
  }

  pushDataToBankAccountLookupResponses(entries: any) {
    for (let i = 0; i < entries.length; i++) {
      const obj = entries[i]
      for (const key in obj) {
        if (obj[key] === null) continue
        const fieldName: string = obj[key]
        const fieldValue = entries[i][1]
        if (fieldName.startsWith('_')) break
        if (fieldValue !== undefined && fieldValue !== 0 && fieldValue !== null && fieldValue !== '') {
          this.dataResponseKeys.push(this.camelCaseToSentenceCaseText(fieldName))
          this.dataResponseValues.push(fieldValue)
          break
        }
      }
    }
    for (let i = 0; i < this.dataResponseKeys.length; i++) {
      this.dataResponse.push({
        key: this.dataResponseKeys[i],
        value: this.dataResponseValues[i]
      })
    }
  }

  camelCaseToSentenceCaseText(text: any) {
    const result = text.replace(/([A-Z])/g, ' $1')
    const finalResult = result.charAt(0).toUpperCase() + result.slice(1)
    return finalResult
  }

  clear() {
    this.paymentProviderFlag = false
    this.bankAccountFlag = false
    this.bankCodeFlag = false
    this.bankNameFlag = false
    this.phoneNumberFlag = false
    this.bankAccountLookupResponseFlag = false
    this.banks = []
    this.currentDomainMethodProcessor = undefined
    this.bankAccountInput = ''
    this.bankCodeInput = ''
    this.bankNameInput = ''
    this.phoneNumberInput = ''
    this.bankAccountLookupRequest.domainMethodProcessorProperties = undefined
    this.bankAccountLookupRequest.domainMethodName = ''
    this.bankAccountLookupRequest.accountNumber = ''
    this.bankAccountLookupRequest.bankCode = ''
    this.bankAccountLookupRequest.bankName = ''
    this.bankAccountLookupResponse = undefined
    this.dataResponse = []
    this.dataResponseKeys = []
    this.dataResponseValues = []
    this.domainMethodProcessors = this.loadTypes()
  }

  createManualWithdrawal(): any {
    this.rootScope.provide.bankAccountLookupGeneration.createManualWithdrawal(
      this.currentDomainMethodProcessor.domainMethod,
      this.selectedUser.guid,
      this.bankAccountLookupRequest.accountNumber,
      this.bankAccountLookupRequest.bankCode,
      this.manualAmount.toString(),
      this.manualComment,
      this.manualRedirect
    )
  }

  adjustSum(count: number | null): any {
    if (count) {
      if (count + +this.manualAmount > +this.manualCurrentBalance) {
        this.showAdjustWarning = true
        return false
      } else {
        this.manualAmount = (+this.manualAmount + +count).toFixed(2)
        this.manualNewBalance = (+this.manualCurrentBalance - +this.manualAmount).toFixed(2)
      }
    } else {
      this.manualAmount = (+this.manualCurrentBalance).toFixed(2)
      this.manualNewBalance = (0).toFixed(2)
    }
    this.showAdjustWarning = false
  }

  searchUser(): any {
    if (this.selectedUser) {
      // if you clean user's select
      this.awaitingForResponse = true
      this.rootScope.provide.bankAccountLookupGeneration
        .getUserBalance(this.currentDomainMethodProcessor.domainMethod.domain.name, this.selectedUser.guid)
        .then((res) => {
          let resFix = 0

          if (res) {
            resFix = res
          }

          this.manualCurrentBalance = (resFix / 100).toFixed(2)
          this.balanceIsAvailable = true

          // let temp = res.toLocaleString("en-IN", {minimumFractionDigits: 3, maximumFractionDigits: 3})
        })
        .finally(() => (this.awaitingForResponse = false))
    } else {
      this.manualCurrentBalance = (0).toFixed(2)
      this.balanceIsAvailable = false
    }
  }

  @Watch('search', { deep: true })
  onSearchChanged() {
    if (this.search) {
      this.rootScope.provide.bankAccountLookupGeneration
        .searchUsers(this.search)
        .then((res: any) => {
          this.items = res.plain()
        })
        .catch((err: any) => {
          console.log("Can't find any users due ", err)
        })
    } else {
      this.items = []
    }
  }

  changeAmount(): any {
    if (+this.manualAmount > +this.manualCurrentBalance) {
      this.showAdjustWarning = true
      return false
    } else {
      this.manualNewBalance = (+this.manualCurrentBalance - +this.manualAmount).toFixed(2)
    }
    this.showAdjustWarning = false
  }

  //rules
  notNegative(value: number): boolean {
    if (value > 0) {
      return true
    } else {
      return false
    }
  }

  notEmptyComment(value: string): boolean {
    if (value.length > 0) {
      return true
    } else {
      return false
    }
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant('UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP.' + text)
  }
}
</script>

<style scoped>
.divider {
  background-color: #000;
  border-width: 1px !important;
}
.lookupButton {
  background-color: #0080ff;
  color: white;
}
</style>
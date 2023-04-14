<template>
  <div>
    <v-dialog
        data-test-id="dialog-auto-withdrawal-rule-edit"
        v-model="dialog"
        max-width="500px"
        @click:outside="close"
    >
      <v-card>
        <v-toolbar
            color="primary"
            dark
            style="font-size: 22px"
        >{{ !isAdd ? translate("TITLE_RULE") : translate("NEW_RULE_TITLE") }}
        </v-toolbar>
        <v-card-text class="mt-2" v-if="editItem">
          <v-form data-test-id="frm-auto-withdrawal-rule-edit" v-model="validForm" ref="form">
            <v-row>
              <v-col
                  class="d-flex"
                  cols="12"
              >
                <v-switch
                    v-model="editItem.enabled"
                    :hint="translate('HINT')"
                    :label="translate('BUTTONS.ENABLED')"
                ></v-switch>
              </v-col>
              <v-col
                  cols="12"
                  md="12"
                  v-if="rulesOperators.length"
              >
                <v-select
                    v-model="editItem.field"
                    :items="rulesOperators"
                    :disabled="!isAdd"
                    item-text="displayName"
                    item-value="id"
                    :label="translate('FORM.FIELD')"
                    :rules="[v => v !== null ||  translate('FORM.FIELD_REQUIRED')]"
                    :hint="translateAll('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.FIELDS.FIELD.DESCRIPTION')"
                    persistent-hint
                    required

                ></v-select>
              </v-col>

              <v-col
                  cols="12"
                  md="12"
                  v-if="hasSetting && settings.length">
                <div v-for="setting in settings" :key="setting.key">
                  <v-select
                      v-if="setting.type === 'MULTISELECT' && setting.options.length"
                      v-model="setting.value"
                      :items="setting.options"
                      :menu-props="{ maxHeight: '300' }"
                      label="Settings"
                      item-text="name"
                      item-value="name"
                      :loading="loadingOptions"
                      multiple
                  ></v-select>

                  <v-select
                      v-else-if="(setting.type === 'SINGLESELECT' ||setting.type === 'BOOLEAN') && setting.options.length"
                      v-model="setting.value"
                      :items="setting.options"
                      :menu-props="{ maxHeight: '300' }"
                      label="Settings"
                      item-text="name"
                      item-value="name"
                  ></v-select>

                  <v-text-field
                      v-else-if="setting.type === 'LONG'"
                      v-model="setting.value"
                      type="number"
                      min="0"
                      label="Settings"
                  ></v-text-field>

                  <v-text-field
                      v-else-if="setting.type === 'STRING'"
                      v-model="setting.value"
                      type="text"
                      label="Settings"
                  ></v-text-field>
                </div>
              </v-col>


              <v-col
                  cols="12"
                  md="12"
                  v-if="parseInt(editItem.field) >= 0"
              >
                <v-select
                    v-model="editItem.operator"
                    :items="rulesFields"
                    item-text="name"
                    item-value="id"
                    :label="translate('FORM.OPERATOR')"
                    :rules="[v => v !== null || translate('FORM.FIELD_REQUIRED')]"
                    :hint="translateAll('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.FIELDS.OPERATOR.DESCRIPTION')"
                    persistent-hint
                    required
                ></v-select>
              </v-col>

              <template v-if="parseInt(editItem.field) >= 0  && parseInt(editItem.operator) === 0 ">
                <v-col
                    cols="12"
                    md="12"
                >
                  <v-text-field
                      v-model="editItem.value"
                      label="Value: Range Start *"
                      type="number"
                      min="0"
                      :rules="[v => (!!v && parseInt(v) >= 0 ) || translate('FORM.FIELD_REQUIRED')]"
                      required
                  ></v-text-field>
                </v-col>
                <v-col
                    cols="12"
                    md="12"

                >
                  <v-text-field
                      v-model="editItem.value2"
                      label="Value: Range End *"
                      type="number"
                      min="0"
                      :rules="[
                          v => (!!v && parseInt(v) >=0 ) || translate('FORM.FIELD_REQUIRED'),
                          v => (v && parseInt(v) > parseInt(this.editItem.value) ) || translateAll('UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.BETWEEN.RANGEWARN')
                       ]"
                      required
                  ></v-text-field>
                </v-col>
              </template>


              <template
                  v-else-if="valueType === 'BOOLEAN'">
                <v-col
                    cols="12"
                    md="12"
                >
                  <v-select
                      v-model="editItem.value"
                      :items="rulesSelectValueYesOrNo"
                      item-text="name"
                      item-value="name"
                      required
                      :label="translate('FORM.VALUE')"
                      :rules="[v => !!v || translate('FORM.FIELD_REQUIRED')]"
                  ></v-select>
                </v-col>
              </template>

              <template v-else-if="valueType === 'MULTISELECT'">
                <v-col
                    cols="12"
                    md="12"
                >
                  <v-select
                      v-model="editItem.value"
                      :items="ruleValueMultiselectOptions"
                      :menu-props="{ maxHeight: '300' }"
                      label="Value"
                      item-text="name"
                      item-value="id"
                      :rules="[v => !!v.length ||  translate('FORM.FIELD_REQUIRED')]"
                      required
                      multiple
                  ></v-select>
                </v-col>
              </template>


              <template v-else-if="parseInt(editItem.field) >= 0   && parseInt(editItem.operator) >= 0">
                <v-col
                    cols="12"
                    md="12"
                >
                  <v-text-field
                      v-model="editItem.value"
                      type="number"
                      min="0"
                      :label="translate('FORM.VALUE')"
                      :rules="[v => (!!v && parseInt(v) >= 0 ) || translate('FORM.FIELD_REQUIRED')]"
                      required
                  ></v-text-field>
                </v-col>
              </template>

            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
              color="blue darken-1"
              text
              @click="close"
          >
            {{ translate("BUTTONS.CANCEL") }}
          </v-btn>
          <v-btn
              color='primary'
              @click="saveItem"
              data-test-id="btn-auto-withdrawal-rule--save"
          >
            {{ translate("BUTTONS.SAVE") }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import {Component, Mixins, Inject, Prop, Watch} from "vue-property-decorator";
import AssetTabMixin from '@/plugin/cms/mixins/AssetTabMixin';
import {RootScopeInterface} from '@/core/interface/ScopeInterface'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import {
  AutoWithdrawalRuleItem,
  AutoWithdrawalItemValueYesOrNo,
  AutoWithdrawalItemOperator,
  AutoWithdrawalItemField, AutoWithdrawalItem,
} from '@/core/interface/AutoWithdrawalInterface'
import AutoWithdrawalRulesetsApiInterface from "@/core/interface/axios-api/AutoWithdrawalRulesetsApiInterface";
import AutoWithdrawalRulesetsApiService from '@/core/axios/axios-api/AutoWithdrawalRulesetsApi'

@Component
export default class EditLoadedRulesItem extends Mixins(AssetTabMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({required: false}) dataRule!: AutoWithdrawalRuleItem | null
  @Prop({required: false}) dataRulset!: AutoWithdrawalItem
  @Prop({default: false}) isAdd!: boolean
  @Prop({required: true}) domainName!: string
  @Prop({default: false}) isUpdateRule!: boolean

  dialog: Boolean = false
  editItem: AutoWithdrawalRuleItem = {
    enabled: true,
    field: null,
    operator: null,
    settings: [],
    value: null,
    value2: null,
  }
  allRulesFields: AutoWithdrawalItemField[] = []
  rulesFields: AutoWithdrawalItemField[] = []
  rulesOperators: AutoWithdrawalItemOperator[] = []
  ruleValueMultiselectOptions: any[] = []
  rulesSelectValueYesOrNo: AutoWithdrawalItemValueYesOrNo[] = [{id: 1, name: "Yes"}, {id: 2, name: "No"}]
  valueType: string = ''
  validForm: Boolean = false
  $refs!: {
    form: any
  }
  settings: any[] = []
  hasSetting: Boolean = false
  AutoWithdrawalRulesetsApiService: AutoWithdrawalRulesetsApiInterface = new AutoWithdrawalRulesetsApiService(this.userService)
  loadingOptions: boolean = false

  @Watch('editItem.field')
  onFieldChange = (newVal: number, prevVal: number) => {
    if (prevVal !== null) {
      this.editItem.operator = null
    }
    this.loadRuleOperatorData(newVal)

    if (this.valueType === 'MULTISELECT' && prevVal) {
      this.updateValue([])
    }
  }

  async loadRuleOperatorData(ruleFieldId: number) {
    this.loadingOptions = true
    try {
      const response = await this.AutoWithdrawalRulesetsApiService.ruleOperatorData(this.domainName, ruleFieldId)
      if (response?.data && response.data.successful) {
        this.valueType = response.data.data.value.type
        this.rulesFields = response.data.data.operator.options
        this.ruleValueMultiselectOptions = response.data.data.value.options
        if(this.editItem?.value && Array.isArray(this.editItem.value)){
          if(this.valueType !== 'MULTISELECT'){
            const listValue:string[] = []
            this.editItem.value.forEach((el:any) => listValue.push(el.description))
            this.$set(this.editItem, 'value', listValue.join(','))
          } else if(typeof this.editItem.value[0] === 'object'){
            const listValue:string[] = []
            this.editItem.value.forEach((el:any) => listValue.push(el.value))
            this.$set(this.editItem, 'value', listValue)
          }
        }
        if (Object.keys(response.data.data.settings).length == 0) {
          this.settings = []
          this.hasSetting = false
        } else {
          this.settings = Object.values(response.data.data.settings)
          this.hasSetting = true
          if (this.editItem?.settings.length) {
            this.editItem.settings.forEach((el: any) => {
              this.settings.forEach((elem: any) => {
                if (el.key === elem.key) {
                  if (elem.type === 'MULTISELECT') {
                    const reg = /\s*,\s*/;
                    elem.value = el.value.toString().trim().split(reg)
                  } else {
                    elem.value = el.value
                  }
                }
              })
            })
          }
        }
      } else {
        this.logService.error('Error loaded')
      }

    } catch (err) {
      this.logService.error(err)
    } finally {
      this.loadingOptions = false
    }
  }

  async loadFields() {
    try {
      const response = await this.AutoWithdrawalRulesetsApiService.ruleFieldsDataUrl()
      if (response?.data && response.data.successful) {
        let rulesOperatorsList: AutoWithdrawalItemOperator[] = response.data.data
        if (this.dataRulset?.rules.length && this.isAdd) {
          this.dataRulset.rules.forEach((rule: AutoWithdrawalRuleItem) => {
            rulesOperatorsList = rulesOperatorsList.filter((operator: AutoWithdrawalItemOperator) => operator.id !== rule.field)
          })
        }
        this.rulesOperators = [...rulesOperatorsList]
        if (this.dataRule) {
          this.editItem = {...this.dataRule}
          if (this.editItem.operator !== null) {
            this.editItem.operator = this.editItem.operator.toString()
          }
        }

      } else {
        this.rulesOperators = []
      }
    } catch (err) {
      this.logService.error(err.message)
    }
  }

  saveItem() {
    this.$refs.form.validate()
    if (this.validForm) {
      if (Number(this.editItem.operator) !== 0) {
        this.editItem.value2 = null
      }

      if (Array.isArray(this.editItem.value)) {
        this.editItem.value = this.editItem.value.join(',')
      }

      if (this.hasSetting) {
        let settings = [...this.settings]
        settings.forEach((el: any) => {
          if (Array.isArray(el.value)) {
            el.value = el.value.join(',')
          }
          el.code = {
            type: el.type,
            id: el.id
          }
          delete el.options
          delete el.type
          delete el.id
        })
        this.editItem.settings = settings
      }
      if (this.isAdd) {
        this.$emit('saveNewItem', this.editItem)
      } else {
        this.$emit('saveItem', this.editItem)
      }
      return
    }
    return;
  }
  async doWork() {
    await this.loadFields()
  }

  mounted() {
    this.doWork()
    this.$nextTick(() => {
      this.dialog = true
    })
  }

  updateValue(data: any) {
    this.$set(this.editItem, 'value', data)
  }

  close() {
    this.$emit('cancel')
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.IMPORT." + text)
  }

  translateAll(text: string): any {
    return this.translateService.instant(text)
  }
}
</script>

<template>
  <div>
    <v-dialog
        data-test-id="dialog-auto-withdrawal-rulset-edit"
        v-model="dialog"
        max-width="1000px"
        @click:outside="close"
    >
      <v-card>
        <v-toolbar
            color="primary"
            dark
            class="mb-3"
            style="font-size: 22px"
        >{{ pageTitle }}
        </v-toolbar>
        <v-card-text v-if="editItem">
          <v-row>
            <v-form data-test-id="frm-auto-withdrawal-rulset-edit" style="width: 100%" v-model="validForm" ref="form">
              <v-col
                  cols="12"
                  sm="6"
                  md="6"
              >
                <v-text-field
                    v-model="editItem.name"
                    :label="translate('FORM.NAME')"
                    :rules="[v => !!v || translate('FORM.NAME_REQUIRED')]"
                    required
                ></v-text-field>
              </v-col>
              <v-col
                  cols="12"
                  sm="6"
              >
                <v-select
                    v-model="editItem.domain"
                    :items="domains"
                    item-text="name"
                    item-value="name"
                    :rules="[v => !!v || translate('FORM.FIELD_REQUIRED')]"
                    required
                    :label="translate('FORM.DOMAIN')"
                    return-object
                    :disabled="isUpdate"
                ></v-select>
              </v-col>
              <v-col
                  class="d-flex"
                  cols="12"
                  sm="6"
              >
                <v-switch
                    v-model="editItem.enabled"
                    :label="translate('FORM.ENABLED')"
                ></v-switch>
              </v-col>


              <v-col
                  class="d-flex"
                  cols="12"
                  sm="6"
              >
                <v-switch
                    v-model="editItem.delayedStart"
                    :label="translate('FORM.DELAYED')"
                ></v-switch>
              </v-col>
              <v-col
                  v-if="editItem.delayedStart === true"
                  cols="12"
                  sm="6"
                  md="6"
              >
                <v-text-field
                    v-model="editItem.delay"
                    :label="translate('FORM.DELAY') + ' (ms)'"
                    :rules="[v => !!v || translate('FORM.FIELD_REQUIRED')]"
                    type="number"
                    min="0"
                    required
                ></v-text-field>
              </v-col>
            </v-form>
            <rule-edit-table @deleteRule="deleteRule" @saveNewRuleItem="saveNewRuleItem" @saveRuleItem="saveRuleItem"
                             v-if="editItem && !isUpdate" :showAddButton="editItem && editItem.domain"
                             :dataItem="editItem"></rule-edit-table>
          </v-row>
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
          >
            {{ translate("BUTTONS.SAVE") }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import {Component, Mixins, Inject, Prop} from "vue-property-decorator";
import AssetTabMixin from '@/plugin/cms/mixins/AssetTabMixin';
import {RootScopeInterface} from '@/core/interface/ScopeInterface'
import {AutoWithdrawalItem, AutoWithdrawalRuleItem} from '@/core/interface/AutoWithdrawalInterface'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import AutoWithdrawalRuleMixin from "@/plugin/cashier/autowithdrawals/mixins/AutoWithdrawalRuleMixin";
import RuleEditTable from "@/plugin/cashier/autowithdrawals/components/RuleEditTable.vue";

@Component({
  components: {
    RuleEditTable
  }
})
export default class EditLoadedRulesItem extends Mixins(AssetTabMixin, AutoWithdrawalRuleMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({required: false}) dataItem!: AutoWithdrawalItem | null
  @Prop({default: false}) isClone!: boolean
  @Prop({default: false}) isNewItem!: boolean
  @Prop({default: false}) isUpdate!: boolean
  dialog: boolean = false
  editItem: any = null
  defaultNewItem: AutoWithdrawalItem = {
    delay: null,
    delayedStart: false,
    domain: null,
    enabled: false,
    name: "",
    rules: []
  }
  domains: any[] = []
  validForm: boolean = false
  $refs!: {
    form: any
  }

  close() {
    this.dialog = false
    this.$emit('cancel')
  }

  saveItem() {
    this.$refs.form.validate()
    if (this.validForm) {
      if (!this.editItem.delayedStart) {
        this.editItem.delay = null
      }
      this.$emit('saveItem', this.editItem)
    }
  }

  mounted() {
    this.domains = this.userService.domainsWithAnyRole(["ADMIN", "AUTOWITHDRAWALS_*"])
    if (this.isNewItem) {
      this.editItem = this.defaultNewItem
    } else if (this.dataItem) {
      this.editItem = {...this.dataItem}
    }

    this.$nextTick(() => {
      this.dialog = true
    })

  }

  deleteRule(item: any) {
    if (this.editItem.rules.length) {
      this.editItem.rules = this.editItem.rules.filter((el: any) => el.id !== item.id)
    }
  }

  saveRuleItem(item: AutoWithdrawalRuleItem) {
    this.editItem.rules = this.editItem.rules.map((el: any) => {
      if (el.id === item.id) {
        return item
      }
      return el
    })
  }

  saveNewRuleItem(item: AutoWithdrawalRuleItem) {
    const rule: AutoWithdrawalRuleItem = item
    rule.id = this.editItem.rules.length + 1
    this.editItem.rules.push(rule)
  }

  get pageTitle() {
    if (this.isClone)
      return this.translate("CLONE_TITLE")
    else if (this.isNewItem)
      return 'Add an Auto-Withdrawal Ruleset '
    else
      return this.translate("EDIT_TITLE")
  }


  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.IMPORT." + text);
  }
}
</script>

<template>
  <div  style="width: 100%">
    <v-row v-if="editItem">
      <v-col
          class="d-flex justify-space-between"
          cols="12"
      >
        <v-toolbar
            class="mb-0"
            color="grey lighten-4"
            dark
            flat
        >
          <v-toolbar-title style="width: 100%" class="d-flex justify-space-between">
            <span style="color: #000"> {{translate("TITLE_TABLE")}} </span>
            <v-btn
                color="primary"
                dark
                class="mb-0"
                v-bind="attrs"
                @click="openNewRuleModal"
                v-on="on"
                v-if="editItem && showAddButton && canEdit"
                data-test-id="btn-auto-withdrawal-rule--add"
            >
              Add new rule
            </v-btn>
          </v-toolbar-title>
        </v-toolbar>

      </v-col>
      <v-col
          class="d-flex"
          cols="12"
      >

        <v-data-table
            style="width: 100%"
            :headers="headers"
            :items="editItem.rules"
            item-key="id"
            hide-default-footer
            data-test-id="tbi-auto-withdrawal--rule"
        >
          <template v-slot:[`item.enabled`]="{ item }">
            <v-btn
                v-if="item.enabled"
                x-small
                color="success"
                dark
            >
              {{translate("BUTTONS.ENABLED")}}
            </v-btn>
            <v-btn
                v-else
                x-small
                color="error"
                dark
            >
              {{translate("BUTTONS.DISABLED")}}
            </v-btn>

          </template>
          <template v-slot:[`item.field`]="{ item }">
            {{findNameRulesFields(item)}}
          </template>
          <template v-slot:[`item.operator`]="{ item }">
            {{findNameRulesOperator(item)}}
          </template>
          <template v-slot:[`item.value`]="{ item }">
            <template v-if="findNameRulesOperator(item) === 'Between' && item.value2">
              {{valueList(item.value)}} to {{item.value2}}
            </template>
            <template v-else>
              <span style="max-width: 300px;     display: block;">
                {{valueList(item.value)}}
              </span>

            </template>
          </template>
          <template v-slot:[`item.edit`]="{ item }">
            <div class="d-flex">
              <v-btn
                  v-if="canEdit"
                  small
                  color="primary"
                  class="ml-2"
                  @click="openEditItemDialog(item)"
                  data-test-id="btn-auto-withdrawal-rule--edit"
              >
                {{translate("BUTTONS.EDIT")}}
              </v-btn>
              <v-btn
                  small
                  v-if="canEdit"
                  color="error"
                  class="ml-2"
                  @click="deleteRule(item)"
                  data-test-id="btn-auto-withdrawal-rule--delete"
              >
                {{translate("BUTTONS.DELETE")}}
              </v-btn>
            </div>
          </template>
        </v-data-table>
      </v-col>
    </v-row>

    <edit-rule-item @saveItem="saveRuleItem" @saveNewItem="saveNewRuleItem" @cancel="closeEditItemDialog"
                    v-if="showEditRuleItemDialog" :domainName="editItem.domain.name" :isAdd="isAddNewRule"
                    :dataRule="editRule" :data-rulset="dataItem" :isUpdateRule="updateRule"></edit-rule-item>
  </div>
</template>

<script lang="ts">
import {Component, Mixins, Inject, Prop} from "vue-property-decorator";
import AssetTabMixin from '@/plugin/cms/mixins/AssetTabMixin';
import {RootScopeInterface} from '@/core/interface/ScopeInterface'
import {AutoWithdrawalItem, AutoWithdrawalRuleItem} from '@/core/interface/AutoWithdrawalInterface'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import EditRuleItem from '@/plugin/cashier/autowithdrawals/components/EditRuleItem.vue'
import AutoWithdrawalRuleMixin from "@/plugin/cashier/autowithdrawals/mixins/AutoWithdrawalRuleMixin";
import {ConfirmDialogInterface} from "@/plugin/components/dialog/DialogInterface";

@Component({
  components: {
    EditRuleItem
  }
})
export default class RuleEditTable extends Mixins(AssetTabMixin, AutoWithdrawalRuleMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({required: true}) dataItem!: AutoWithdrawalItem
  @Prop({required: false, default: false}) showAddButton!: Boolean
  @Prop({ default: false }) isUpdateRule!: boolean
  @Prop({ default: true }) canEdit!: boolean
  editItem: any = null
  headers = [
    {
      text: 'Field',
      align: 'start',
      sortable: false,
      value: 'field',
    },
    {text: 'Operator', sortable: false, value: 'operator'},
    {text: 'Value', align: 'center', sortable: false, width: '150', value: 'value'},
    {text: 'Enabled', sortable: false, value: 'enabled'},
    {text: '', sortable: false, value: 'edit'},

  ]
  showEditRuleItemDialog: boolean = false
  editRule: AutoWithdrawalRuleItem | null = null
  isAddNewRule: boolean = false
  updateRule: boolean = false

  openNewRuleModal() {
    this.updateRule = false
    this.editRule = null
    this.isAddNewRule = true
    this.showEditRuleItemDialog = true
  }

  closeEditItemDialog() {
    this.showEditRuleItemDialog = false
  }

  valueList(value){
    if(value && Array.isArray(value) ){
      if(value?.length){
        const listValue:string[] = []
        value.forEach((el:any) => listValue.push(el.description))
        return listValue.join(',')
      }
      return ''
    } else {
      return  value
    }

  }

  openEditItemDialog(itemData: any) {
    this.isAddNewRule = false
    if(this.isUpdateRule){
      this.updateRule = true
    } else{
      this.updateRule = false
    }
    const item = {...itemData}
    if ((item.field === 6 || item.field === 7 || item.field === 11) && Array.isArray(item.value) === false) {
      const reg = /\s*,\s*/;
      item.value = item.value.toString().trim().split(reg)
    }
    this.editRule = item
    this.showEditRuleItemDialog = true
  }

  mounted() {
    if (this.dataItem) {
      this.editItem = this.dataItem
    }

  }

  saveRuleItem(item: AutoWithdrawalRuleItem) {
    this.$emit('saveRuleItem', item)
    this.showEditRuleItemDialog = false
  }

  saveNewRuleItem(item: AutoWithdrawalRuleItem) {
    this.$emit('saveNewRuleItem', item)
    this.showEditRuleItemDialog = false
  }

  deleteRule(item: AutoWithdrawalRuleItem) {
    const params: ConfirmDialogInterface = {
      title: 'Please confirm',
      text: 'Are you sure you want to delete this item.',
      btnPositive: {
        text: 'Confirm',
        color: 'success',
        onClick: () => {
          this.$emit('deleteRule', item)
        }
      },
      btnNegative: {
        text: 'Cancel',
        flat: true,
        color: 'error',
        onClick: () => {
        }
      }
    }
    this.listenerService.call('dialog-confirm', params)

  }


  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.IMPORT." + text);
  }
}
</script>

<template>
  <v-dialog
      data-test-id="dialog-auto-withdrawal-rulset-list"
      v-model="dialog"
      max-width="1200"
      @click:outside="close"
  >
    <edit-loaded-rules-item @saveItem="saveItem" @cancel="closeEditItemDialog" v-if="showEditItemDialog"  :dataItem="editItem"></edit-loaded-rules-item>
    <v-card>
        <v-toolbar
            color="primary"
            dark
            class="mb-4"
        >{{ translate("TITLE_LOADED") }}</v-toolbar>
        <v-card-text>
          <v-alert
              dense
              text
              type="success"
              class="mb-3"
              v-if="requestMessage"
          >
            {{requestMessage}}
          </v-alert>
          <v-alert
              dense
              outlined
              type="error"
              class="mb-3"
              v-if="messageError"
          >
            {{messageError}}
          </v-alert>
          <v-data-table
              data-test-id="tbl-auto-withdrawal-rulset-list"
              :headers="headers"
              :items="data"
              item-key="id"
              :loading="loading"
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
            <template v-slot:[`item.rules`]="{ item }">
              {{ item.rules.length }}
            </template>
            <template v-slot:[`item.lastUpdated`]="{ item }">
              {{ timestampToDate(item.lastUpdated) }}
            </template>
            <template v-slot:[`item.edit`]="{ item }">
              <div class="d-flex">
                <v-btn
                    small
                    color="primary"
                    class="ml-2"
                    @click="editData(item)"
                >
                  {{translate("BUTTONS.EDIT")}}
                </v-btn>
                <v-btn
                    small
                    color="error"
                    class="ml-2"
                    @click="deleteData(item)"
                >
                  {{translate("BUTTONS.DELETE")}}
                </v-btn>
              </div>
            </template>
          </v-data-table>
        </v-card-text>
        <v-card-actions class="justify-end">
          <v-btn
              text
              @click="dialog = false"
          >
            {{translate("BUTTONS.CANCEL")}}
          </v-btn>
          <v-btn
              color="primary"
              class="ml-2"
              @click="sendData"
              :disabled="loading"
              data-test-id="btn-auto-withdrawal-rulset-edit--save"
          >
            {{translate("BUTTONS.SAVE")}}
          </v-btn>

        </v-card-actions>
      </v-card>
  </v-dialog>
</template>

<script lang="ts">
import {Component, Mixins, Inject, Prop} from "vue-property-decorator";
import AssetTabMixin from '@/plugin/cms/mixins/AssetTabMixin';
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import EditLoadedRulesItem from '@/plugin/cashier/autowithdrawals/components/EditLoadedRulesItem.vue'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import { AutoWithdrawalItem } from '@/core/interface/AutoWithdrawalInterface'
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import AutoWithdrawalRulesetsApiService from "@/core/axios/axios-api/AutoWithdrawalRulesetsApi";
import AutoWithdrawalRulesetsApiInterface from "@/core/interface/axios-api/AutoWithdrawalRulesetsApiInterface";

@Component({
  components: {
    EditLoadedRulesItem
  }
})
export default class LoadedRulesTable extends Mixins(AssetTabMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Inject('userService') readonly userService!: UserServiceInterface

  @Prop({ required: true }) dataTable!: AutoWithdrawalItem[]
  dialog:boolean = false
  data:AutoWithdrawalItem[] = []
  singleSelect:boolean = false
  snackbar:boolean = false
  selected =  []
  requestMessage:string = ''
  messageError:string = ''
  headers = [
    {
      text: 'Name',
      align: 'start',
      sortable: false,
      value: 'name',
    },
    { text: 'Domain', sortable: false, value: 'domain.name' },
    { text: 'Enabled', sortable: false, value: 'enabled' },
    { text: 'Rules', align: 'center', sortable: false, value: 'rules' },
    { text: 'Last Updated', sortable: false, value: 'lastUpdated' },
    { text: 'Last Updated By', sortable: false, value: 'lastUpdatedBy' },
    { text: '', sortable: false, value: 'edit' },

  ]
  editItem:AutoWithdrawalItem | null = null
  showEditItemDialog:boolean = false
  loading:boolean = false

  //  transfer the userService in ApiClass
  AutoWithdrawalRulesetsApiService:AutoWithdrawalRulesetsApiInterface = new AutoWithdrawalRulesetsApiService(this.userService)

  async sendData() {
    this.loading = true
    this.snackbar = false
    const sendData:AutoWithdrawalItem[] = JSON.parse(JSON.stringify(this.data))
    sendData.forEach((el:any) => {
      el.id = null
      if(el.rules.length){
        el.rules.forEach((rule:any) => {
          rule.id = null
        })
      }
    })
    try {
      const result =  await  this.AutoWithdrawalRulesetsApiService.submitImportData(sendData)

      if(result?.data?.status === 500 || result?.data?.successful === false){
        this.messageError = result.data.message
        this.requestMessage = ''
      } else {
        this.snackbar = true
        this.requestMessage = 'Import was successful'
        this.messageError = ''
      }
      this.loading = false
    } catch (err) {
      this.loading = false
      this.requestMessage = ''
      this.messageError = err.message
      this.logService.error(err.message)
    }
  }
  closeEditItemDialog(){
    this.showEditItemDialog = false
  }
  editData(item:any){
    this.editItem = item
    this.showEditItemDialog = true
  }
  saveItem(item:any){
    this.data =  this.data.map((el:any) => {
      if(el.id === item.id){
        return item
      }
      return el
    })
    this.showEditItemDialog = false
  }
  deleteData(item:any){
    this.data = this.data.filter((el:any) => el.id !== item.id)
    if(!this.data.length){
      this.$emit('cancel')
    }
  }
  close() {
    this.dialog = false
    this.$emit('cancel')
  }
  mounted() {
    this.dialog = false
    if(this.dataTable.length){
      this.data = JSON.parse(JSON.stringify(this.dataTable))
    }
    this.$nextTick(() => {
      this.dialog = true
    })
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.IMPORT." + text);
  }
}
</script>

<style scoped></style>

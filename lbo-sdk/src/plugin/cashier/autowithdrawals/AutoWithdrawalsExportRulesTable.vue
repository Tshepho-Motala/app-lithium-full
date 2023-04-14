<template>
  <v-dialog
        max-width="1000"
        data-test-id="dialog-auto-withdrawal-export"
    >
      <template v-slot:activator="{ on, attrs }">
        <v-btn
            color="primary"
            v-bind="attrs"
            v-on="on"
            @click="loadAutoWithdrawals"
            data-test-id="btn-auto-withdrawal-export"
        >{{translate("BUTTONS.LABEL")}}</v-btn>
      </template>
      <template v-slot:default="dialog">
        <v-card>
          <v-toolbar
              color="primary"
              class="mb-4"
              dark
          > {{ translate("TITLE") }}</v-toolbar>
          <v-card-text>
            <v-data-table
                data-test-id="tbl-auto-withdrawal-export"
                v-model="selected"
                :headers="headers"
                :items="data"
                :loading="loading"
                :single-select="singleSelect"
                item-key="id"
                show-select
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
              <template v-slot:[`item.lastUpdated`]="{ item }">
                {{ timestampToDate(item.lastUpdated) }}
              </template>
            </v-data-table>
          </v-card-text>
          <v-card-actions class="justify-end">
            <v-btn
                text
                @click="dialog.value = false"
            >
              {{translate("BUTTONS.CANCEL")}}
            </v-btn>
            <v-btn
                color="primary"
                class="ml-2"
                @click="sendExport"
                :disabled="!selected.length || loading"
            >
              {{translate("BUTTONS.LABEL")}}
            </v-btn>

          </v-card-actions>
        </v-card>
      </template>
    </v-dialog>
</template>

<script lang="ts">
import {Component, Mixins, Inject, Prop} from "vue-property-decorator";
import AssetTabMixin from '@/plugin/cms/mixins/AssetTabMixin';
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import { AutoWithdrawalItem } from '@/core/interface/AutoWithdrawalInterface'
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import AutoWithdrawalRulesetsApiService from "@/core/axios/axios-api/AutoWithdrawalRulesetsApi";
import AutoWithdrawalRulesetsApiInterface from "@/core/interface/axios-api/AutoWithdrawalRulesetsApiInterface";
import {sendExportApiApiParams} from "@/core/interface/api-params/AutoWithdrawalRulesetsApiIParamsInterface";

@Component
export default class AutoWithdrawalsExportRulesTable extends Mixins(AssetTabMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Inject('userService') readonly userService!: UserServiceInterface

  @Prop({ required: false, default: [] }) dataItems!:AutoWithdrawalItem[]
  @Prop({ required: false ,default: false }) isHasData!:boolean

  singleSelect:boolean = false
  selected =  []
  headers = [
    {
      text: 'Name',
      align: 'start',
      sortable: false,
      value: 'name',
    },
    { text: 'Domain', sortable: false, value: 'domain.name' },
    { text: 'Enabled', sortable: false, value: 'enabled' },
    { text: 'Last Updated', sortable: false, value: 'lastUpdated' },
    { text: 'Last Updated By', sortable: false, value: 'lastUpdatedBy' },
  ]
  data:AutoWithdrawalItem[] = []
  loading:boolean = true

  //  transfer the userService in ApiClass
  AutoWithdrawalRulesetsApiService:AutoWithdrawalRulesetsApiInterface = new AutoWithdrawalRulesetsApiService(this.userService)

  async loadAutoWithdrawals() {
    if(this.isHasData){
      this.data = JSON.parse(JSON.stringify(this.dataItems))
      this.loading = false
    } else{
      this.loading = true
      try{
        const result = await this.rootScope.provide.cashierProvider.loadAutoWithdrawalsRulesets()
        if(result?.data?.data.length){
          this.data = result.data.data
        }
        this.loading = false
      } catch (err) {
        this.logService.error("Can't load list " + err)
      }
    }
  }
  downloadJson(content:any, fileName:string, contentType:string) {
    let a = document.createElement("a");
    let file = new Blob([content], {type: contentType});
    a.href = URL.createObjectURL(file);
    a.download = fileName;
    a.click();
  }
  async sendExport() {
    this.loading = true
    const exportList: number[] = []
    if(this.selected.length) {
      this.selected.forEach((el:any) => {
        exportList.push(el.id)
      })
    }
    const data:sendExportApiApiParams = {
      ids: exportList.join(',')
    }
    try {
      const result = await  this.AutoWithdrawalRulesetsApiService.sendExportApi(data)
      if(result?.data){
         this.downloadJson(JSON.stringify(result.data), 'AutoWithdrawalExport.json', 'application/json');
       } else  if(result?.data?.status === 500 || result?.data?.successful === false){
        this.logService.error(result.data.message)
      }
      this.selected = []
      this.loading = false
    } catch(err)  {
      this.loading = false
      this.logService.error(err.message)
    }
  }
  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.EXPORT." + text);
  }
}
</script>

<style scoped></style>

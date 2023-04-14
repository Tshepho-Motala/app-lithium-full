<template>
  <v-dialog
      max-width="700"
      v-model="dialog"
      @click="dialog.value = false"
      data-test-id="dialog-auto-withdrawal-import"
  >
    <template v-slot:activator="{ on, attrs }">
      <v-btn
          color="primary"
          data-test-id="btn-auto-withdrawal-import"
          v-bind="attrs"
          v-on="on"
          @click="dialog = true"
      >{{translate("BUTTONS.LABEL")}}</v-btn>
      <loaded-rules-table @cancel="closeLoadedItemDialog" v-if="showLoadedItemDialog" :data-table="importData"></loaded-rules-table>
    </template>
    <template v-slot:default="dialog">
      <v-card>
        <v-toolbar
            color="primary"
            dark
        >{{ translate("TITLE") }}</v-toolbar>
        <v-card-text>
          <v-file-input
              class="mt-3 mb-3"
              :label="translate('FORM.FILE')"
              v-model="fileInput"
              accept="application/json, json,.json"
          ></v-file-input>
          <file-drop accept=".json" v-on:files-selected="logFiles" />

          <v-alert
              dense
              outlined
              type="error"
              class="mt-3"
              v-if="messageError"
          >
            {{messageError}}
          </v-alert>
        </v-card-text>
        <v-card-actions class="justify-end">
          <v-btn
              text
              @click="dialog.value = false"
          >{{translate("BUTTONS.CANCEL")}}</v-btn>
          <v-btn
              depressed
              color="primary"
              class="ml-2"
              @click="sendImport"
              :disabled="!fileInput"
          >
            {{translate("BUTTONS.LABEL")}}
          </v-btn>
        </v-card-actions>
      </v-card>
    </template>
  </v-dialog>
</template>

<script lang="ts">
import { Component,  Mixins, Inject } from "vue-property-decorator";
import AssetTabMixin from '@/plugin/cms/mixins/AssetTabMixin';
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import FileDrop from '@/plugin/components/FileDrop.vue'
import LogServiceInterface from "@/core/interface/service/LogServiceInterface";
import LoadedRulesTable from '@/plugin/cashier/autowithdrawals/components/LoadedRulesTable.vue'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import { AutoWithdrawalItem } from '@/core/interface/AutoWithdrawalInterface'
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import AutoWithdrawalRulesetsApiService from "@/core/axios/axios-api/AutoWithdrawalRulesetsApi";
import AutoWithdrawalRulesetsApiInterface from "@/core/interface/axios-api/AutoWithdrawalRulesetsApiInterface";

@Component({
  components: {
    FileDrop,
    LoadedRulesTable
  }
})
export default class AutoWithdrawalsImportRules extends Mixins(AssetTabMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Inject('userService') readonly userService!: UserServiceInterface

  fileInput:File | string = ''
  loading:boolean = true
  importData:AutoWithdrawalItem[]  = []
  showLoadedItemDialog:boolean = false
  messageError:string = ''
  dialog:boolean = false

  //  transfer the userService in ApiClass
  AutoWithdrawalRulesetsApiService:AutoWithdrawalRulesetsApiInterface = new AutoWithdrawalRulesetsApiService(this.userService)

  async sendImport() {
    this.loading = true
    this.showLoadedItemDialog = false
    try {
      const result  = await  this.AutoWithdrawalRulesetsApiService.sendImportFile(this.fileInput)
      if(result?.data?.status === 500 || result?.data?.successful === false){
        this.logService.error(result.data.message)
        this.messageError = result.data.message
      } else if(result?.data?.data?.length){
        const data = result.data.data
        data.forEach((el:any, i:number) => {
          el.id = i
          if(el.rules.length){
            el.rules.forEach((rule:any, item:number) => {
              rule.id = item
            })
          }
        })
        this.importData = data
        this.showLoadedItemDialog = true
        this.messageError = ''
        this.dialog = false
      }
      this.loading = false
    } catch (err) {
      this.loading = false
      this.logService.error(err.message)
      this.messageError = err.message
    }
  }
  logFiles(fileList: FileList) {
    this.fileInput = fileList[0]
  }
  closeLoadedItemDialog(){
    this.showLoadedItemDialog = false
  }
  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.IMPORT." + text);
  }
}
</script>

<style scoped></style>

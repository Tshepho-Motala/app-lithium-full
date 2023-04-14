<template>
  <div>
    <v-card class=" pl-6 pr-6 pt-2 pb-5 mb-6" v-if="data">
      <h3>{{translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.EDIT.TITLE')}}</h3>
      <h4>( {{data.id}} - {{ data.name }} )</h4>
      <v-btn
          v-if="data.enabled"
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

    </v-card>
    <div v-if="data" class="d-flex mb-4">

      <v-btn
          style="text-transform: inherit; box-shadow: none;  height: 34px;"
          color="blue-grey lighten-2"
          @click="goToList()"
          dark
      >
        <v-icon
            dark
            left
        >
          mdi-arrow-left
        </v-icon>
        Back
      </v-btn>
      <v-btn
          data-test-id="btn-auto-withdrawal--edit"
          style="text-transform: inherit; box-shadow: none;  height: 34px;"
          color="primary"
          @click="showEditItemDialog = true"
          v-if="hasRoleForDomain('AUTOWITHDRAWALS_RULESETS_EDIT', data.domain.name)"
      >
        <v-icon left>
          mdi-pencil
        </v-icon>
        {{translate("BUTTONS.EDIT")}}
      </v-btn>

      <v-btn
          style="text-transform: inherit; box-shadow: none;  height: 34px;"
          color="error"
          data-test-id="btn-auto-withdrawal--disable"
          @click="openDisabledDialog()"
          v-if="data.enabled && hasRoleForDomain('AUTOWITHDRAWALS_RULESETS_EDIT', data.domain.name)"
      >
        <v-icon left>
          mdi-cancel
        </v-icon>
        {{translate("BUTTONS.DISABLED")}}
      </v-btn>
      <v-btn
          style="text-transform: inherit; box-shadow: none;  height: 34px;"
          color="success"
          data-test-id="btn-auto-withdrawal--enable"
          @click="openEnabledDialog()"
          v-if="!data.enabled && hasRoleForDomain('AUTOWITHDRAWALS_RULESETS_EDIT', data.domain.name)"
      >
        <v-icon left>
          mdi-check
        </v-icon>
        {{translate("BUTTONS.ENABLED")}}
      </v-btn>

      <clone-ruleset v-if="hasRoleForDomain('AUTOWITHDRAWALS_RULESETS_EDIT', data.domain.name)"  :clone-item="data" :is-has-item="true"></clone-ruleset>
      <v-btn
          data-test-id="btn-auto-withdrawal--delete"
          style="text-transform: inherit; box-shadow: none;  height: 34px;"
          color="error"
          @click="onDeleteClick()"
          v-if="hasRoleForDomain('AUTOWITHDRAWALS_RULESETS_DELETE', data.domain.name)"
      >
        <v-icon left>
          mdi-delete
        </v-icon>
        {{translate("BUTTONS.DELETE")}}
      </v-btn>

      <div v-if="data.enabled" class="d-flex align-center justify-center">
        <v-btn
            data-test-id="btn-auto-withdrawal--process"
            style="text-transform: inherit; box-shadow: none;  height: 34px;"
            color="warning"
            @click="openProcessDialog()"
            class="mr-4"

        >
          <v-icon left>
            mdi-cached
          </v-icon>
          {{translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.PROCESS.NAME')}}
        </v-btn>
        <p style="color: #fb8c00" class="ma-0">
          {{translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.PROCESS.DESC')}}</p>
      </div>


    </div>
    <v-card class=" pl-6 pr-6 pt-4 pb-4 mb-4" v-if="data">
      <v-list class="pt-0" dense>
        <v-list-item>
          <v-list-item-content class="font-weight-bold">Name</v-list-item-content>
          <v-list-item-content class="align-end">
            {{ data.name }}
          </v-list-item-content>
        </v-list-item>
        <v-divider class="mt-0 mb-0"></v-divider>
        <v-list-item>
          <v-list-item-content class="font-weight-bold">Domain</v-list-item-content>
          <v-list-item-content class="align-end">
            {{ data.domain.name }}
          </v-list-item-content>
        </v-list-item>
        <v-divider class="mt-0 mb-0"></v-divider>
        <v-list-item>
          <v-list-item-content class="font-weight-bold">Enabled</v-list-item-content>
          <v-list-item-content class="align-end">
            {{ data.enabled }}
          </v-list-item-content>
        </v-list-item>
        <v-divider class="mt-0 mb-0"></v-divider>
        <v-list-item>
          <v-list-item-content class="font-weight-bold">Last Updated</v-list-item-content>
          <v-list-item-content class="align-end">
            {{ timestampToDate(data.lastUpdated) }}
          </v-list-item-content>
        </v-list-item>
        <v-divider class="mt-0 mb-0"></v-divider>
        <v-list-item>
          <v-list-item-content class="font-weight-bold">Last Updated By</v-list-item-content>
          <v-list-item-content class="align-end">
            {{ data.lastUpdatedBy }}
          </v-list-item-content>
        </v-list-item>
        <v-divider class="mt-0 mb-0"></v-divider>
        <v-list-item>
          <v-list-item-content class="font-weight-bold">Delayed start</v-list-item-content>
          <v-list-item-content class="align-end">
            {{ data.delayedStart }}
          </v-list-item-content>
        </v-list-item>
        <v-divider v-if="data.delay" class="mt-0 mb-0"></v-divider>
        <v-list-item v-if="data.delay">
          <v-list-item-content class="font-weight-bold">Delay</v-list-item-content>
          <v-list-item-content class="align-end">
            {{ data.delay }}
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-card>

    <v-card>
      <v-card-text>
        <rule-edit-table :canEdit="hasRoleForDomain('AUTOWITHDRAWALS_RULESETS_EDIT', data.domain.name)"
                         :isUpdateRule="true" @deleteRule="deleteRule" @saveNewRuleItem="saveNewRuleItem"
                         @saveRuleItem="saveRuleItem"
                         v-if="data" :showAddButton="true" :dataItem="data"></rule-edit-table>
      </v-card-text>
    </v-card>
    <template v-if="changeLogs.length">
      <change-log @loadMore="getChangelogs" :has-item="hasMore" :log-list="changeLogs"></change-log>
    </template>
    <v-snackbar
        v-model="snackbar.show"
        :timeout="2000"
        :color="snackbar.color"
    >
      {{ snackbar.text }}

    </v-snackbar>
    <edit-loaded-rules-item
        :isUpdate="true"
        @saveItem="saveItem"
        @cancel="closeEditItemDialog"
        v-if="showEditItemDialog"
        :dataItem="data">
    </edit-loaded-rules-item>

  </div>
</template>

<script lang="ts">
import {Component, Mixins, Inject} from 'vue-property-decorator'
import AssetTabMixin from '@/plugin/cms/mixins/AssetTabMixin'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import {RootScopeInterface} from '@/core/interface/ScopeInterface'
import AutoWithdrawalRulesetsApiService from '@/core/axios/axios-api/AutoWithdrawalRulesetsApi'
import AutoWithdrawalRulesetsApiInterface from '@/core/interface/axios-api/AutoWithdrawalRulesetsApiInterface'
import {
  AutoWithdrawalItem,
  AutoWithdrawalRuleItem,
  AutoWithdrawalRulsetUpdate
} from "@/core/interface/AutoWithdrawalInterface";
import AutoWithdrawalRuleMixin from "@/plugin/cashier/autowithdrawals/mixins/AutoWithdrawalRuleMixin";
import CloneRuleset from '@/plugin/cashier/autowithdrawals/CloneRuleset.vue'
import ChangeLog from "@/plugin/components/change-log/ChangeLog.vue";
import {ConfirmDialogInterface} from "@/plugin/components/dialog/DialogInterface";
import ListenerServiceInterface from "@/core/interface/service/ListenerServiceInterface";
import EditLoadedRulesItem from "@/plugin/cashier/autowithdrawals/components/EditLoadedRulesItem.vue";
import RuleEditTable from "@/plugin/cashier/autowithdrawals/components/RuleEditTable.vue";
import {
  AuthorsListItemInterface,
  ChangeLogItemInterface
} from "@/core/interface/components-interfaces/ChangeLogInterface";


@Component({
  components: {
    CloneRuleset,
    ChangeLog,
    EditLoadedRulesItem,
    RuleEditTable
  }
})
export default class AutoWithdrawalDetailPage extends Mixins(AssetTabMixin, AutoWithdrawalRuleMixin) {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('listenerService') readonly listenerService!: ListenerServiceInterface


  //  transfer the userService in ApiClass
  AutoWithdrawalRulesetsApiService: AutoWithdrawalRulesetsApiInterface = new AutoWithdrawalRulesetsApiService(this.userService)
  data: AutoWithdrawalItem | null = null
  domainName: String = ''
  rulesetId: Number = 0
  changeLogsPage: number = 0
  hasMore = false
  changeLogs: ChangeLogItemInterface[] = []
  authorsList: AuthorsListItemInterface[] = []
  showEditItemDialog: boolean = false

  created() {
    this.rulesetId = this.rootScope.provide.cashierProvider.rulesetID
    this.loadAutoWithdrawals()
    this.getChangelogs()
  }

  async loadAutoWithdrawals() {
    try {
      const result = await this.AutoWithdrawalRulesetsApiService.getAutoWithdrawal(this.rulesetId)
      if (result?.data?.data) {
        this.data = result.data.data
        this.domainName = result.data.data.domain.name
      } else {
        this.data = null
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  async getChangelogs() {
    const params = {
      p: this.changeLogsPage
    }
    try {
      const response = await this.AutoWithdrawalRulesetsApiService.getAutoWithdrawalChangeLog(this.rulesetId, params)
      let authorsListGuid: any = []
      if (response?.data?.data) {
        this.changeLogs = [...this.changeLogs, ...response.data.data.list]
        this.changeLogs.forEach((el: ChangeLogItemInterface) => {
          authorsListGuid.push(el.authorGuid)
        })
        this.hasMore = response.data.data.hasMore
        const authorRes = await this.AutoWithdrawalRulesetsApiService.findUsersByUsernames(this.domainName, authorsListGuid)
        if (authorRes?.data?.data) {
          this.authorsList = [...this.authorsList, ...authorRes.data.data]
          this.changeLogs.forEach((el: ChangeLogItemInterface) => {
            const authorFullName = this.authorsList.find((elem: AuthorsListItemInterface) => elem.guid === el.author)
            if (authorFullName) {
              el.authorFullName = authorFullName.firstName + authorFullName.lastName
            }

          })
        }
        this.changeLogsPage = this.changeLogsPage + 1
      } else {
        this.changeLogs = []
        this.authorsList = []
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  async sendQueueprocess() {
    try {
      const response = await this.AutoWithdrawalRulesetsApiService.sendQueueprocess(this.domainName, this.rulesetId)
      if (response?.data?.data && response.data.successful) {
        this.changeLogsPage = 0
        this.changeLogs = []
        await this.getChangelogs()
        this.snackbar = {
          show: true,
          text: this.translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.PROCESS.SUCCESS'),
          color: 'success'
        }
      } else {
        this.errorSnackBarMessage()
      }
    } catch (err) {
      this.logService.error(err)
      this.errorSnackBarMessage()
    }
  }


  async deleteAutoWithdrawalRuleset() {
    try {
      const response = await this.AutoWithdrawalRulesetsApiService.deleteAutoWithdrawalRuleset(this.domainName, this.rulesetId)
      if (response?.data?.data && response.data.successful) {
        this.goToList()
      } else {
        this.errorSnackBarMessage()
      }
    } catch (err) {
      this.logService.error(err)
      this.errorSnackBarMessage()
    }
  }

  goToList() {
    window.location.href = `/#/dashboard/cashier/autowithdrawals`
  }

  async saveRuleItem(item) {
    try {
      const response = await this.AutoWithdrawalRulesetsApiService.updateRule(this.domainName, this.rulesetId, item.id, item)
      if (response?.data && response.data.successful) {
        if (this.data) {
          this.data.rules = response.data.data.rules
        }
        this.snackbar = {
          show: true,
          text: this.translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.RULE.ADD.SUCCESS'),
          color: 'success'
        }
        if (this.data?.enabled) {
          await this.reloadRule()
        }

      } else {
        this.errorSnackBarMessage()
      }
    } catch (err) {
      this.errorSnackBarMessage()
    }
  }

  async saveNewRuleItem(item: AutoWithdrawalRuleItem) {
    try {
      const response = await this.AutoWithdrawalRulesetsApiService.addRule(this.domainName, this.rulesetId, item)
      if (response?.data && response.data.successful) {
        if (this.data) {
          this.data.rules = response.data.data.rules
        }
        this.snackbar = {
          show: true,
          text: this.translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.RULE.UPDATE.SUCCESS'),
          color: 'success'
        }

        if (this.data?.enabled) {
          await this.reloadRule()
        }
      } else {
        this.errorSnackBarMessage()
      }
    } catch (err) {
      this.errorSnackBarMessage()
    }
  }


  async deleteRule(item) {
    try {
      const response = await this.AutoWithdrawalRulesetsApiService.deleteRule(this.domainName, this.rulesetId, item.id)
      if (response?.data && response.data.successful) {
        if (this.data) {
          this.data.rules = response.data.data.rules
          await this.getChangelogs()
        }
        this.snackbar = {
          show: true,
          text: this.translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.PROCESS.SUSSPROSES'),
          color: 'success'
        }
        if (this.data?.enabled) {
          await this.reloadRule()
        }
      } else {
        this.errorSnackBarMessage()
      }
    } catch (err) {
      this.errorSnackBarMessage()
    }
  }


  async enabledAutoWithdrawalRuleset() {
    try {
      const response = await this.AutoWithdrawalRulesetsApiService.enabledAutoWithdrawalRuleset(this.domainName, this.rulesetId)
      if (response?.data?.data && response.data.successful) {
        this.data = response.data.data
        await this.sendQueueprocess()
        if (response.data.data.enabled) {
          this.snackbar = {
            show: true,
            text: this.translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.EDIT.ENABLE.SUCCESS'),
            color: 'success'
          }
        } else {
          this.snackbar = {
            show: true,
            text: this.translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.EDIT.DISABLE.SUCCESS'),
            color: 'success'
          }
        }
      } else {
        this.errorSnackBarMessage()
      }
    } catch (err) {
      this.logService.error(err)
      this.errorSnackBarMessage()
    }
  }

  openProcessDialog() {
    const params: ConfirmDialogInterface = {
      title: 'Please confirm',
      text: this.translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.PROCESS.CONFIRM'),
      btnPositive: {
        text: 'Confirm',
        color: 'success',
        onClick: async () => {
          await this.sendQueueprocess()
        }
      },
      btnNegative: {
        text: 'Cancel',
        color: 'error',
        flat: true,
        onClick: () => {
        }
      }
    }
    this.listenerService.call('dialog-confirm', params)
  }


  reloadRule() {
    const params: ConfirmDialogInterface = {
      title: 'Please confirm',
      text: this.translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.PROCESS.ONEDIT'),
      btnPositive: {
        text: 'Confirm',
        color: 'success',
        onClick: async () => {
          await this.sendQueueprocess()
        }
      },
      btnNegative: {
        text: 'Cancel',
        color: 'error',
        flat: true,
        onClick: () => {
        }
      }
    }
    this.listenerService.call('dialog-confirm', params)

  }


  openDisabledDialog() {
    const params: ConfirmDialogInterface = {
      title: 'Please confirm',
      text: this.translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.EDIT.DISABLE.CONFIRM'),
      btnPositive: {
        text: 'Confirm',
        color: 'success',
        onClick: async () => {
          await this.enabledAutoWithdrawalRuleset()
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

  openEnabledDialog() {
    const params: ConfirmDialogInterface = {
      title: 'Please confirm',
      text: this.translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.EDIT.ENABLE.CONFIRM'),
      btnPositive: {
        text: 'Confirm',
        color: 'success',
        onClick: async () => {
          await this.enabledAutoWithdrawalRuleset()
          await this.reloadRule()
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

  onDeleteClick() {
    const params: ConfirmDialogInterface = {
      title: 'Please confirm',
      text: 'Are you sure you want to delete this item.',
      btnPositive: {
        text: 'Confirm',
        color: 'success',
        onClick: async () => {
          await this.deleteAutoWithdrawalRuleset()
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

  closeEditItemDialog() {
    this.showEditItemDialog = false
  }

  async saveItem(item: AutoWithdrawalItem) {
    let params: AutoWithdrawalRulsetUpdate = {
      id: item.id,
      name: item.name,
      enabled: item.enabled,
      delay: item.delay,
      delayedStart: item.delayedStart
    }
    try {
      const response = await this.AutoWithdrawalRulesetsApiService.updateRuleset(this.domainName, params)
      if (response?.data && response.data.successful) {
        this.closeEditItemDialog()
        if (this.data) {
          this.data = response.data.data
        }
        this.snackbar = {
          show: true,
          text: this.translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.PROCESS.SUSSPROSES'),
          color: 'success'
        }
      } else {
        this.errorSnackBarMessage()
      }
    } catch (err) {
      this.errorSnackBarMessage()
    }
  }


  errorSnackBarMessage() {
    this.snackbar = {
      show: true,
      text: this.translateAll('UI_NETWORK_ADMIN.AUTOWITHDRAWALS.RULESET.PROCESS.ERROR'),
      color: 'error'
    }
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.IMPORT." + text);
  }

  translateAll(transStr: string) {
    return this.translateService.instant(transStr)
  }
}
</script>

<template>
  <div>
    <v-btn
        data-test-id="btn-auto-withdrawal-clone"
        color="secondary"
        @click="showEditItemDialog = true"
        style="text-transform: inherit; box-shadow: none;  height: 34px;"

    >
      <v-icon
          white
          left
      >
        mdi-content-copy
      </v-icon>
      {{translate('TITLE')}}
    </v-btn>

    <v-dialog
        data-test-id="dialog-auto-withdrawal-clone"
        v-model="dialog"
        persistent
        max-width="290"
        @click:outside="dialog = false"
    >
      <v-card>
        <v-card-title class="text-h5">
          {{translate('WARNING')}}
        </v-card-title>
        <v-card-text> {{translate('TEXT_WARNING')}}</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
              color="error"
              dark
              @click="dialog = false"
          >
            {{translate('CANCEL')}}
          </v-btn>
          <v-btn
              color=" primary"
              dark
              @click="confirmSend()"
          >
            {{translate('CONTINUE')}}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar
        v-model="messageTextOpen"
        :timeout="2000"
        :color="messageStyle"
    >
      {{ messageText }}
    </v-snackbar>

    <edit-loaded-rules-item @saveItem="saveItem" @cancel="closeEditItemDialog" v-if="showEditItemDialog" :isClone="true"
                            :dataItem="editItem"></edit-loaded-rules-item>
  </div>
</template>


<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import {RootScopeInterface} from '@/core/interface/ScopeInterface'
import EditLoadedRulesItem from '@/plugin/cashier/autowithdrawals/components/EditLoadedRulesItem.vue'
import AutoWithdrawalRulesetsApiService from '@/core/axios/axios-api/AutoWithdrawalRulesetsApi'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import {AutoWithdrawalItem} from '@/core/interface/AutoWithdrawalInterface'
import AutoWithdrawalRulesetsApiInterface from "@/core/interface/axios-api/AutoWithdrawalRulesetsApiInterface";
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";

@Component({
  components: {
    EditLoadedRulesItem
  }
})
export default class ExportRulesTable extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Inject('userService') readonly userService!: UserServiceInterface
  @Prop({default: false}) isHasItem!: boolean
  @Prop() cloneItem!: AutoWithdrawalItem

  editItem: AutoWithdrawalItem | null = null
  showEditItemDialog: boolean = false
  messageText: string = ''
  messageTextOpen: boolean = false
  messageStyle: string = 'success'
  dialog: boolean = false
  sendItem: AutoWithdrawalItem | null = null
  AutoWithdrawalRulesetsApiService: AutoWithdrawalRulesetsApiInterface = new AutoWithdrawalRulesetsApiService(this.userService)

  mounted() {
    this.loadTypes()
  }

  async loadTypes() {
    if (this.isHasItem && this.cloneItem) {
      this.editItem = JSON.parse(JSON.stringify(this.cloneItem))
    } else {
      const data = await this.rootScope.provide.cashierProvider.getRuleset()
      this.editItem = JSON.parse(JSON.stringify(data))
    }
  }

  closeEditItemDialog() {
    this.showEditItemDialog = false
  }

  async saveItem(item: AutoWithdrawalItem) {
    const data: AutoWithdrawalItem = JSON.parse(JSON.stringify(item))
    this.sendItem = data
    if (this.editItem?.domain.name === this.sendItem?.domain.name) {
      this.dialog = true
    } else {
      await this.send()
    }
  }

  async confirmSend() {
    if (this.sendItem?.name) {
      this.sendItem.name = this.sendItem.name + '__clone'
    }
    await this.send()
    this.dialog = false
  }

  async send() {
    const data: AutoWithdrawalItem | null = this.sendItem
    if (data) {
      const domainName = data.domain.name
      data.id = null
      if (data?.domain) {
        data.domain.id = null
        data.domain.version = null
      }
      if (data?.rules) {
        data.rules.map((el: any) => {
          el.id = null
          el.version = null
          if (el.value && Array.isArray(el.value)) {
            if (typeof el.value[0] === 'object') {
              const listValue: string[] = []
              el.value.forEach((el: any) => listValue.push(el.value))
              el.value = listValue.join(',')
            }
          }
        })
      }
      try {
        const res = await this.AutoWithdrawalRulesetsApiService.createRuleset(domainName, data)
        if(res?.data?.successful) {
          this.showEditItemDialog = false
          this.messageText = this.translate('SUCCESSFULLY')
          this.messageTextOpen = true
          this.messageStyle = 'success'
        } else {
          this.messageText = res.data.message
          this.messageTextOpen = true
          this.messageStyle = 'error'
        }
      } catch (e) {
        this.messageText = e.message
        this.messageTextOpen = true
        this.messageStyle = 'error'
      }
    }

  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.SERVICE_CASHIER.AUTO_WITHDRAWALS.CLONE." + text);
  }
}
</script>

<style scoped></style>

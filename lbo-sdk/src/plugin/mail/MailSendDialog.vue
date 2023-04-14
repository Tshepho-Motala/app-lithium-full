<template>
  <div style="width: 100%" class="v-application v-application--is-ltr theme--light">
    <div style="width: 100%">
      <div class="mt-6">
        <v-select
            :items="templateList"
            item-text="name"
            item-value="id"
            :label="translateAll('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.SEND_TEMPLATE.AVAILABLE_TEMPLATES_LABEL')"
            outlined
            v-model="template"

        ></v-select>
      </div>

      <preview-mail-template v-if="showDialog" @send="sendEmail" :user="user" :preview="edited" :template-id="template"
                             @cancel="closeDialog"></preview-mail-template>

      <div class="d-flex justify-end mt-4 mb-5">

        <v-btn
            dark
            color="secondary"
            v-if="template"
            @click="previewMail"
        >
          <v-icon left>mdi-television</v-icon>
          Preview
        </v-btn>

        <v-btn
            dark
            color="primary"
            v-if="template"
            @click="editMail(template)"
        >
          <v-icon left>mdi-pencil</v-icon>
          Edit
        </v-btn>

        <v-btn
            dark
            color="success"
            @click="sendEmail"
            v-if="template"
        >
          <v-icon left>mdi-content-save</v-icon>
          Send
        </v-btn>
      </div>
    </div>

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
import {Component, Inject, Mixins, Vue} from 'vue-property-decorator'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import MailApiInterface from "@/core/interface/axios-api/MailApiInterface";
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import MailApi from "@/core/axios/axios-api/MailApi";
import AssetTabMixin from "@/plugin/cms/mixins/AssetTabMixin";
import PreviewMailTemplate from "@/plugin/mail/PreviewMailTemplate.vue";
import {CashierConfigUser} from "@/core/interface/cashierConfig/CashierConfigInterface";
import {TemplateListItemInterface} from "@/core/interface/mail-page/MailPageInterface";

@Component({
  components: {
    PreviewMailTemplate
  }
})
export default class MailSendDialog extends Mixins(AssetTabMixin) {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('userService')
  readonly userService!: UserServiceInterface;


  MailApiService: MailApiInterface = new MailApi(this.userService)
  user: CashierConfigUser | null = null
  templateList: TemplateListItemInterface[] = []
  template: number | null = null
  edited: boolean = false
  snackbar: any = {
    show: false,
    text: '',
    color: 'success',
  }
  showDialog: boolean = false

  async mounted() {
    this.user = this.rootScope.provide.quickActionProvider.user
    if (this.user) {
      await this.loadMailTemplatesList(this.user.domain.name)
    }
  }

  async loadMailTemplatesList(domainName) {
    if (domainName) {
      try {
        const response = await this.MailApiService.loadMailTemplates(domainName)
        if (response?.data?.data && response.data.successful) {
          this.templateList = response.data.data
        }
      } catch (e) {
        this.snackbar = {
          show: true,
          text: this.translateAll(e.message),
          color: 'error'
        }
      }
    }

  }


  async sendEmail() {
    const params: any = {}
    if (this.user) {
      params.recipientEmail = this.user.email
      params.recipientGuid = this.user.guid
      params.recipientId = this.user.id

    }
    if (this.template !== null) {
      try {
        const response = await this.MailApiService.sendUserTemplateMail(this.template, params)
        if (response?.data?.data && response.data.successful) {
          this.snackbar = {
            show: true,
            text: 'Email sent successfully',
            color: 'success'
          }
        } else {
          this.snackbar = {
            show: true,
            text: this.translateAll(response.data.message),
            color: 'error'
          }
        }
      } catch (e) {
        this.snackbar = {
          show: true,
          text: this.translateAll(e.message),
          color: 'error'
        }
      }
    }

  }

  editMail() {
    this.edited = true
    this.showDialog = true
  }

  previewMail() {
    this.edited = false
    this.showDialog = true
  }

  closeDialog() {
    this.showDialog = false
  }

  translateAll(transStr: string) {
    return this.translateService.instant(transStr)
  }
}
</script>

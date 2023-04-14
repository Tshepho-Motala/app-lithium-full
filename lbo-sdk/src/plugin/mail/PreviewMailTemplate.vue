<template>
  <div>
    <v-dialog
        data-test-id="dialog-auto-withdrawal-rule-edit"
        v-model="dialog"
        max-width="800px"
        @click:outside="close"
    >
      <v-card>
        <v-toolbar
            v-if="templateDetail"
            color="primary"
            dark
            style="font-size: 22px"
        >
          {{ !edited ? 'Preview' : "Editing" }} "{{ templateDetail.name }}" template
        </v-toolbar>
        <v-card-text class="mt-5">
          <div v-if="templateDetail">
            <v-text-field
                class="mt-3"
                v-model="templateDetail.current.subject"
                label="Email Subject *"
                type="text"
                outlined
                persistent-hint
                :disabled="!edited"
                hint="The subject of the email. Keep it short and to the point, and stay away from all capitals or special characters."
                required

            ></v-text-field>
            <v-text-field
                class="mt-3"
                outlined
                v-model="templateDetail.current.emailFrom"
                label="From Email Address"
                type="text"
                persistent-hint
                :disabled="!edited"
                hint="From Email Address"
                required
            ></v-text-field>
            <ckeditor class="mt-3" :config="editorConfig" :read-only="!edited" v-model="body"></ckeditor>
            <label>The html body of the email. Feel free to make use of the template fields to enhance the personality
              of the email. Avoid using images to present text.</label>
          </div>
          <v-snackbar
              v-model="snackbar.show"
              :timeout="2000"
              :color="snackbar.color"
          >
            {{ snackbar.text }}

          </v-snackbar>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
              dark
              color="error"
              @click="close"

          >
            <v-icon left>mdi-cancel</v-icon>
            Cancel
          </v-btn>

          <v-btn
              dark
              color="success"
              @click="send"
          >
            <v-icon left>mdi-content-save</v-icon>
            Send
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
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import MailApiInterface from "@/core/interface/axios-api/MailApiInterface";
import MailApi from "@/core/axios/axios-api/MailApi";
import CKEditor from 'ckeditor4-vue';
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import {CashierConfigUser} from "@/core/interface/cashierConfig/CashierConfigInterface";
import {
  TemplateListItemInterface,
  TemplateListPlaceholderInterface
} from "@/core/interface/mail-page/MailPageInterface";

@Component({
  components: {
    ckeditor: CKEditor.component
  }
})
export default class PreviewMailTemplate extends Mixins(AssetTabMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({required: true}) templateId!: number
  @Prop({required: true}) user!: CashierConfigUser
  @Prop({required: false, default: false}) preview!: boolean

  @Inject('userService')
  readonly userService!: UserServiceInterface;


  MailApiService: MailApiInterface = new MailApi(this.userService)
  dialog: boolean = false
  edited: boolean = false
  templateDetail: TemplateListItemInterface | null = null
  editorConfig = {
    height: '300',
    editorName: 'mailTemplate',
    allowedContent: true,
    entities: false,
    fullPage: true,
    language: 'en',
  }
  body: string = ''
  placeholders: TemplateListPlaceholderInterface[] = []
  snackbar: any = {
    show: false,
    text: '',
    color: 'success',
  }

  async mounted() {
    this.edited = this.preview
    if (this.templateId) {
      await this.loadMailTemplate(this.templateId)
      await this.loadMailPlaceholder(this.templateId)
    }
    this.$nextTick(() => {
      this.dialog = true
    })
  }


  async loadMailTemplate(id: number) {
    if (id) {
      try {
        const response = await this.MailApiService.loadMailTemplate(id)
        if (response?.data?.data && response.data.successful) {
          this.templateDetail = response.data.data
          this.body = response.data.data.current.body
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

  async loadMailPlaceholder(id: number) {
    const params: any = {
      recipientGuid: this.user.guid
    }
    if (id && this.templateDetail) {
      try {
        const response = await this.MailApiService.loadMailPlaceholder(id, params)
        if (response?.data?.data && response.data.successful) {
          this.placeholders = response.data.data
          let stringHtml = this.templateDetail.current.body
          if (this.placeholders.length) {
            this.placeholders.forEach((el: any) => {
              stringHtml = this.replaceAll(stringHtml, el.key, el.value)
            })
          }
          this.body = stringHtml
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

  replaceAll(str, find, replace) {
    return str.replace(new RegExp((find), 'g'), replace);
  }

  close() {
    this.$emit('cancel')
  }

  async send() {
    if(this.templateDetail) {
      if (!this.edited) {
        this.$emit('send')
        this.$emit('cancel')
      } else {
        const params = {
          domainName: this.templateDetail.domain.name,
          subject: this.templateDetail.current.subject,
          body: this.body,
          to: this.user.email,
          userGuid: this.user.guid,
        }
        try {
          const response = await this.MailApiService.sendUserMail(this.templateId, this.user.id, params)
          if (response?.data?.data && response.data.successful) {
            this.snackbar = {
              show: true,
              text: 'Email sent successfully',
              color: 'success'
            }
            setTimeout(() => this.close(), 1000)
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

  }

  translateAll(text: string): any {
    return this.translateService.instant(text)
  }
}
</script>

<template>
  <v-form v-model="valid">
    <v-col cols="12">
      <v-select
        v-model="docEdit.typeId"
        :items="availableTypes"
        label="Document Type"
        hint="Select the appropriate document type corresponding to the file you are uploading"
        required
        persistent-hint
        :rules="notEmptyRule"
        @change="onTypeSelected"
      ></v-select>
    </v-col>
    <v-col cols="12">
      <v-radio-group
        v-model="docEdit.reviewStatus"
        label="Document Status"
        hint="Select the appropriate document status after it has been reviewed"
        persistent-hint
        required
        :rules="notEmptyRule"
      >
        <v-radio label="Waiting" value="WAITING"></v-radio>
        <v-radio label="Valid" value="VALID"></v-radio>
        <v-radio label="Invalid" value="INVALID"></v-radio>
        <v-radio v-if="docEdit.reviewStatus === 'HISTORIC'" disabled label="Historic" value="HISTORIC"></v-radio>
      </v-radio-group>
    </v-col>
    <v-col cols="12">
      <v-select
        v-model="docEdit.reviewReason"
        :items="availableReasons"
        label="No Review reason"
        hint="Select a reason for the review outcomes"
        persistent-hint
        clearable
      ></v-select>
    </v-col>
    <v-col cols="12">
      <v-switch small label="Sensitive File" v-model="docEdit.sensitive" :disabled="sensitiveDisabled"></v-switch>
    </v-col>
    <v-col cols="12" v-if="this.isNew">
      <div class="text-center">
        <v-file-input v-model="file" :rules="notEmptyRule"></v-file-input>
      </div>
      <file-drop v-on:files-selected="logFiles" accept=".jpg,.jpeg,.png,.pdf," />
    </v-col>
    <v-row>
      <v-spacer></v-spacer>
      <v-btn color="blue darken-1" text @click="onCancel" :loading="saving"> Cancel </v-btn>
      <v-btn color="blue darken-1" text @click="onSave" :loading="saving" :disabled="!valid"> Save </v-btn>
    </v-row>
  </v-form>
</template>

<script lang="ts">
import { Component, Prop, Inject, Vue } from 'vue-property-decorator'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import FileDrop from '../components/FileDrop.vue'
import '@/core/directive/role-check/RoleCheckDirective'
import LogServiceInterface from '@/core/interface/service/LogServiceInterface'
import UserServiceInterface from '@/core/interface/service/UserServiceInterface'
import { DocumentTypeInterface } from '@/mock/provider/DocumentGenerationMock'

@Component({
  components: {
    FileDrop
  }
})
export default class DocumentUpdate extends Vue {
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('userService') readonly userService!: UserServiceInterface
  @Prop({ required: true }) docEdit: any
  @Prop({ required: true }) isNew!: boolean

  mounted() {
    this.loadEnabledTypes(this.isNew)
    this.loadReviewReasons()
    this.domain = this.rootScope.provide.documentGeneration.data.domain
  }

  // get sensitiveDisabled(): boolean {
  //   return !this.userService.hasRoleForDomain(this.domain, 'DOCUMENT_SENSITIVE_EDIT')
  // }

  sensitiveDisabled = false

  logFiles(fileList: FileList) {
    this.file = fileList[0]
  }

  domain: string = ''

  availableTypes: DocumentTypeInterface[] = []
  availableReasons: Array<any> = []

  valid = false
  saving = false
  file: any = null

  notEmptyRule = [(v: any) => !!v || 'Field is required']

  onCancel() {
    this.file = null
    this.$emit('cancel', this.isNew)
  }

  onSave() {
    this.saving = true
    if (this.isNew) {
      this.rootScope.provide.documentGeneration
        .uploadDocument(this.file, this.docEdit)
        .then((result: any) => {
          this.saving = false
          this.file = null
          this.$emit('save', result.data, this.isNew)
        })
        .catch((err: any) => {
          this.saving = false
          this.logService.error("Can't save document due " + err)
        })
    } else {
      this.rootScope.provide.documentGeneration
        .updateDocument(this.docEdit)
        .then(() => {
          this.saving = false
          this.$emit('save', this.docEdit, this.isNew)
        })
        .catch((err: any) => {
          this.saving = false
          this.logService.error("Can't save document due " + err)
        })
    }
  }

  async loadEnabledTypes(internalOnly: boolean) {
    this.availableTypes = await this.rootScope.provide.documentGeneration.loadAvailableDocumentTypes(internalOnly)
  }

  async loadReviewReasons() {
    this.rootScope.provide.documentGeneration.loadAvailableReviewReasons().then((result: any) => {
      this.availableReasons = result
    })
  }

  onTypeSelected(i: number) {
    const item = this.availableTypes.find(x => x.value === i)
    if(!item) {
      // What if we dont?
      //return

      this.sensitiveDisabled = false
      this.docEdit.sensitive = false
    }else {
      this.sensitiveDisabled = item.typeSensitive
      this.docEdit.sensitive = item.typeSensitive
    }
  }
}
</script>

<style scoped>
.lithiumPrimary {
  background-color: #3c8dbc;
}
</style>

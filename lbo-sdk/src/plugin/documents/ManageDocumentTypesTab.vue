<template>
  <v-container>
    <v-row>
      <v-col align-self="start">
        <v-btn v-role="'DOCUMENT_TYPES_EDIT'" :domain="domain" @click="onButtonAdd()">
          <v-icon left> mdi-plus-thick </v-icon>
          Add
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-data-table :headers="headers" :items="types" :items-per-page="10" class="elevation-1">
          <template v-slot:[`item.enabled`]="{ item }">
            <v-switch small v-model="item.enabled" @click="onSwitch(item)"></v-switch>
          </template>
          <template v-slot:[`item.typeSensitive`]="{ item }">
            <v-switch small v-model="item.typeSensitive" @click="onSwitch(item)"></v-switch>
          </template>
          <template v-slot:[`item.image`]="{ item }">
            <img v-if="item.iconBase64" style="width: 30px; height: 30px;  object-fit: contain;"
            :src="item.iconBase64"
            alt="">
          </template>
          <template v-slot:[`item.id`]="{ item }">
            <v-btn
              tile
              small
              color="primary"
              @click="onButtonEdit(item.id)"
              lit-if-permission="DOCUMENT_TYPES_EDIT"
              :lit-permission-domain="rootScope.domainName"
            >
              <v-icon left> mdi-pencil </v-icon>
              Edit
            </v-btn>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
    <v-dialog v-model="showDialog" max-width="500" persistent>
      <v-card>
        <v-card-title>
          <span class="text-h5">Edit Document Type</span>
        </v-card-title>
        <v-card-text>
          <DocumentTypeUpdate @save="onSave" @cancel="onCancel" :docTypeEdit="editItem" />
        </v-card-text>
      </v-card>
    </v-dialog>
    <v-snackbar v-model="snackbarInfo" color="green" :timeout="2000">
      {{ infoText }}
      <template v-slot:action="{ attrs }">
        <v-btn color="white" text v-bind="attrs" @click="snackbarInfo = false"> Close </v-btn>
      </template>
    </v-snackbar>
    <v-snackbar v-model="snackbarError" color="red" :timeout="2000">
      {{ errorText }}
      <template v-slot:action="{ attrs }">
        <v-btn color="white" text v-bind="attrs" @click="snackbarError = false"> Close </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script lang="ts">
import { Component, Inject, Vue } from 'vue-property-decorator'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import DocumentTypeUpdate from './DocumentTypeUpdate.vue'
import '@/core/directive/role-check/RoleCheckDirective'
import LogServiceInterface from '@/core/interface/service/LogServiceInterface'
import UserServiceInterface from '@/core/interface/service/UserServiceInterface'

@Component({
  components: { DocumentTypeUpdate }
})
export default class ManageDocumentTypesTab extends Vue {
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('userService') readonly userService!: UserServiceInterface

  mounted() {
    this.loadTypes()
    this.domain = this.rootScope.provide.documentGeneration.data.domain
    if (this.userService.hasRoleForDomain(this.domain, 'DOCUMENT_TYPES_EDIT')){
      this.headers.push({ text: 'Action', sortable: false, value: 'id' })
      this.disabledEdit = false
    } else {
      this.disabledEdit = true
    }
  }

  types: Array<any> = []
  domain: string = ''
  headers = [
    {
      text: 'Purpose',
      align: 'start',
      sortable: true,
      value: 'purpose'
    },
    { text: 'Document Type', sortable: true, value: 'type' },
    { text: 'Enabled', sortable: true, value: 'enabled' },
    { text: 'Sensitive', sortable: true, value: 'typeSensitive' },
    { text: 'Preview', sortable: false, value: 'image' }
  ]

  disabledEdit!: boolean
  showDialog = false
  editItem = { enabled: true, iconBase64: '',typeSensitive: false }

  snackbarInfo = false
  snackbarError = false
  infoText = ''
  errorText = ''

  onSwitch(item: any) {
    const itemObj = JSON.parse(JSON.stringify(item));
    this.saveDocType(itemObj)
  }

  onButtonEdit(itemId: any) {
    this.editItem = { ...this.types.find((element) => element.id === itemId) }
    this.showDialog = true
  }

  onButtonAdd() {
    this.editItem = { enabled: true, iconBase64: '',typeSensitive: false }
    this.showDialog = true
  }

  onCancel() {
    this.showDialog = false
  }

  onSave(docTypeSaved: any) {
    this.showDialog = false    
    this.addOrReplce(docTypeSaved)
  }

  addOrReplce(docType: any) {
    const index = this.types.findIndex((e) => e.id === docType.id)
    if (index >= 0) {
      this.types.splice(index, 1, docType)
      this.showInfo('Document type updated')
    } else {
      this.types.push(docType)
      this.showInfo('Document type added')
    }
  }

  showInfo(text: string) {
    this.infoText = text
    this.snackbarInfo = true
  }

  showError(text: string) {
    this.errorText = text
    this.snackbarError = true
  }

  async saveDocType(docType: any) {
    this.rootScope.provide.documentGeneration
      .saveDocumentType(docType)
      .then((result: any) => {
        this.showInfo('Document type updated')
        this.$emit('save', result)
      })
      .catch((err: any) => {
        this.showError("Can't save document type due " + err)
        this.logService.warn("Can't save document type due " + err)
      })
  }

  async loadTypes() {
    this.logService.log('Loading types...')
    this.rootScope.provide.documentGeneration.loadDocumentTypes().then((result: any) => {
      this.types = result    
    })
  }
}
</script>

<style scoped>
.lithiumPrimary {
  background-color: #3c8dbc;
}
</style>
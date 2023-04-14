<template>
  <v-container class="grey lighten-4" fluid>
    <v-row>
      <v-col align-self="start">
        <v-btn v-role="['DOCUMENT_SENSITIVE_EDIT', 'DOCUMENT_REGULAR_EDIT']" :domain="domain" @click="onUpload">
          <v-icon left> mdi-upload </v-icon>
          Upload document
        </v-btn>
      </v-col>
    </v-row>

    <v-row>
      <v-col v-for="doc in docs" :key="doc.documentFileId" cols="auto">
        <v-hover v-slot="{ hover }">
          <v-card max-width="200px" :elevation="hover ? 12 : 2" :class="{ 'on-hover': hover }">
            <v-img class="center" height="200px" width="200px" :src="'services/service-document/public/files/typeIcon/' + doc.typeId">
              <v-container>
                <v-row class="fill-height flex-column" justify="space-between">
                  <v-col class="text-h3 heading text-left">
                    <v-chip :style="{ visibility: doc.sensitive ? 'visible' : 'hidden' }" color="red" text-color="white" label>
                      <v-icon left> mdi-lock-alert-outline </v-icon> Sensitive
                    </v-chip>
                  </v-col>
                  <v-spacer />
                </v-row>
                <v-row class="text-center">
                  <v-col>
                    <v-btn
                      x-small
                      fab
                      v-role="'DOCUMENT_DELETE'"
                      :domain="domain"
                      @click="onDelete(doc.id)"
                      :style="{ visibility: hover ? 'visible' : 'hidden' }"
                      color="red"
                    >
                      <v-icon :style="{ visibility: hover ? 'visible' : 'hidden' }">mdi-delete-variant</v-icon>
                    </v-btn>
                  </v-col>
                  <v-col>
                    <v-btn
                      x-small
                      fab
                      v-role="'DOCUMENT_DOWNLOAD'"
                      :domain="domain"
                      @click="onPreview(doc.documentFileId)"
                      :style="{ visibility: hover ? 'visible' : 'hidden' }"
                      color="green"
                    >
                      <v-icon :style="{ visibility: hover ? 'visible' : 'hidden' }">mdi-download</v-icon>
                    </v-btn>
                  </v-col>
                  <v-col>
                    <v-btn
                      x-small
                      fab
                      v-role="['DOCUMENT_SENSITIVE_EDIT', !doc.sensitive ? 'DOCUMENT_REGULAR_EDIT' : '']"
                      :domain="domain"
                      @click="onEdit(doc.id)"
                      :style="{ visibility: hover ? 'visible' : 'hidden' }"
                      color="blue"
                    >
                      <v-icon :style="{ visibility: hover ? 'visible' : 'hidden' }">mdi-pencil-outline</v-icon>
                    </v-btn>
                  </v-col>
                </v-row>

                <v-row>
                  <v-spacer /><v-spacer />
                  <v-col>
                    <v-chip
                      v-if="doc.reviewStatus !== undefined && doc.reviewStatus.toUpperCase() == 'VALID'"
                      text-color="white"
                      color="green"
                      label
                      >{{ doc.reviewStatus }}</v-chip
                    >
                    <v-chip
                      v-else-if="doc.reviewStatus !== undefined && doc.reviewStatus.toUpperCase() == 'INVALID'"
                      text-color="white"
                      color="red"
                      label
                      >{{ doc.reviewStatus }}</v-chip
                    >
                    <v-chip v-else text-color="black" color="white" label>{{ doc.reviewStatus }}</v-chip>
                  </v-col>
                </v-row>
              </v-container>
            </v-img>

            <v-card-subtitle class="pb-0">
              <template v-if="hover">{{ doc.fileName }}</template>
              <template v-else>{{ trucateText(doc.fileName) }}</template>
              <tr />
              {{ formatDate(doc.uploadDate) }}
            </v-card-subtitle>
          </v-card>
        </v-hover>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-dialog v-model="showUploadDialog" max-width="500" persistent>
        <v-card>
          <v-card-title>
            <span class="text-h5">Upload Document</span>
          </v-card-title>
          <v-card-text>
            <DocumentUpdate @save="onSave" @cancel="onCancel" :docEdit="editItem" :isNew="true" />
          </v-card-text>
        </v-card>
      </v-dialog>
    </v-row>
    <v-row justify="center">
      <v-dialog v-model="showEditDialog" max-width="500" persistent>
        <v-card>
          <v-card-title>
            <span class="text-h5">Edit Document</span>
          </v-card-title>
          <v-card-text>
            <DocumentUpdate @save="onSave" @cancel="onCancel" :docEdit="editItem" :isNew="false" />
          </v-card-text>
        </v-card>
      </v-dialog>
    </v-row>

    <v-row justify="center">
      <v-dialog v-model="showPreviewDialog" max-height="600px" max-width="600px" transition="dialog-bottom-transition" persistent>
        <v-card>
          <div
            v-if="
              previewDoc !== null &&
              previewDoc.mimeType !== undefined &&
              previewDoc.mimeType.startsWith('image') &&
              previewDoc.base64 !== undefined &&
              previewDoc.base64.length > 0
            "
          >
            <v-img @contextmenu.prevent :src="previewDoc.base64" contain />
          </div>
          <div v-else-if="previewDoc !== null && previewDoc.mimeType !== undefined && previewDoc.mimeType.length > 0">
            <v-card-title class="text-h5"> No previev </v-card-title>
            <v-card-text
              >Preview for ( {{ previewDoc.mimeType }} ) file type is not supported. Let save a file and try open with external viewer</v-card-text
            >
          </div>
          <div v-else>
            <v-card-title class="text-h5"> No file </v-card-title>
            <v-card-text>No uploaded file for preview</v-card-text>
          </div>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="green darken-1" text @click="showPreviewDialog = false"> Close </v-btn>
            <v-btn
              color="green darken-1"
              text
              @click="downloadFile(previewDoc)"
              :disabled="previewDoc === null || previewDoc.base64 === undefined || previewDoc.base64.length < 1"
            >
              Save
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-row>

    <v-row justify="center">
      <v-dialog v-model="showDeleteDialog" persistent max-width="290">
        <v-card>
          <v-card-title class="text-h5"> Delete document </v-card-title>
          <v-card-text>Are you sure you want to delete this document?</v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="green darken-1" text @click="showDeleteDialog = false" :loading="deleting"> Cancel </v-btn>
            <v-btn color="green darken-1" text @click="onDeleteApprove" :loading="deleting"> Delete </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-row>
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
import DocumentUpdate from './DocumentUpdate.vue'
import { format } from 'date-fns'
import '@/core/directive/role-check/RoleCheckDirective'
import LogServiceInterface from '@/core/interface/service/LogServiceInterface'
import UserServiceInterface from '@/core/interface/service/UserServiceInterface'
import ListenerServiceInterface from '@/core/interface/service/ListenerServiceInterface'

@Component({
  components: { DocumentUpdate }
})
export default class DocumentsTab extends Vue {
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('userService') readonly userService!: UserServiceInterface
  @Inject('listenerService') readonly listenerService!: ListenerServiceInterface

  mounted() {
    this.domain = this.rootScope.provide.documentGeneration.data.domain
    this.loadDocuments()
    this.listenerService.subscribe('refresh-documents-list', () => {
      this.loadDocuments()
    })
    let documentFileId = this.getUrlParameter('documentFileId');
    if (documentFileId && documentFileId >= 0){
      this.onPreview(documentFileId)
    }
  }

  docs: Array<any> = []
  domain: string = ''
  showUploadDialog = false
  showEditDialog = false
  showDeleteDialog = false
  showPreviewDialog = false
  editItem = {}
  deleteDocId: string = ''
  deleting = false

  snackbarInfo = false
  snackbarError = false
  infoText = ''
  errorText = ''

  previewDoc: any = null

  getUrlParameter(name){
    let results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results==null) {
      return null;
    }
    return decodeURI(results[1]) || 0;
  }

  formatDate(millis: number): string {
    if (millis === undefined || millis === null) {
      return 'no-date'
    }
    return format(millis, 'dd MMMM yyyy, HH:mm:ss')
  }

  trucateText(value: string, length = 20) {
    if (value === undefined || value === null) {
      return 'no-file'
    }
    return value.length <= length ? value : value.substring(0, length) + '...'
  }

  async loadDocuments() {
    this.rootScope.provide.documentGeneration.loadDocuments().then((result: any) => {
      this.docs = result
    })
  }

  onDelete(docId: string) {
    this.deleteDocId = docId
    this.showDeleteDialog = true
  }

  onDeleteApprove() {
    this.deleting = true
    this.rootScope.provide.documentGeneration
      .deleteDocument(this.deleteDocId)
      .then(() => {
        let index = this.docs.findIndex((doc) => doc.id === this.deleteDocId)
        if (index > -1) {
          this.docs.splice(index, 1)
          this.logService.log('Deleted document #' + this.deleteDocId)
        }
        this.deleting = false
        this.deleteDocId = ''
        this.showDeleteDialog = false
      })
      .catch((err: any) => {
        this.deleting = false
        this.logService.warn("Can't delete document due " + err)
        this.deleteDocId = ''
        this.showDeleteDialog = false
      })
  }

  onPreview(docFileId: any) {
    this.rootScope.provide.documentGeneration
      .loadDocumentFile(docFileId)
      .then((result) => {
        this.previewDoc = result.file
        this.showPreviewDialog = true
      })
      .catch((err: any) => {
        this.logService.warn("Can't retrieve document due " + err)
      })
  }

  downloadFile(doc: any) {
    const linkSource = doc.base64
    const downloadLink = document.createElement('a')
    const fileName = doc.name

    downloadLink.href = linkSource
    downloadLink.download = fileName
    downloadLink.click()
    this.showPreviewDialog = false
  }

  onEdit(docId: string) {
    this.editItem = { ...this.docs.find((element) => element.id === docId) }
    this.showEditDialog = true
  }

  onUpload() {
    this.editItem = {}
    this.showUploadDialog = true
  }

  onCancel(isNew: boolean) {
    if (isNew) {
      this.showUploadDialog = false
    } else {
      this.showEditDialog = false
    }
  }

  onSave(docSaved: any, isNew: boolean) {
    if (isNew) {
      this.showUploadDialog = false
    } else {
      this.showEditDialog = false
    }
    this.logService.log('Saving: ', docSaved)
    this.addOrReplace(docSaved)
  }

  addOrReplace(docSaved: any) {
    const index = this.docs.findIndex((e) => e.id === docSaved.id)
    if (index >= 0) {
      this.docs.splice(index, 1, docSaved)
      this.showInfo('Document updated')
    } else {
      this.docs.push(docSaved)
      this.showInfo('Document added')
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
}
</script>

<style scoped>
.lithiumPrimary {
  background-color: #3c8dbc;
}
.v-card {
  transition: opacity 0.4s ease-in-out;
}

.v-card:not(.on-hover) {
  opacity: 0.6;
}

.v-dialog .v-card {
  opacity: 1;
}
</style>
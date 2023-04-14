<template>
  <v-container>
    <DocumentUpdate @save="onSave" @cancel="onCancel" :docEdit="editItem" :isNew="true" />
  </v-container>
</template>

<script lang="ts">
import { Component, Inject, Vue } from 'vue-property-decorator'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import DocumentUpdate from './DocumentUpdate.vue'

@Component({
  components: { DocumentUpdate }
})
export default class DocumentUploadQuickAction extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface

  editItem = {}

  async onCancel(isNew: boolean) {
    this.rootScope.provide.documentGeneration.closeUploadDialog()
  }

  onSave(docSaved: any, isNew: boolean) {
    
  }
}
</script>

<style scoped>
</style>
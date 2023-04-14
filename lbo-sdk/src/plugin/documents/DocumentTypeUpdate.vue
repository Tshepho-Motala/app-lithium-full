<template>
  <v-form v-model="valid">
    <v-col cols="12">
      <v-text-field
        v-model="docTypeEdit.type"
        label="Document Type*"
        hint="Name of the document type"
        required
        :rules="notEmptyRule"
        persistent-hint
      ></v-text-field>
    </v-col>
    <v-col cols="12">
      <v-select
        v-model="docTypeEdit.purpose"
        :items="[
          { text: 'Internal LBO use', value: 'Internal' },
          { text: 'External client', value: 'External' }
        ]"
        label="Purpose*"
        hint="Select purpose"
        persistent-hint
        required
        :rules="notEmptyRule"
      ></v-select>
    </v-col>


    <v-col cols="12">
      <v-combobox
          v-model="docTypeEdit.mappingNames"
          :items=" docTypeEdit.mappingNames"
          hide-selected
          hint=" Press 'enter' to create a new name"
          label="Mapping names"
          multiple
          persistent-hint
          small-chips
      >
        <template v-slot:no-data>
          <v-list-item>
            <v-list-item-content>
              <v-list-item-title>
                Press <kbd>enter</kbd> to create a new one
              </v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </template>

        <template v-slot:selection="{ attrs, item, parent, selected }">
          <v-chip

              v-bind="attrs"
              :color="`grey lighten-3`"
              :input-value="selected"
              label
              small
          >
          <span class="pr-2">
            {{ item }}
          </span>
            <v-icon
                small
                @click="parent.selectItem(item)"
            >
              $delete
            </v-icon>
          </v-chip>
        </template>
      </v-combobox>
    </v-col>


    <v-col cols="12">
      <v-file-input
        @change="saveBase64"
        accept="image/*"
        v-model="docTypeEdit.icon"
        label="No icon"
        hint="For internal LBO use only"
        persistent-hint
        prepend-icon="mdi-file-image"
      ></v-file-input>
      <div v-if="docTypeEdit.iconBase64 !== undefined && docTypeEdit.iconBase64 !== null && docTypeEdit.iconBase64.length > 0">
        <v-img :src="docTypeEdit.iconBase64" contain height="150px" width="150px" />
      </div>
    </v-col>
    <v-col cols="12">
      <v-switch small :label="`Enabled: ${docTypeEdit.enabled.toString()}`" v-model="docTypeEdit.enabled"></v-switch>
      <v-switch small :label="`Sensitive: ${docTypeEdit.typeSensitive}`" v-model="docTypeEdit.typeSensitive"></v-switch>
    </v-col>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn color="blue darken-1" text @click="onCancel"> Cancel </v-btn>
      <v-btn color="blue darken-1" text @click="onSave" :loading="saving" :disabled="!valid"> Save </v-btn>
    </v-card-actions>
  </v-form>
</template>

<script lang="ts">
import { Component, Prop, Inject, Vue } from 'vue-property-decorator'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import LogServiceInterface from '@/core/interface/service/LogServiceInterface'

@Component
export default class DocumentTypeUpdate extends Vue {
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Prop({ required: true }) docTypeEdit: any

  valid = false
  saving = false

  notEmptyRule = [(v: any) => !!v || 'Field is required']

  onCancel() {
    this.$emit('cancel')
  }

  mounted(){
    if(this.docTypeEdit.mappingNames === null){
      this.docTypeEdit.mappingNames = []
    }
  }



  onSave() {
    this.saving = true
    this.rootScope.provide.documentGeneration
      .saveDocumentType(this.docTypeEdit)
      .then((result: any) => {
        this.saving = false
        this.$emit('save', result)
      })
      .catch((err: any) => {
        this.saving = false
        this.logService.error("Can't save document type due " + err)
      })
  }

  saveBase64(file: any) {
    if (file != null && file.name) {
      const reader = new FileReader()
      reader.onload = (e) => {
        if (e !== null && e.target !== null) {
          this.docTypeEdit.iconBase64 = e.target.result
          this.docTypeEdit.iconName = file.name
          this.docTypeEdit.iconType = file.type
          this.docTypeEdit.iconSize = file.size
        }
      }
      reader.readAsDataURL(file)
    } else {
      this.docTypeEdit.iconBase64 = ''
      this.docTypeEdit.iconName = ''
      this.docTypeEdit.iconType = ''
      this.docTypeEdit.iconSize = null
    }
  }
}
</script>

<style scoped>
.lithiumPrimary {
  background-color: #3c8dbc;
}
</style>
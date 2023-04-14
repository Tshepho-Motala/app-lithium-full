<template>
  <v-card data-test-id="cnt-progressive-item-editor">
    <v-card-title>{{ translate('UI_NETWORK_ADMIN.CMS.PROGRESSIVE_ITEM_EDITOR.HEADER.TITLE') }}</v-card-title>
    <v-card-text>
      <v-row>
        <v-col cols="12" md="12">
          <v-text-field
              :label="translate('UI_NETWORK_ADMIN.CMS.PROGRESSIVE_ITEM_EDITOR.FIELDS.PROGRESSIVE_ID.LABEL')"
              disabled v-model="progressive.progressiveId"
              data-test-id="txt-progressive-id"></v-text-field>
        </v-col>
        <v-col cols="12" md="12">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.PROGRESSIVE_ITEM_EDITOR.FIELDS.TITLE.LABEL')" v-model="progressive.title" data-test-id="txt-progressive-title"></v-text-field>
        </v-col>
        <v-col cols="12">
          <v-textarea :label="translate('UI_NETWORK_ADMIN.CMS.PROGRESSIVE_ITEM_EDITOR.FIELDS.DESCRIPTION.LABEL')" v-model="progressive.description" data-test-id="txt-progressive-description"></v-textarea>
        </v-col>
      </v-row>
    </v-card-text>
    <v-card-actions>
      <v-btn data-test-id="btn-cancel" text @click="onCancelClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.CANCEL') }}</v-btn>
      <v-spacer></v-spacer>
      <v-btn data-test-id="btn-save" color="primary" @click="onSaveClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.SAVE') }}</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script lang="ts">

import {Component, Inject, Prop, Vue} from "vue-property-decorator"
import LayoutProgressiveGameItem from "@/plugin/cms/models/LayoutProgressiveGameItem"
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface"

@Component({
  components: {

  }
})
export default class ProgressiveItemEditor extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  @Prop() progressive!: LayoutProgressiveGameItem

  onCancelClick() {
    this.$emit("cancel-progressive-edit")
  }

  onSaveClick() {
    this.$emit("update-progressive", this.progressive)
  }

  translate(text: string) {
    return this.translateService.instant(text)
  }
}
</script>

<style scoped>

</style>
<template>
  <v-container fluid>

    <v-row class="v-reset-row">
      <v-col md="12">
        <v-btn
          color="success"
          @click="showDialog = true"
          :disabled="loading || !canAdd('WEB_ASSET_ADD')"
          v-if="hasConfig"
        >
          <v-icon left>mdi-plus-thick</v-icon>
          {{ $translate("UI_NETWORK_ADMIN.CMS.ASSETS.BUTTON.ADD") }}
        </v-btn>
      </v-col>
    </v-row>


    <v-row class="v-reset-row">
      <v-col md="12">
        <v-data-table
          :headers="headers"
          :items="assets"
          :server-items-length="totalItems"
          :options.sync="options"
          class="elevation-1"
          :items-per-page="20"
          :loading="loading"
          :footer-props="{ 'items-per-page-options': [5, 10, 15, 20, 100, -1] }"
        >
          <template v-slot:[`item.url`]="{ item }">
            <a :href="item.url" target="blank">{{ item.url }}</a>
          </template>

          <template v-slot:[`item.size`]="{ item }">
            {{ convertSize(item.size) }}
          </template>

          <template v-slot:[`item.uploadedDate`]="{ item }">
            {{ timestampToDate(item.uploadedDate) }}
          </template>

          <template v-slot:[`item.id`]="{ item }">
            <v-btn tile small color="error" @click="onDelete(item)" :disabled="!canDelete('WEB_ASSET_DELETE')">
              <v-icon left> mdi-delete </v-icon>
              {{ $translate("UI_NETWORK_ADMIN.CMS.ASSETS.BUTTON.DELETE") }}
            </v-btn>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
    <asset-upload-form
      :visible="showDialog"
      :type="type"
      :config="providerConfig"
      :domain="domain"
      @upload-error="onNotification"
      @upload-close="onFormClose"
      @upload-complete="onNotification"
    />
  </v-container>
</template>

<script lang="ts">
import { Component, Mixins, Prop, Watch } from "vue-property-decorator";

import TranslationMixin from "@/core/mixins/translationMixin";
import AssetUploadForm from "./AssetUploadForm.vue";
import CmsAssetType from "../models/CmsAssetType";
import { ConfirmDialogInterface } from "@/plugin/components/dialog/DialogInterface";
import AssetTabMixin from '@/plugin/cms/mixins/AssetTabMixin';

@Component({
  components: { AssetUploadForm },
})
export default class AssetTab extends Mixins(AssetTabMixin,TranslationMixin) {
  @Prop({ required: true, type: String })
  type!: CmsAssetType;

  headers = [
    {
      text: this.$translate("UI_NETWORK_ADMIN.CMS.ASSETS.FIELDS.NAME.NAME"),
      align: "start",
      sortable: true,
      value: "name",
    },
    {
      text: this.$translate("UI_NETWORK_ADMIN.CMS.ASSETS.FIELDS.URL.NAME"),
      sortable: true,
      value: "url",
    },
    {
      text: this.$translate("UI_NETWORK_ADMIN.CMS.ASSETS.FIELDS.SIZE.NAME"),
      sortable: true,
      value: "size",
    },
    {
      text: this.$translate(
        "UI_NETWORK_ADMIN.CMS.ASSETS.FIELDS.UPLOADED_DATE.NAME"
      ),
      sortable: true,
      value: "uploadedDate",
    },
    { text: "Action", sortable: false, value: "id" },
  ];

  onDelete(id: number) {
    const params: ConfirmDialogInterface = {
      title: this.$translate("UI_NETWORK_ADMIN.CMS.ASSETS.DELETE.TITLE"),
      text: this.$translate("UI_NETWORK_ADMIN.CMS.ASSETS.DELETE.PROMPT"),
      btnPositive: {
        text: this.$translate("UI_NETWORK_ADMIN.CMS.ASSETS.DELETE.CONFIRM"),
        onClick: async () => {
          try {
            await this.cms.deleteImage(this.domain, id);

            this.success( this.$translate(
                "UI_NETWORK_ADMIN.CMS.ASSETS.DELETE.SUCCESS_MESSAGE"
              ))

            this.getAssets(this.type)

          } catch (error) {
            this.logService.error(error);

            this.error(this.$translate(
                "UI_NETWORK_ADMIN.CMS.ASSETS.DELETE.ERROR_MESSAGE"
            ))
            
          }
        },
      },
      btnNegative: {
        text: this.$translate("UI_NETWORK_ADMIN.CMS.ASSETS.DELETE.CANCEL"),
        flat: true,
        onClick: () => {
          this.logService.log("delete action cancelled");
        },
      },
    };

    this.listenerService.call("dialog-confirm", params);
  }

  created() {
    this.getAssets(this.type);
    this.getConfig();
  }

  @Watch("options", { deep: true })
  onPageChanged(newVal: any, oldVal: any) {
    this.getAssets(this.type);
  }

  @Watch("domain")
  onDomainChanged() {
    this.getAssets(this.type);
  }
}
</script>

<style scoped></style>

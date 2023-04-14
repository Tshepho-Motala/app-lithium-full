<template>
  <v-container fluid>
    <v-row class="v-reset-row">
      <v-col md="12">
        <v-btn
          color="success"
          @click="showDialog = true"
          :disabled="loading || !canAdd(type.addRole)"
          v-if="hasConfig"
        >
          <v-icon left>mdi-plus-thick</v-icon>
          {{ $translate("UI_NETWORK_ADMIN.CASINO.IMAGES.BUTTON.ADD") }}
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
          :footer-props="{ 'items-per-page-options': [5, 10, 15, 20, 100] }"
        >
          <template v-slot:[`item.name`]="{ item }">
            <img :src="item.url" style="width: 60px;" />
          </template>

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
            <v-btn tile small color="error" @click="onDelete(item)" :disabled="!canDelete(type.deleteRole)">
              <v-icon left> mdi-delete </v-icon>
              {{ $translate("UI_NETWORK_ADMIN.CASINO.IMAGES.BUTTON.DELETE") }}
            </v-btn>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
    <image-upload-form
      :visible="showDialog"
      :type="type.type"
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
import ImageUploadForm from "./ImageUploadForm.vue";
import { ConfirmDialogInterface } from "@/plugin/components/dialog/DialogInterface";
import AssetTabMixin from '@/plugin/cms/mixins/AssetTabMixin';

@Component({
  components: { ImageUploadForm },
})
export default class ImageTab extends Mixins(AssetTabMixin,TranslationMixin) {
  @Prop({ required: true, type: Object })
  type: any;

  headers = [
    {
      text: this.$translate("UI_NETWORK_ADMIN.CASINO.IMAGES.FIELDS.NAME.NAME"),
      align: "start",
      sortable: true,
      value: "name",
    },
    {
      text: this.$translate("UI_NETWORK_ADMIN.CASINO.IMAGES.FIELDS.URL.NAME"),
      sortable: true,
      value: "url",
    },
    {
      text: this.$translate("UI_NETWORK_ADMIN.CASINO.IMAGES.FIELDS.SIZE.NAME"),
      sortable: true,
      value: "size",
    },
    {
      text: this.$translate(
        "UI_NETWORK_ADMIN.CASINO.IMAGES.FIELDS.UPLOADED_DATE.NAME"
      ),
      sortable: true,
      value: "uploadedDate",
    },
    { text: "Action", sortable: false, value: "id" },
  ];

  

  onDelete(id: number) {
    const params: ConfirmDialogInterface = {
      title: this.$translate("UI_NETWORK_ADMIN.CASINO.IMAGES.DELETE.TITLE"),
      text: this.$translate("UI_NETWORK_ADMIN.CASINO.IMAGES.DELETE.PROMPT"),
      btnPositive: {
        text: this.$translate("UI_NETWORK_ADMIN.CASINO.IMAGES.DELETE.CONFIRM"),
        onClick: async () => {
          try {
            await this.cms.deleteImage(this.domain, id);

            this.success(this.$translate(
                "UI_NETWORK_ADMIN.CASINO.IMAGES.DELETE.SUCCESS_MESSAGE"
            ))

            this.getAssets(this.type.type)

          } catch (error) {
            this.logService.error(error);

            this.error(this.$translate(
                "UI_NETWORK_ADMIN.CASINO.IMAGES.DELETE.ERROR_MESSAGE"
            ))
          }
        },
      },
      btnNegative: {
        text: this.$translate("UI_NETWORK_ADMIN.CASINO.IMAGES.DELETE.CANCEL"),
        flat: true,
        onClick: () => {
          this.logService.log("delete action cancelled");
        },
      },
    };

    this.listenerService.call("dialog-confirm", params);
  }

  created() {
    this.getAssets(this.type.type);
    this.getConfig();
  }

  @Watch("options", { deep: true })
  onPageChanged(newVal: any, oldVal: any) {
    this.getAssets(this.type.type);
  }

  @Watch("domain")
  onDomainChanged() {
    this.getAssets(this.type.type);
  }
}
</script>

<style scoped></style>

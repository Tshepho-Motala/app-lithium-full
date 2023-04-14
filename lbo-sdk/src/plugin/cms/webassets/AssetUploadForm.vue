<template>
  <v-dialog v-model="visible" max-width="500" persistent origin="top">
    <v-card>
      <v-card-title class="px-10">
        <span class="text-h5 text-center">{{ title }}</span>
      </v-card-title>
      <v-card-text>
        <v-form v-model="valid" :ref="type">
          <v-col cols="12">
            <v-text-field
              outlined
              label="Name"
              v-model="form.name"
              :rules="[required]"
              :error-messages="errorMessages.name"
            ></v-text-field>
          </v-col>

          <v-col cols="12">
            <v-file-input
              outlined
              label="File"
              prepend-icon=""
              append-icon="mdi-attachment"
              @change="fileSelected"
              :rules="[required, notEmpty]"
              :accept="accept"
            ></v-file-input>
          </v-col>
        </v-form>
      </v-card-text>
      <v-card-actions class="px-10">
        <v-progress-circular
          v-if="uploading"
          color="primary"
          size="50"
          indeterminate
          >{{ progress }}%</v-progress-circular
        >
        <v-spacer></v-spacer>
        <v-btn color="error" text @click.prevent="cancel" :disabled="uploading"
          >Cancel</v-btn
        >
        <v-btn color="blue" text @click="upload" :disabled="!valid || uploading"
          >Upload</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import TranslationMixin from "@/core/mixins/translationMixin";
import {Component,Mixins} from "vue-property-decorator";
import { slug } from "@/core/utils/stringUtils";
import CmsAssetType from "../models/CmsAssetType";
import AssetUploadFormMixin from "../mixins/AssetUploadFormMixin";

@Component
export default class AssetUploadForm extends Mixins(AssetUploadFormMixin,TranslationMixin) {
  form: any = {
    name: "",
    file: "",
  };

  get title() {
    return this.$translate("UI_NETWORK_ADMIN.CMS.ASSETS.FORM.TITLE");
  }
  get accept(): string {
    return this.type === CmsAssetType.Style
      ? "text/css"
      : ".woff, .woff2, .svg, .ttf, .otf, .eot";
  }

  fileSelected(file: any) {
    this.form.file = file;
  }

  async upload() {
    this.uploading = true;

    try {
      let data = new FormData();
      let file = this.form.file as File;
      let name = slug(this.form.name);
      let filename = name + "-" + file.size + "." + file.name.split(".").pop();

      data.append("name", name);
      data.append("file", file, filename);
      data.append("size", file.size.toString());
      data.append(
        "url",
        `${this.config.uri}${this.config.bucketCmsAssetPrefix}/${filename}`
      );
      data.append("type", this.type);

      await this.rootScope.provide.cmsProvider.upload(
        this.domain,
        data,
        this.onProgress
      );

      const form = this.$refs[this.type] as any;
      form.reset();
      this.uploadSuccess(this.$translate("UI_NETWORK_ADMIN.CMS.ASSETS.ADD.SUCCESS_MESSAGE"));
    } catch (error) {
      this.logService?.error(error);
      this.uploadError(this.$translate("UI_NETWORK_ADMIN.CMS.ASSETS.ADD.ERROR_MESSAGE"))
    }

    this.uploading = false;
  }
}
</script>

<style scoped></style>

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
              :label="nameLabel"
              v-model="form.name"
              :rules="[required]"
              :error-messages="errorMessages.name"
              
            ></v-text-field>
          </v-col>

          <v-col cols="12">
            <v-file-input
              outlined
              label="Image"
              prepend-icon=""
              append-icon="mdi-attachment"
              @change="imageSelected"
              :rules="[required, notEmpty]"
               accept="image/*"
            ></v-file-input>
          </v-col>

          <div v-if="form.image">
            <div class="d-flex justify-center">
              <img style="height:150px" :src="previewUrl" alt="" />
            </div>
          </div>
        </v-form>
      </v-card-text>
      <v-card-actions class="px-10">
        <v-progress-circular v-if="uploading" color="primary" size="50" indeterminate>{{ progress }}%</v-progress-circular>
        <v-spacer></v-spacer>
        <v-btn color="error" text @click.prevent="cancel" :disabled="uploading">Cancel</v-btn>
        <v-btn color="blue" text @click="upload" :disabled="!valid || uploading">Upload</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import TranslationMixin from "@/core/mixins/translationMixin";
import { Component, Mixins } from "vue-property-decorator";
import { slug } from '@/core/utils/stringUtils';

import AssetUploadFormMixin from '@/plugin/cms/mixins/AssetUploadFormMixin'
import CmsAssetType from "@/plugin/cms/models/CmsAssetType";
import CmsAllowedImageTypes from "@/plugin/cms/models/CmsAllowedImageTypes";

@Component
export default class ImageUploadForm extends Mixins(AssetUploadFormMixin, TranslationMixin) {
  
  form: any = {
      name: '',
      image: ''
  };

  get previewUrl(): string {
      return URL.createObjectURL(this.form.image)
  }  

  get title() {
    return this.$translate(
      `UI_NETWORK_ADMIN.CASINO.IMAGES.${this.type?.toUpperCase()}.TITLE`
    );
  }

  get nameLabel () {
      return this.$translate(`UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.${this.type?.toUpperCase()}.NAME`)
  }

  imageSelected(image:any) {
      this.form.image = image
  }


  checkIfFileIsValid(type: string, image: File): boolean {
    if (type === CmsAssetType.Banner || type === CmsAssetType.Tile) {
     let fileNameAndTypeArray = image.type.split("/");
     if (fileNameAndTypeArray[0] !== "image" || !this.checkFileExtension(fileNameAndTypeArray[1])) {
       return false;
     }

    }
    return true;
  }

  checkFileExtension(extension: string): boolean {
    for (let cmsAllowedImageType in CmsAllowedImageTypes) {
      if (extension.toUpperCase() === cmsAllowedImageType.toUpperCase()) {
        return true;
      }
    }
    return false;
  }

  async upload() {
      this.uploading = true;

      try {
          let data = new FormData()
          let image = this.form.image as File
          const form = this.$refs[this.type] as any;

        if (!this.checkIfFileIsValid(this.type, image)) {
          this.uploadError(this.$translate(`UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.INVALID.ERROR_MESSAGE`));
          this.uploading = false;
          form.reset();
          return;
        }

          let name = slug(this.form.name);
          let filename =  name + '-'+ image.size +'.'+ image.name.split('.').pop();

          data.append('name', name)
          data.append('file', image, filename)
          data.append('size', image.size.toString())
          data.append('url', `${this.config.uri}${this.config.bucketCmsImagePrefix}/${filename}`)
          data.append("type", this.type)

          await this.rootScope.provide.cmsProvider.upload(this.domain, data, this.onProgress)


          form.reset();
          this.uploadSuccess(this.$translate(`UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.${this.type.toUpperCase()}.SUCCESS_MESSAGE`))
      }
      catch(error) {
          this.logService?.error(error)
          this.uploadError(this.$translate(`UI_NETWORK_ADMIN.CASINO.IMAGES.FORM.${this.type.toUpperCase()}.ERROR_MESSAGE`))
      }

      this.uploading = false;
  }

}
</script>

<style scoped>
</style>

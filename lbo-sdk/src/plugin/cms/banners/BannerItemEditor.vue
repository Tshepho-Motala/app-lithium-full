<template>
<!--  <v-card data-test-id="cnt-banner-item-editor">-->
<!--    <v-card-title> {{ translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.OUTPUT.HEADER.TITLE') }}</v-card-title>-->
<!--    <v-card-text>-->
      <v-row>
        <v-col cols="12">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.IMAGE.LABEL')" v-model="banner.imageUrl" disabled data-test-id="txt-game-image"></v-text-field>
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.NAME.LABEL')" disabled v-model="banner.name" data-test-id="txt-game-name"></v-text-field>
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.DISPLAY_TEXT.LABEL')" v-model="banner.displayText" data-test-id="txt-game-display-text"></v-text-field>
        </v-col>
        <v-col cols="12" md="12">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.TERMS_URL.LABEL')" v-model="banner.termsUrl" data-test-id="txt-game-terms-url"></v-text-field>
        </v-col>
        <v-col cols="12" md="12">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.URL.LABEL')" v-model="banner.link" data-test-id="txt-game-url"></v-text-field>
        </v-col>
        <v-col cols="12" md="12">
          <v-select
              data-test-id="txt-loggedIn"
              v-model="banner.loggedIn"
              :items="loggedInOptions"
              item-text="text"
              item-value="value"
              label="Logged In"
              persistent-hint
              single-line
          ></v-select>
        </v-col>
        <v-col cols="12" md="6">
          <v-time-picker :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.FROM.LABEL')" v-model="banner.timeFrom" data-test-id="txt-game-from" format="HH:mm:ss"></v-time-picker>
          <p class="text-md-center">{{ translate('UI_NETWORK_ADMIN.CMS.BANNER_EDITOR.TIME_PICKER.UTC_TIME_SPECIFICATION.LABEL') }}</p>
        </v-col>
        <v-col cols="12" md="6">
          <v-time-picker :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.TO.LABEL')" v-model="banner.timeTo" data-test-id="txt-games-to" format="HH:mm:ss"></v-time-picker>
          <p class="text-md-center">{{ translate('UI_NETWORK_ADMIN.CMS.BANNER_EDITOR.TIME_PICKER.UTC_TIME_SPECIFICATION.LABEL') }}</p>
        </v-col>
      </v-row>
<!--    </v-card-text>-->
<!--    <v-card-actions>-->
<!--      <v-btn data-test-id="btn-cancel" text @click="onCancelClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.CANCEL') }}</v-btn>-->
<!--      <v-spacer></v-spacer>-->
<!--      <v-btn data-test-id="btn-error" color="error" @click="onDeleteClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.DELETE') }}</v-btn>-->
<!--      <v-btn data-test-id="btn-save" color="primary" @click="onSaveClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.SAVE') }}</v-btn>-->
<!--    </v-card-actions>-->
<!--  </v-card>-->
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import LayoutBannerItem from "@/plugin/cms/models/LayoutBannerItem";
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import {Banner} from "@/plugin/cms/models/Banner";

@Component({
  components: {}
})
export default class BannerItemEditor extends Vue{
  @Inject('translateService') readonly translateService!: TranslateServiceInterface;
  @Prop() banner!: Banner;

  loggedInOptions = [
    { text: 'Both', value: null },
    { text: 'Logged In', value: true },
    { text: 'Logged out', value: false }
  ]

  onCancelClick() {
    this.$emit("cancel-game-edit");
  }

  onSaveClick() {
    this.$emit("update-banner", this.banner);
  }

  onDeleteClick(){
    this.$emit("delete-banner", this.banner);
  }

  translate(text: string) {
    return this.translateService.instant(text);
  }
}
</script>

<style scoped>

</style>

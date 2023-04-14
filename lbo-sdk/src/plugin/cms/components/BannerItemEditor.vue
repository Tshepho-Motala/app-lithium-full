<template>
  <v-card data-test-id="cnt-banner-item-editor">
    <v-card-title> {{ translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.OUTPUT.HEADER.TITLE') }}</v-card-title>
    <v-card-text>
      <v-row>
        <v-col cols="12" md="6">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.NAME.LABEL')" disabled v-model="game.name" data-test-id="txt-game-name"></v-text-field>
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.GAME_GUID.LABEL')" disabled v-model="game.gameID" data-test-id="txt-game-id"></v-text-field>
        </v-col>
        <v-col cols="12">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.IMAGE.LABEL')" v-model="game.image" data-test-id="txt-game-image"></v-text-field>
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.ID.LABEL')"  v-model.number="game.id" data-test-id="txt-game-id" type="number"></v-text-field>
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.DISPLAY_TEXT.LABEL')" v-model="game.display_text" data-test-id="txt-game-display-text"></v-text-field>
        </v-col>
        <v-col cols="12" md="12">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.TERMS_URL.LABEL')" v-model="game.terms_url" data-test-id="txt-game-terms-url"></v-text-field>
        </v-col>
        <v-col cols="12" md="12">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.URL.LABEL')" v-model="game.url" data-test-id="txt-game-url"></v-text-field>
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.FROM.LABEL')" v-model="game.from" data-test-id="txt-game-from"></v-text-field>
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.TO.LABEL')" v-model="game.to" data-test-id="txt-games-to"></v-text-field>
        </v-col>
        <v-col cols="12" md="12">
          <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ITEM_EDITOR.FIELDS.RUN_COUNT.LABEL')" v-model="game.runcount" data-test-id="txt-games-run-count"></v-text-field>
        </v-col>
      </v-row>
    </v-card-text>
    <v-card-actions>
      <v-btn data-test-id="btn-cancel" text @click="onCancelClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.CANCEL') }}</v-btn>
      <v-spacer></v-spacer>
      <v-btn data-test-id="btn-error" color="error" @click="onDeleteClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.DELETE') }}</v-btn>
      <v-btn data-test-id="btn-save" color="primary" @click="onSaveClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.SAVE') }}</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import LayoutBannerItem from "@/plugin/cms/models/LayoutBannerItem";
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";

@Component({
  components: {}
})
export default class BannerItemEditor extends Vue{
  @Inject('translateService') readonly translateService!: TranslateServiceInterface;
  @Prop() game!: LayoutBannerItem;

  onCancelClick() {
    this.$emit("cancel-game-edit");
  }

  onSaveClick() {
    this.$emit("update-banner", this.game);
  }

  onDeleteClick(){
    this.$emit("delete-banner", this.game);
  }

  translate(text: string) {
    return this.translateService.instant(text);
  }
}
</script>

<style scoped>

</style>
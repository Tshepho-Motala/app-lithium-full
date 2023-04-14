<template>
    <v-card data-test-id="cnt-game-item-editor">
      <v-card-title> {{ translate('UI_NETWORK_ADMIN.CMS.GAME_ITEM_EDITOR.OUTPUT.HEADER.TITLE') }}</v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.GAME_ITEM_EDITOR.FIELDS.NAME.LABEL')" disabled v-model="game.gameName" data-test-id="txt-game-name"></v-text-field>
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.GAME_ITEM_EDITOR.FIELDS.GAME_GUID.LABEL')" disabled v-model="game.gameID" data-test-id="txt-game-id"></v-text-field>
          </v-col>
          <v-col cols="12">
            <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.GAME_ITEM_EDITOR.FIELDS.IMAGE.LABEL')" v-model="game.image" data-test-id="txt-game-image"></v-text-field>
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.GAME_ITEM_EDITOR.FIELDS.TYPE.LABEL')" disabled v-model="game.type" data-test-id="txt-game-type"></v-text-field>
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.GAME_ITEM_EDITOR.FIELDS.BADGE.LABEL')" v-model="game.badge" data-test-id="txt-game-badge"></v-text-field>
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field :label="translate('UI_NETWORK_ADMIN.CMS.GAME_ITEM_EDITOR.FIELDS.PROMO.ID')" v-model="game.promoId" data-test-id="txt-game-promo-id"></v-text-field>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <v-btn data-test-id="btn-cancel" text @click="onCancelClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.CANCEL') }}</v-btn>
        <v-spacer></v-spacer>
        <v-btn data-test-id="btn-delete" color="error" @click="onDeleteClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.DELETE') }}</v-btn>
        <v-btn data-test-id="btn-save" color="primary" @click="onSaveClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.SAVE') }}</v-btn>
      </v-card-actions>
    </v-card>


</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator"
import LayoutGameItem from "@/plugin/cms/models/LayoutGameItem"
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface"

@Component({
  components: {}
})
export default class GameItemEditor extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  @Prop() game!: LayoutGameItem

  onCancelClick() {
    this.$emit("cancel-game-edit")
  }

  onSaveClick() {
  this.$emit("update-game", this.game)
  }

  onDeleteClick(){
    this.$emit("delete-game", this.game)
  }

  translate(text: string) {
    return this.translateService.instant(text)
  }

}
</script>

<style scoped>

</style>

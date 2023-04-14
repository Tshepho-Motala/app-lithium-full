<template>
  <v-card>
    <v-card-title> {{ translate('UI_NETWORK_ADMIN.CMS.BANNER_ENTRY_LAYOUT_SELECTOR.OUTPUT.HEADER.TITLE') }}</v-card-title>
    <v-card-text>
      <v-select :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ENTRY_LAYOUT_SELECTOR.FIELDS.SELECT_LAYOUT.LABEL')" v-model="selectedLayoutType" :items="layoutTypes" return-object></v-select>
      <v-select :label="translate('UI_NETWORK_ADMIN.CMS.BANNER_ENTRY_LAYOUT_SELECTOR.FIELDS.SELECT_TILE_SIZE.LABEL')" v-if="selectedLayoutType" v-model="tileSize" :items="tileSizeList"  return-object></v-select>
    </v-card-text>
    <v-card-actions>
      <v-btn text @click="onCancelClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.CANCEL') }}</v-btn>
      <v-spacer></v-spacer>
      <v-btn color="primary" @click="onSaveClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.SAVE') }}</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from 'vue-property-decorator'
import LobbyItem from "@/plugin/cms/models/LobbyItem";
import LayoutBuilderBanner from "@/plugin/cms/components/LayoutBuilderBanner.vue";
import LayoutBannerEntryItem, {TileSizeEnum} from "@/plugin/cms/models/LayoutBannerEntryItem";
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import {LayoutBannerEntryTypeEnum} from "@/plugin/cms/models/LayoutBannerEntryTypeEnum";

@Component({
  components: {
    LayoutBuilderBanner
  }
})
export default class BannerEntryLayoutSelector extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface;

  @Prop({required: true}) lobbyItem!: LobbyItem;

  selectedLayoutType: LayoutBannerEntryTypeEnum | null = null;
  title: string = ''

  layoutTypes: LayoutBannerEntryTypeEnum[] = [
    LayoutBannerEntryTypeEnum.BANNER,

  ];

  tileSizeList = [TileSizeEnum.STANDARD, TileSizeEnum.CUSTOM];
  tileSize: TileSizeEnum = TileSizeEnum.STANDARD;
  postId: any;
  errorMessage: any;

  onSaveClick() {
    this.selectedLayoutType = LayoutBannerEntryTypeEnum.BANNER;
    this.$emit("save-banner-item", new LayoutBannerEntryItem(this.selectedLayoutType));
    this.clearText();
  }

  onCancelClick() {
    this.clearText();
    this.$emit("cancel-create-banner");
  }

  private clearText() {
    this.selectedLayoutType = null;
  }

  translate(text: string) {
    return this.translateService.instant(text);
  }
}
</script>

<style scoped>

</style>
<template>
  <div data-test-id="cnt-grid-entry-item-editor">
    <GameItemEditor v-if="!isBannerItem" :game="game" @cancel-game-edit="onCancelClick" @update-game="onUpdateGameItemClick" @delete-game="onDeleteItemClick"></GameItemEditor>
    <BannerItemEditor v-else :game="game" @cancel-game-edit="onCancelClick" @update-banner="onUpdateBannerItemClick" @delete-banner="onDeleteItemClick"></BannerItemEditor>
  </div>
</template>

<script lang="ts">
import {Component, Prop, Vue} from "vue-property-decorator";
import LayoutGameItem from "@/plugin/cms/models/LayoutGameItem";
import LayoutBannerItem from "@/plugin/cms/models/LayoutBannerItem";
import GameItemEditor from "@/plugin/cms/components/GameItemEditor.vue";
import BannerItemEditor from "@/plugin/cms/components/BannerItemEditor.vue";

@Component({
  components: {
    BannerItemEditor,
    GameItemEditor
  }
})
export default class GridEntryItemEditor extends Vue {
  @Prop() game!: LayoutGameItem | LayoutBannerItem;
  @Prop() isBannerItem!: boolean;


  onCancelClick() {
    this.$emit("cancel-game-edit");
  }

  onUpdateGameItemClick(game: LayoutGameItem) {
    this.$emit("update-game-item", game);
  }

  onUpdateBannerItemClick(game: LayoutBannerItem) {
    this.$emit("update-banner-item", game);
  }

  onDeleteItemClick(game: LayoutBannerItem | LayoutGameItem){
    this.$emit("delete-item", game);
  }
}
</script>

<style scoped>

</style>
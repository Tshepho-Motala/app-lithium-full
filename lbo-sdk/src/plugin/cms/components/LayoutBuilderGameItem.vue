<template>
  <v-row data-test-id="cnt-layout-builder-game-item" no-gutters class="gridItemDisplay pa-2" v-on:click="editGame">
    <v-dialog v-model="showEditItemDialog" v-if="showEditItemDialog">
      <GridEntryItemEditor :game="gameEditCopy"
                           :isBannerItem="isBannerItem"
                           @cancel-game-edit="onCancelEdit"
                           @update-game-item="onUpdateGameItem"
                           @delete-item="removeGameTile">
      </GridEntryItemEditor>
    </v-dialog>
    <v-col cols="12" class="text-center">
      {{ gameName }}
    </v-col>
    <v-col cols="12" class="text-center">
      <v-img v-if="game.image" :alt="`${gameName} avatar`" :src="game.image"></v-img>
      <v-avatar color="primary" v-else>
        <span class="white--text">{{ makeAcronym(gameName) }}</span>
      </v-avatar>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import {Component, Prop, Vue} from 'vue-property-decorator'
import LayoutGameItem from "@/plugin/cms/models/LayoutGameItem"
import LayoutBannerItem from "@/plugin/cms/models/LayoutBannerItem"
import GameItemEditor from "@/plugin/cms/components/GameItemEditor.vue"
import GridEntryItemEditor from "@/plugin/cms/components/GridEntryItemEditor.vue"
import {LayoutWidgetEntryTypeEnum} from "@/plugin/cms/models/LayoutGameEntryTypeEnum"
import {PageBanner} from "@/plugin/cms/models/PageBanner";

@Component({
  components: {GridEntryItemEditor, GameItemEditor: GameItemEditor}
})
export default class LayoutBuilderGameItem extends Vue {
  @Prop({required: true}) game!: LayoutGameItem
  @Prop({required: true}) entryType!: LayoutWidgetEntryTypeEnum

  showEditItemDialog: boolean = false
  gameEditCopy!: LayoutGameItem | LayoutBannerItem | null

  get isBannerItem() {
    return this.entryType === LayoutWidgetEntryTypeEnum.BANNER
  }
  makeAcronym(str: string): string {
    return str.match(/\b(\w)/g)!.join('')
  }

  get gameName(): string {
    return this.game.gameName ? this.game.gameName : ''
  }

  editGame() {
    this.gameEditCopy = JSON.parse(JSON.stringify(this.game))
    this.showEditItemDialog = true
  }

  onCancelEdit() {
    this.showEditItemDialog = false
    this.gameEditCopy = null
  }

  onUpdateGameItem(game: LayoutGameItem) {
    this.game.image = game.image
    this.game.badge = game.badge
    this.game.promoId = game.promoId
    this.showEditItemDialog = false
    this.gameEditCopy = null
  }

  removeGameTile(game: LayoutGameItem | LayoutBannerItem){
    this.$emit("delete-item", game)
    this.showEditItemDialog = false
  }
}
</script>

<style scoped>
.gridItemDisplay {
  border: 1px solid grey;
  border-radius: 5px;
  min-height: 150px;
  width: 100%;
  cursor: pointer;
}
</style>

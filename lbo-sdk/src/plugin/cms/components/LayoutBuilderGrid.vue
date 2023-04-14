<template>
  <v-row data-test-id="cnt-layout-builder-grid">
    <v-dialog v-model="showProgressiveEditItemDialog" v-if="showProgressiveEditItemDialog">
      <progressive-item-editor :progressive="progressiveEditCopy"
                           @cancel-progressive-edit="onCancelEdit"
                           @update-progressive="onUpdateProgressiveItem">
      </progressive-item-editor>
    </v-dialog>
    <v-col cols="12" v-if="!entry.showWidgetEntries">
      <v-icon>mdi-information</v-icon> {{ translate('UI_NETWORK_ADMIN.CMS.LAYOUT_BUILDER_GRID.OUTPUT.AUTO_GENERATED_WIDGET.TITLE') }}
    </v-col>
    <!-- LIVE TILES -->
    <v-col cols="12" v-if="entry.showWidgetEntries">
      <div v-if="entry.progressives" class="mt-2">
        <v-chip class="ml-1 mt-2" v-for="(progressive, i) in entry.progressives" :key="i" color="blue" v-on:click="editProgressiveClick(progressive)">{{ progressive.progressiveId }}</v-chip>
      </div>
      <draggable v-model="entry.tiles" class="row mt-2">
        <v-col cols="12" md="4" lg="3" v-for="(item, i) in entry.tiles" :key="`item-${i}`" class="pa-3">
          <LayoutBuilderGameItem
              :data-test-id="`cnt-layout-builder-game-item-${i}`"
              :game="item"
              :entryType="entry.type"
              @delete-item="removeGameTile"
          />
        </v-col>
      </draggable>

      <!-- NEW TILE -->
      <v-row>
        <v-col cols="12" md="4" lg="3" class="pa-3" v-if="showLayoutBuilderGameTemplate()">
          <LayoutBuilderGameTemplate :widgetProgressives="entry.progressives" :gameTiles="entry.tiles" :selectedDomain="selectedDomain"
                                     :selectedChannel="selectedChannel" :lobbyItem="lobbyItem"
                                     @onSelect="onGameSelected"/>
        </v-col>
      </v-row>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import {Component, Inject, Prop, Vue} from 'vue-property-decorator'
import LayoutGameEntryItem, {TileSizeEnum} from '../models/LayoutGameEntryItem'
import LayoutBannerItem from '../models/LayoutBannerItem'
import LobbyItem from '../models/LobbyItem'
import LayoutBuilderGameTemplate from './LayoutBuilderGameTemplate.vue'
import LayoutBuilderGameItem from './LayoutBuilderGameItem.vue'
import LayoutBuilderGameRemoveTile from './LayoutBuilderGameRemoveTile.vue'
import ProgressiveItemEditor from './ProgressiveItemEditor.vue'
import draggable from 'vuedraggable'
import LayoutGameItem from '@/plugin/cms/models/LayoutGameItem'
import GameItemInterface from '@/plugin/cms/interfaces/GameItemInterface'
import {LayoutWidgetEntryTypeEnum} from '@/plugin/cms/models/LayoutGameEntryTypeEnum'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface"
import LayoutJackpotGameEntryItem from '../models/LayoutJackpotGameEntryItem'
import LayoutProgressiveGameItem from "@/plugin/cms/models/LayoutProgressiveGameItem"

@Component({
  components: {
    LayoutBuilderGameTemplate,
    LayoutBuilderGameItem,
    LayoutBuilderGameRemoveTile,
    ProgressiveItemEditor,
    draggable
  }
})
export default class LayoutBuilderGrid extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  @Prop({ required: true }) lobbyItem!: LobbyItem
  @Prop({ required: true }) entry!: any
  @Prop({ default: false }) allowTitle!: boolean
  @Prop() selectedDomain!: string
  @Prop() selectedChannel!: string

  selectedGame: GameItemInterface | null = null
  selected: GameItemInterface | null = null

  progressiveEditCopy?: LayoutProgressiveGameItem
  showProgressiveEditItemDialog = false
  selectedProgressive?: LayoutProgressiveGameItem

  get isBannerItem() {
    return this.entry.type === LayoutWidgetEntryTypeEnum.BANNER
  }

  onGameSelected(game: GameItemInterface) {
    if (this.entry.tiles) {
      const tileItem = this.buildTile(game)
      if (this.entry.tiles.filter((x) => x.gameName === game.name).length === 0) {
        this.entry.tiles.push(tileItem)
      }
    }
  }

  private buildTile(game: GameItemInterface): LayoutGameItem {
    const layoutGameItem = new LayoutGameItem(game.commercialName, game.guid)
    if (this.entry.tile_size === TileSizeEnum.STANDARD) layoutGameItem.image = game.cdnImageUrl
    return layoutGameItem
  }

  get isJackpotWidgetEntry() {
    return this.entry.type.valueOf().includes("jackpot");
  }

  showLayoutBuilderGameTemplate() {
    return !this.isJackpotWidgetEntry || (this.entry.type.valueOf() === LayoutWidgetEntryTypeEnum.JACKPOT_TILE.valueOf() && this.entry.tiles.length < 3
          && this.entry.type.valueOf() !== LayoutWidgetEntryTypeEnum.JACKPOT_GRID.valueOf())
  }

  buildBannerItem(game: GameItemInterface): LayoutBannerItem {
    return new LayoutBannerItem(game.name, game.cdnImageUrl)
  }

  removeGameTile(game: LayoutGameItem) {
    const index = this.entry.tiles.findIndex(tile => tile.gameID === game.gameID)
    this.entry.tiles.splice(index, 1)
  }

  editProgressiveClick(progressive: LayoutProgressiveGameItem) {
    this.selectedProgressive = progressive
    this.progressiveEditCopy = Object.assign(new LayoutProgressiveGameItem(), JSON.parse(JSON.stringify(progressive)))
    this.showProgressiveEditItemDialog = true
  }

  onCancelEdit() {
    this.showProgressiveEditItemDialog = false
  }

  onUpdateProgressiveItem(progressive) {
    if (this.selectedProgressive) {
      this.selectedProgressive.description = progressive.description
      this.selectedProgressive.title = progressive.title
    }
    this.showProgressiveEditItemDialog = false
  }

  translate(text: string) {
    return this.translateService.instant(text)
  }
}
</script>


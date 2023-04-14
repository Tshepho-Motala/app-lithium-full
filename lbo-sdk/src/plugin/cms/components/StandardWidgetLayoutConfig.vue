<template>
  <div v-if="modifyWidget">
    <v-select data-test-id="slt-layout-type"
              :label="translate('UI_NETWORK_ADMIN.CMS.GAME_ENTRY_LAYOUT_SELECTOR.FIELDS.SELECT_LAYOUT.LABEL')"
              v-model="modifyWidget.tileWidgetType" :items="filteredLayoutTypes" return-object></v-select>
    <div v-if="!!modifyWidget.tileWidgetType">
      <v-text-field data-test-id="txt-tile"
                    :label="translate('UI_NETWORK_ADMIN.CMS.GAME_ENTRY_LAYOUT_SELECTOR.FIELDS.TITLE.LABEL')"
                    v-model="modifyWidget.title">
      </v-text-field>
      <v-select data-test-id="slt-title-size"
                :label="translate('UI_NETWORK_ADMIN.CMS.GAME_ENTRY_LAYOUT_SELECTOR.FIELDS.SELECT_TILE_SIZE.LABEL')"
                v-model="modifyWidget.tile_size"
                :items="tileSizeList"
                return-object>
      </v-select>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from 'vue-property-decorator'
import LobbyItem from "@/plugin/cms/models/LobbyItem"
import LayoutBuilderWidget from "@/plugin/cms/components/LayoutBuilderWidget.vue"
import LayoutGameEntryItem, {TileSizeEnum} from "@/plugin/cms/models/LayoutGameEntryItem"
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface"
import {TileWidgetTypeEnum} from "@/plugin/cms/models/TileWidgetTypeEnum"

@Component({
  components: {
    LayoutBuilderWidget
  }
})
export default class StandardWidgetLayoutConfig extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  @Prop({required: true}) lobbyItem!: LobbyItem
  @Prop({required: true}) modifyWidget!: LayoutGameEntryItem
  @Prop({required: true}) isModify!: LayoutGameEntryItem
  snackbar: boolean = false
  snackbarColour: string = ''
  snackbarTitle: string = ''
  modifyWidgetCopy: LayoutGameEntryItem | null = null

  tileSizeList = [TileSizeEnum.STANDARD, TileSizeEnum.CUSTOM]
  tileSize: TileSizeEnum = TileSizeEnum.STANDARD

  layoutTypes: TileWidgetTypeEnum[] = [
    TileWidgetTypeEnum.TILES,
    TileWidgetTypeEnum.GRID,
    TileWidgetTypeEnum.TOP_GAMES,
    TileWidgetTypeEnum.RECENTLY_PLAYED_GAMES,
    TileWidgetTypeEnum.ATOZ,
    TileWidgetTypeEnum.RECOMMENDED_GAMES,
    TileWidgetTypeEnum.TAGGED_GAMES,
    TileWidgetTypeEnum.JACKPOT_GRID,
    TileWidgetTypeEnum.JACKPOT_TILE,
    TileWidgetTypeEnum.DFG
  ]

  onSaveClick() {
    if (this.modifyWidgetCopy !== null) {
      if (this.modifyWidgetCopy.tileWidgetType === TileWidgetTypeEnum.RECENTLY_PLAYED_GAMES) {
        if (this.lobbyItem.page.widgets.filter(widget => {
          if (widget !== this.modifyWidget && widget.tileWidgetType === TileWidgetTypeEnum.RECENTLY_PLAYED_GAMES) {
            return true
          }
          return false
        }).length > 0) {
          this.buildSnackBarProperties("duplicate")
          return
        }
      }

      if (this.modifyWidgetCopy.tileWidgetType === TileWidgetTypeEnum.ATOZ) {
        if (this.lobbyItem.page.widgets.filter(widget => {
          if (widget !== this.modifyWidget && widget.tileWidgetType === TileWidgetTypeEnum.ATOZ) {
            return true
          }
          return false
        }).length > 0) {
          this.buildSnackBarProperties("duplicate")
          return

        }
      }
      if (this.modifyWidgetCopy.tileWidgetType === TileWidgetTypeEnum.TAGGED_GAMES) {
        if (this.lobbyItem.page.widgets.filter(widget => {
          if (widget !== this.modifyWidget && widget.tileWidgetType === TileWidgetTypeEnum.TAGGED_GAMES) {
            return true
          }
          return false
        }).length > 0) {
          this.buildSnackBarProperties("duplicate")
          return

        }
      }

      if (this.modifyWidgetCopy.tileWidgetType === TileWidgetTypeEnum.RECOMMENDED_GAMES) {
        if (this.lobbyItem.page.widgets.filter(widget => {
          if (widget !== this.modifyWidget && widget.tileWidgetType === TileWidgetTypeEnum.RECOMMENDED_GAMES) {
            return true
          }
          return false
        }).length > 0) {
          this.buildSnackBarProperties("duplicate")
          return
        }
      }
      if (this.modifyWidgetCopy?.tileWidgetType == null) {
        this.buildSnackBarProperties("invalid")
        return
      }
      Object.assign(this.modifyWidget, this.modifyWidgetCopy)
      this.modifyWidget.type = this.modifyWidget.getType
      if (!this.modifyWidget.showWidgetEntries) {
        this.modifyWidget.tiles = []
      }
      if (this.isModify) {
        this.$emit("modify-widget", this.modifyWidget)
      } else {
        this.$emit("save-widget-item", this.modifyWidget)
      }

    }
  }


  onCancelClick() {
    this.$emit("cancel-create-widget")
  }

  beforeMount() {
    this.modifyWidgetCopy = Object.assign(new LayoutGameEntryItem(null, '', TileSizeEnum.STANDARD), JSON.parse(JSON.stringify(this.modifyWidget)))
  }

  get filteredLayoutTypes() {
    if(this.isModify) {
      if (this.modifyWidget.type && this.modifyWidget.type.valueOf().includes("jackpot")) {
        const isJackpotType = this.modifyWidget.type && this.modifyWidget.type.valueOf().includes("jackpot")
        return this.layoutTypes.filter(layoutType => layoutType.valueOf().includes('jackpot') === isJackpotType)
      }
      if (this.modifyWidget.type && this.modifyWidget.type.valueOf().includes("dfg")) {
        const isDfgType = this.modifyWidget.type && this.modifyWidget.type.valueOf().includes("dfg")
        return this.layoutTypes.filter(layoutType => layoutType.valueOf().includes('dfg') === isDfgType)
      }
    }
    return this.layoutTypes
  }

  translate(text: string) {
    return this.translateService.instant(text)
  }

  buildSnackBarProperties(type: string) {
    switch (type) {
      case "duplicate":
        this.snackbarColour = "error"
        this.snackbarTitle = this.translate("UI_NETWORK_ADMIN.CMS.GAME_ENTRY_LAYOUT_SELECTED_WIDGET.WIDGET.NO_DUPLICATES.MESSAGE")
        this.snackbar = true
        break
    }
  }
}
</script>

<style scoped>

</style>

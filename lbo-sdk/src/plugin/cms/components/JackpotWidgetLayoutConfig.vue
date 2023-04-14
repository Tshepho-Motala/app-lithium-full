<template>
  <v-card data-test-id="game-entry-layout">
    <v-card-title>{{ translate("UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.HEADER.TITLE") }}</v-card-title>
    <v-card-text>
      <div v-if="modifyWidgetCopy">
        <v-form ref="form" lazy-validation>
          <v-text-field data-test-id="txt-jackpot-logo" :label="translate('UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.JACKPOT_LOGO.LABEL')" v-model="modifyWidgetCopy.jackpotLogo">
          </v-text-field>
          <v-textarea data-test-id="txt-description" :label="translate('UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.DESCRIPTION.LABEL')" v-model="modifyWidgetCopy.description">
          </v-textarea>
          <v-select data-test-id="slt-title-size"
                    :label="translate('UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.PROGRESSIVES.LABEL')"
                    :items="progressivesList"
                    :multiple="true"
                    item-text="progressiveId"
                    :rules="validationRules.progressives"
                    v-model="selectedProgressives"
                    return-object>
          </v-select>
          <v-select v-if="modifyWidgetCopy.tileWidgetType === jackpotTileType"
                    :label="translate('UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.WIDGET_LINK.LABEL')"
                    v-model="defaultSelectedWidgetLink"
                    @change="onWidgetLinkChange"
                    :items="progressiveWidgets"
                    item-text="widget.title"
                    return-object>
          </v-select>
          <v-text-field v-if="modifyWidgetCopy.widgetLink"
                        data-test-id="txt-jackpot-widget-link-title"
                        :label="translate('UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.WIDGET_LINK_TITLE.LABEL')"
                        v-model="modifyWidgetCopy.widgetLink.text">
          </v-text-field>
        </v-form>
      </div>
    </v-card-text>
    <v-card-actions>
      <v-btn data-test-id="btn-cancel" text @click="onCancelClick">
        {{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.CANCEL') }}
      </v-btn>
      <v-spacer></v-spacer>
      <v-btn data-test-id="btn-save" color="primary" @click="onSaveClick">
        {{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.SAVE') }}
      </v-btn>
    </v-card-actions>
    <div class="text-center">
      <v-snackbar v-model="snackbar" :color="snackbarColour" :right="true"
      >{{ snackbarTitle }}
        <template v-slot:action="{ attrs }">
          <v-btn data-test-id="btn-close" color="black" text v-bind="attrs" @click="snackbar = false">
            <v-icon dark>mdi-close</v-icon>
          </v-btn>
        </template>
      </v-snackbar>
    </div>
  </v-card>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from 'vue-property-decorator'
import LobbyItem from "@/plugin/cms/models/LobbyItem"
import LayoutBuilderWidget from "@/plugin/cms/components/LayoutBuilderWidget.vue"
import {TileSizeEnum} from "@/plugin/cms/models/LayoutGameEntryItem"
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface"
import {TileWidgetTypeEnum} from "@/plugin/cms/models/TileWidgetTypeEnum"
import LayoutJackpotGameEntryItem from '../models/LayoutJackpotGameEntryItem'
import LayoutProgressiveGameItem from '../models/LayoutProgressiveGameItem'
import Lobby from '../models/Lobby'
import ProgressiveJackpotBalanceInterface, {
  ProgressiveJackpotGameBalanceInterface
} from "@/plugin/cms/interfaces/ProgressiveJackpotBalanceInterface"
import {RootScopeInterface} from "@/core/interface/ScopeInterface"
import GameItemInterface from "@/plugin/cms/interfaces/GameItemInterface"
import LayoutGameItem from "@/plugin/cms/models/LayoutGameItem"

@Component({
  components: {
    LayoutBuilderWidget
  }
})
export default class JackpotWidgetLayoutConfig extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Inject('rootScope') readonly rootScope!: RootScopeInterface

  @Prop({required: true}) lobbyItem!: LobbyItem
  @Prop({required: true}) selectedLobby!: Lobby
  @Prop({required: true}) selectedDomain!: string
  @Prop({required: true}) selectedChannel!: string
  @Prop({required: true}) modifyWidget!: LayoutJackpotGameEntryItem
  @Prop() isModify!: boolean
  snackbar: boolean = false
  snackbarColour: string = ''
  snackbarTitle: string = ''
  modifyWidgetCopy: LayoutJackpotGameEntryItem | null = null
  defaultSelectedWidgetLink = {}

  tileSizeList = [TileSizeEnum.STANDARD, TileSizeEnum.CUSTOM]
  tileSize: TileSizeEnum = TileSizeEnum.STANDARD

  jackpotTileType: string = TileWidgetTypeEnum.JACKPOT_TILE.valueOf()

  progressivesList: ProgressiveJackpotBalanceInterface[] = []

  selectedProgressives: ProgressiveJackpotBalanceInterface[] = []

  get progressiveWidgets() {
    const jackpotWidgets: any[] = []
    this.selectedLobby.lobbyItems.forEach(lobbyItem => {
      lobbyItem.page.widgets.forEach(widget => {
        if (widget.tileWidgetType && widget.tileWidgetType.valueOf() === TileWidgetTypeEnum.JACKPOT_GRID.valueOf()) {
          const jackpotWidgetLink = {subNav: lobbyItem.page.secondary_nav_code , widget: widget}
          if(this.modifyWidget.widgetLink && jackpotWidgetLink.widget.id === this.modifyWidget.widgetLink.link) {
            this.defaultSelectedWidgetLink = jackpotWidgetLink
          }
          jackpotWidgets.push(jackpotWidgetLink)
        }
      })
    })
    return jackpotWidgets
  }

  get validationRules() {
    return {
      progressives: [
        data =>
        {
          if (this.modifyWidgetCopy && this.modifyWidgetCopy.tileWidgetType?.valueOf() === TileWidgetTypeEnum.JACKPOT_TILE.valueOf())
          {
            return (data.length > 0 && data.length <= 3) || this.translate('UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.PROGRESSIVES.VALIDATION.MAX_ERROR_MESSAGE')
          }
          return data.length > 0 || this.translate('UI_NETWORK_ADMIN.CMS.JACKPOT_WIDGET_LAYOUT_CONFIG.FIELDS.PROGRESSIVES.VALIDATION.MIN_ERROR_MESSAGE')
        }
      ]
    }
  }

  onWidgetLinkChange(data: any) {
    if (this.modifyWidgetCopy) {
      this.modifyWidgetCopy.widgetLink = {
        link: data.widget.id,
        text: data.widget.title,
        secondary_nav_code: data.subNav
      }
    }
  }

  onSaveClick() {
    if (!(this.$refs.form as any).validate()) {
      return
    }
    if (!this.modifyWidgetCopy) {
      return
    }
    if (!this.modifyWidgetCopy.progressives) {
      this.modifyWidgetCopy.progressives = []
    } else {
      this.modifyWidgetCopy.progressives = this.modifyWidgetCopy.progressives.filter(mProgressives => this.selectedProgressives.some(newSelection => newSelection.progressiveId === mProgressives.progressiveId))
      const newProgressives = this.selectedProgressives.filter(mProgressives => !this.modifyWidgetCopy?.progressives.some(newSelection => newSelection.progressiveId === mProgressives.progressiveId))
      newProgressives.forEach(newSelected => {
        this.modifyWidgetCopy?.progressives.push(this.buildProgressive(newSelected))
      })
      if (this.modifyWidgetCopy && this.modifyWidgetCopy.tileWidgetType?.valueOf() === TileWidgetTypeEnum.JACKPOT_TILE.valueOf()) {
        if (JSON.stringify(this.modifyWidget.progressives) !== JSON.stringify(this.modifyWidgetCopy.progressives)) {
          this.modifyWidgetCopy.tiles = []
        }
      } else {
        this.modifyWidgetCopy.tiles = []
      }
    }
    if (this.modifyWidgetCopy && this.modifyWidgetCopy.tileWidgetType?.valueOf() === TileWidgetTypeEnum.JACKPOT_GRID.valueOf()) {
      this.modifyWidgetCopy.widgetLink = null
    }

    Object.assign(this.modifyWidget, this.modifyWidgetCopy)
    this.modifyWidget.type = this.modifyWidget.getType
    if (!this.modifyWidget.showWidgetEntries) {
      this.modifyWidget.tiles = []
    }
    if (this.modifyWidget.tileWidgetType?.valueOf() === TileWidgetTypeEnum.JACKPOT_GRID.valueOf()) {
      this.rootScope.provide.gamesProvider.getGamesByDomainAndEnabled(this.selectedDomain, true, true, this.selectedChannel).then(games => {
        this.rootScope.provide.progressiveFeedsProvider.findProgressiveJackpotGameFeedsByDomain(this.selectedDomain).then((progressiveBalances: ProgressiveJackpotGameBalanceInterface[]) => {
          if (this.modifyWidget.progressives) {
            progressiveBalances = progressiveBalances.filter(pb => this.modifyWidget.progressives.some(wp => wp.progressiveId === pb.progressiveId))
            games = games.filter(game => progressiveBalances.some(pb => pb.game.guid === game.guid))
            this.modifyWidget.tiles = []
            games.forEach(game => {
              this.modifyWidget.tiles.push(this.buildTile(game))
            })
          }
          if (this.isModify) {
            this.$emit("modify-widget", this.modifyWidget)
          } else {
            this.$emit("save-widget-item", this.modifyWidget)
          }
          this.clearText()
        })
      })
    } else {
      if (this.isModify) {
        this.$emit("modify-widget", this.modifyWidget)
      } else {
        this.$emit("save-widget-item", this.modifyWidget)
      }
      this.clearText()
    }
  }

  private buildTile(game: GameItemInterface): LayoutGameItem {
    const layoutGameItem = new LayoutGameItem(game.commercialName, game.guid)
    layoutGameItem.image = game.cdnImageUrl
    return layoutGameItem
  }

  populateJackpotGameBalanceList() {
    this.rootScope.provide.progressiveFeedsProvider.findProgressiveJackpotFeedsByDomain(this.selectedDomain).then((progressiveJackpotFeeds: ProgressiveJackpotBalanceInterface[]) => {
      this.progressivesList = progressiveJackpotFeeds
      if (this.modifyWidget.progressives) {
        this.selectedProgressives = this.progressivesList.filter(item => this.modifyWidget.progressives.some(subItem => subItem.progressiveId === item.progressiveId))
      }
    })
  }

  onCancelClick() {
    this.clearText()
    this.$emit("onCancel")
  }

  private clearText() {
    this.isModify = false
  }

  beforeMount() {
    this.populateJackpotGameBalanceList()
    this.modifyWidgetCopy = Object.assign(new LayoutJackpotGameEntryItem(null, '', TileSizeEnum.STANDARD), JSON.parse(JSON.stringify(this.modifyWidget)))
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

  private buildProgressive(progressive: ProgressiveJackpotBalanceInterface): LayoutProgressiveGameItem {
    const progressiveGameItem = new LayoutProgressiveGameItem()
    progressiveGameItem.progressiveId =  progressive.progressiveId
    return progressiveGameItem
  }
}
</script>

<style scoped>

</style>

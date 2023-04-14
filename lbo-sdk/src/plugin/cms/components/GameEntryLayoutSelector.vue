<template>
  <div>
  <v-card data-test-id="game-entry-layout">
    <v-card-title> {{ dialogTitle }} </v-card-title>
    <v-card-text v-if="modifyWidgetCopy">
       <standard-widget-layout-config :isModify="isModify" :lobbyItem="lobbyItem" :modifyWidget="modifyWidgetCopy"/>
    </v-card-text>
    <v-card-actions>
      <v-btn data-test-id="btn-cancel" text @click="onCancelClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.CANCEL') }}</v-btn>
      <v-spacer></v-spacer>
      <v-btn data-test-id="btn-save" color="primary" @click="onSaveClick">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.SAVE') }}</v-btn>
    </v-card-actions>
    <div class="text-center">
      <v-snackbar v-model="snackbar" :color="snackbarColour" :right="true"
      >{{ snackbarTitle }}<template v-slot:action="{ attrs }">
          <v-btn data-test-id="btn-close" color="black" text v-bind="attrs" @click="snackbar = false">
            <v-icon dark>mdi-close</v-icon>
          </v-btn>
        </template>
      </v-snackbar>
    </div>
  </v-card>
  </div>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from 'vue-property-decorator'
import LobbyItem from "@/plugin/cms/models/LobbyItem"
import LayoutBuilderWidget from "@/plugin/cms/components/LayoutBuilderWidget.vue"
import StandardWidgetLayoutConfig from "@/plugin/cms/components/StandardWidgetLayoutConfig.vue"
import LayoutGameEntryItem, {TileSizeEnum} from "@/plugin/cms/models/LayoutGameEntryItem"
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface"
import {TileWidgetTypeEnum} from "@/plugin/cms/models/TileWidgetTypeEnum"
import LayoutJackpotGameEntryItem from '../models/LayoutJackpotGameEntryItem'
import LayoutDfgGameEntryItem from "@/plugin/cms/models/LayoutDfgGameEntryItem"

@Component({
  components: {
    LayoutBuilderWidget,
    StandardWidgetLayoutConfig
  }
})
export default class GameEntryLayoutSelector extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  @Prop({required: true}) lobbyItem!: LobbyItem
  @Prop() dialogTitle!: string
  @Prop({required: true}) modifyWidget!: LayoutGameEntryItem | LayoutJackpotGameEntryItem | LayoutDfgGameEntryItem
  snackbar: boolean = false
  snackbarColour: string = ''
  snackbarTitle: string = ''
  isModify: boolean = false
  modifyWidgetCopy: LayoutGameEntryItem | null = null
  showJackpotDetailsDialog: boolean = false
  showDfgDetailsDialog : boolean = false

  onSaveClick() {
    if (this.modifyWidgetCopy !== null) {
      if (this.modifyWidgetCopy?.tileWidgetType == null) {
        this.buildSnackBarProperties("invalid")
        return
      }
      if (this.modifyWidgetCopy.tileWidgetType === TileWidgetTypeEnum.RECENTLY_PLAYED_GAMES) {
        if (this.lobbyItem.page.widgets.filter(widget => {
          if (widget !== this.modifyWidget &&  widget.tileWidgetType === TileWidgetTypeEnum.RECENTLY_PLAYED_GAMES) {
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
          if (widget !== this.modifyWidget &&  widget.tileWidgetType === TileWidgetTypeEnum.ATOZ) {
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
          if (widget !== this.modifyWidget &&  widget.tileWidgetType === TileWidgetTypeEnum.TAGGED_GAMES) {
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
          if (widget !== this.modifyWidget &&  widget.tileWidgetType === TileWidgetTypeEnum.RECOMMENDED_GAMES) {
            return true
          }
          return false
        }).length > 0) {
          this.buildSnackBarProperties("duplicate")
          return
        }
      }

      if (this.modifyWidgetCopy.tileWidgetType === TileWidgetTypeEnum.DFG) {
        if (this.lobbyItem.page.widgets.filter(widget => {
          if (widget !== this.modifyWidget &&  widget.type === TileWidgetTypeEnum.DFG) {
            return true
          }
          return false
        }).length > 0) {
          this.buildSnackBarProperties("duplicate")
          return
        }
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
      this.clearText()

    }
  }

  onCancelClick() {
    this.clearText()
    this.$emit("cancel-create-widget")
  }

  private clearText() {
    this.isModify = false
  }

  beforeMount() {
    if (this.modifyWidget.type) {
      this.isModify = true
    }
    this.modifyWidgetCopy = Object.assign(new LayoutGameEntryItem(null, '', TileSizeEnum.STANDARD), JSON.parse(JSON.stringify(this.modifyWidget)))
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

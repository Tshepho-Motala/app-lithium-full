<template>
  <v-row no-gutters data-test-id="cnt-layout-builder">
    <v-dialog
      max-width="500"
      v-model="showWidgetItemCreatorDialog"
    >
      <GameEntryLayoutSelector v-if="showWidgetItemCreatorDialog" :modifyWidget="modifyWidget" :lobbyItem="lobbyItem" :dialogTitle=dialogTitle @cancel-create-widget="onCancelWidgetCreateClick" @save-widget-item="onSaveWidgetClick" @modify-widget="onModifyWidgetClick" />
    </v-dialog>
    <v-dialog title="Jackpot Widget" max-width="700" v-model="showJackpotDetailsDialog">
      <jackpot-widget-layout-config v-if="showJackpotDetailsDialog" :isModify="isEditWidget" :selectedChannel="selectedChannel" :selectedDomain="selectedDomain" :lobbyItem="lobbyItem" :modifyWidget="modifyWidget" :selectedLobby="selectedLobby" @save-widget-item="onSaveJackpotWidget" @onCancel="onCancelJackpotWidgetCreate" @modify-widget="onModifyJackpotWidgetClick"/>
    </v-dialog>
    <v-dialog title="Dfg Widget" max-width="700" v-model="showDfgDetailsDialog">
      <dfg-widget-layout-config v-if="showDfgDetailsDialog" :isModify="isEditWidget" :selectedChannel="selectedChannel" :selectedDomain="selectedDomain" :lobbyItem="lobbyItem" :modifyWidget="modifyWidget" :selectedLobby="selectedLobby" @save-widget-item="onSaveDfgWidget" @onCancel="onCancelDfgWidgetCreate" @modify-widget="onModifyDfgWidgetClick"/>
    </v-dialog>
    <div class="text-center">
      <v-snackbar v-model="snackbar" :color="snackbarColour" :right="true"
      >{{ snackbarTitle
        }}<template v-slot:action="{ attrs }">
          <v-btn data-test-id="btn-close" color="black" text v-bind="attrs" @click="snackbar = false">
            <v-icon dark>mdi-close</v-icon>
          </v-btn>
        </template>
      </v-snackbar>
    </div>
    <!-- LIVE TILES -->
    <v-col cols="12">
      <v-expansion-panels focusable>
        <draggable v-model="lobbyItem.page.widgets" class="col col-12" draggable=".page-widgets">
          <v-expansion-panel v-for="(entry, i) in widgets" :key="`item-${i}`" cols="12" class="pa-1 page-widgets">
          <v-expansion-panel-header>
            <v-row>
            {{entry.type ? entry.title+' ['+entry.type +']': translate('UI_NETWORK_ADMIN.CMS.LAYOUT_BUILDER_WIDGET.OUTPUT.BANNER_ENTRY.TITLE') }} <v-spacer></v-spacer>
              <v-icon data-test-id="btn-delete-widget" float:right small class="mr-2" color="red" @click="deleteWidgets(entry)"> mdi-delete </v-icon>
              <v-icon data-test-id="btn-edit-widget" small class="mr-2" @click="editWidget(entry)"> mdi-pencil-box-outline </v-icon>
            </v-row>
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            <LayoutBuilderGrid  :selectedDomain="selectedDomain" :selectedChannel="selectedChannel" :entry="entry" :lobbyItem="lobbyItem" :allowTitle="entry.type" />
          </v-expansion-panel-content>
          </v-expansion-panel>
        </draggable>
      </v-expansion-panels>
    </v-col>

    <!-- NEW TILE -->
    <v-col @click="onNewItemClicked" cols="12" class="pa-1 pt-4">
      <LayoutBuilderWidgetTemplate />

    </v-col>
  </v-row>
</template>

<script lang='ts'>
import {Component, Inject, Prop, Vue} from 'vue-property-decorator'
import LobbyItem from '../models/LobbyItem'
import LayoutBuilderGrid from './LayoutBuilderGrid.vue'
import LayoutBuilderWidgetTemplate from './LayoutBuilderWidgetTemplate.vue'
import GameEntryLayoutSelector from "@/plugin/cms/components/GameEntryLayoutSelector.vue"
import LayoutGameEntryItem, {TileSizeEnum} from "@/plugin/cms/models/LayoutGameEntryItem"
import {LayoutWidgetEntryTypeEnum} from "@/plugin/cms/models/LayoutGameEntryTypeEnum"
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface"
import ListenerServiceInterface from "@/core/interface/service/ListenerServiceInterface"
import draggable from "vuedraggable"
import {TileWidgetTypeEnum} from "@/plugin/cms/models/TileWidgetTypeEnum"
import JackpotWidgetLayoutConfig from './JackpotWidgetLayoutConfig.vue'
import Lobby from '../models/Lobby'
import {nanoid} from "nanoid"
import LayoutJackpotGameEntryItem from '../models/LayoutJackpotGameEntryItem'
import DfgWidgetLayoutConfig from "@/plugin/cms/components/DfgWidgetLayoutConfig.vue"

@Component({
  components: {
    GameEntryLayoutSelector,
    LayoutBuilderGrid,
    LayoutBuilderWidgetTemplate,
    JackpotWidgetLayoutConfig,
    DfgWidgetLayoutConfig,
    draggable
  }
})
export default class LayoutBuilderWidget extends Vue {
  @Inject('listenerService') readonly listenerService!: ListenerServiceInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  @Prop({required: true}) lobbyItem!: LobbyItem
  @Prop() selectedDomain!: string
  @Prop() selectedChannel!: string
  @Prop({required: true}) selectedLobby!: Lobby
  @Prop() snackbar: boolean = false
  @Prop() snackbarColour: string = ''
  @Prop() snackbarTitle: string = ''


  showWidgetItemCreatorDialog = false
  isEditWidget: boolean = false
  dialogTitle: string = ''
  modifyWidget: LayoutGameEntryItem | null = null
  modifyWidgetOriginal: LayoutGameEntryItem | null = null

  showJackpotDetailsDialog = false
  showDfgDetailsDialog : boolean = false

  showWidgetTypeSelectorMenu = false
  widgetTypeMenuX = 0
  widgetTypeMenuY = 0
  widgetType = null

  get widgets(): LayoutGameEntryItem[] {
    return this.lobbyItem.page.widgets.filter(widget => widget.type !== LayoutWidgetEntryTypeEnum.BANNER.valueOf())
  }

  onNewItemClicked() {
    this.modifyWidget = new LayoutGameEntryItem(null, '', TileSizeEnum.STANDARD)
    this.isEditWidget = false
    this.dialogTitle = this.translate("UI_NETWORK_ADMIN.CMS.LAYOUT_BUILDER_WIDGET.OUTPUT.CREATE_WIDGET_DIALOG.TITLE")
    this.showWidgetItemCreatorDialog = true
  }

  onCancelWidgetCreateClick() {
    this.showWidgetItemCreatorDialog = false
    this.clearText()
  }

  onSaveWidgetClick() {
    if (this.modifyWidget?.tileWidgetType == null) {
      this.buildSnackBarProperties("invalid")
      return
    }
    this.modifyWidget.type = this.modifyWidget.getType
    if (!this.modifyWidget.showWidgetEntries) {
      this.modifyWidget.tiles = []
    }

    if (this.modifyWidget?.tileWidgetType?.valueOf().includes('jackpot')) {
      this.showJackpotDetailsDialog = true
      this.showWidgetItemCreatorDialog = false
      return
    }

    if (this.modifyWidget?.tileWidgetType?.valueOf().includes('dfg')) {
      this.showDfgDetailsDialog = true
      this.showWidgetItemCreatorDialog = false
      return
    }

    this.lobbyItem.page.widgets.push(this.modifyWidget)
    this.isEditWidget = false
    this.showWidgetItemCreatorDialog = false
    this.clearText()
  }

  onSaveJackpotWidget() {
    if (this.modifyWidget?.tileWidgetType?.valueOf() === TileWidgetTypeEnum.JACKPOT_GRID) {
      (this.modifyWidget as LayoutJackpotGameEntryItem).id = nanoid()
    }
    this.lobbyItem.page.widgets.push(this.modifyWidget)
    this.isEditWidget = false
    this.showJackpotDetailsDialog = false
    this.clearText()
  }

  onSaveDfgWidget() {
    this.lobbyItem.page.widgets.push(this.modifyWidget);
    this.isEditWidget = false
    this.showDfgDetailsDialog = false
    this.clearText()
  }

  deleteWidgets(widget: LayoutGameEntryItem) {
    this.listenerService.call('dialog-confirm', {
      title: this.translate('UI_NETWORK_ADMIN.CMS.GLOBAL.DIALOG.CONFIRM_DELETE_WIDGET.TITLE'),
      text: this.translate('UI_NETWORK_ADMIN.CMS.GLOBAL.DIALOG.CONFIRM_DELETE_WIDGET.TEXT') + ' ' + widget.type + '?',
      btnPositive: {
        text: 'Delete',
        color: 'error',
        onClick: () => {
          let index = this.lobbyItem.page.widgets.findIndex((x) => x === widget)
          if (this.lobbyItem.page.widgets[index] === widget) {
            this.lobbyItem.page.widgets.splice(index, 1)
          } else {
            let indexAfter = this.lobbyItem.page.widgets.indexOf(widget)
            this.lobbyItem.page.widgets.splice(indexAfter, 1)
          }
        }
      }
    })
  }

  editWidget(item: LayoutGameEntryItem) {
    this.isEditWidget = true
    this.dialogTitle = this.translate("UI_NETWORK_ADMIN.CMS.LAYOUT_BUILDER_WIDGET.OUTPUT.MODIFY_WIDGET_DIALOG.TITLE")
    this.modifyWidget = item
    if (this.modifyWidget.tileWidgetType === null && this.modifyWidget.type) {
      for (let key in TileWidgetTypeEnum) {
        if (TileWidgetTypeEnum[key].valueOf() === this.modifyWidget.type.valueOf()) {
          this.modifyWidget.tileWidgetType = TileWidgetTypeEnum[key]
        }
      }
    }
    this.showWidgetItemCreatorDialog = true
  }

  onModifyWidgetClick() {
    this.showWidgetItemCreatorDialog = false
    if (this.modifyWidget?.tileWidgetType?.valueOf().includes('jackpot')) {
      this.showJackpotDetailsDialog = true
      return
    }
    if (this.modifyWidget?.tileWidgetType?.valueOf().includes('dfg')) {
      this.showDfgDetailsDialog = true
      return
    }
    this.isEditWidget = false
    this.buildSnackBarProperties('success')
    this.clearText()
  }

  onModifyJackpotWidgetClick() {
    this.isEditWidget = false
    this.buildSnackBarProperties('success')
    this.showJackpotDetailsDialog = false
    this.clearText()
  }

  onModifyDfgWidgetClick() {
    this.isEditWidget = false
    this.buildSnackBarProperties('success')
    this.showDfgDetailsDialog = false
    this.clearText()
  }

  private clearText() {
    this.modifyWidget = null
  }

  translate(text: string) {
    return this.translateService.instant(text)
  }

  buildSnackBarProperties(type: string) {
    switch (type) {
      case "invalid":
        this.snackbarColour = "error"
        this.snackbarTitle = this.translate("UI_NETWORK_ADMIN.CMS.LAYOUT_BUILDER_WIDGET.SAVE_WIDGET.EMPTY_INPUT")
        this.snackbar = true
        break
      case "success":
        this.snackbarColour = "success"
        this.snackbarTitle = this.translate("UI_NETWORK_ADMIN.CMS.LAYOUT_BUILDER_WIDGET.OUTPUT.MODIFY_WIDGET_DIALOG.SUCCESS")
        this.snackbar = true
        break
    }
  }

  onCancelJackpotWidgetCreate() {
    this.showJackpotDetailsDialog = false
    this.clearText()
  }

  onCancelDfgWidgetCreate() {
    this.showDfgDetailsDialog = false
    this.clearText()
  }

}
</script>



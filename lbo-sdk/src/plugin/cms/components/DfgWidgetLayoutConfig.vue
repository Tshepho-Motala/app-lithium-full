<template>
  <v-card data-test-id="game-entry-layout">
    <v-card-title>{{ translate('UI_NETWORK_ADMIN.CMS.DFG_WIDGET_LAYOUT_CONFIG.DESCRIPTION.DETAILS.TITLE') }}</v-card-title>
    <v-card-text>
      <div v-if="modifyWidgetCopy">
        <v-text-field data-test-id="txt-description-1" :label="translate('UI_NETWORK_ADMIN.CMS.GAME_ENTRY_LAYOUT_SELECTOR_FIELD.DFG_WIDGET.DESCRIPTION.MAIN_WIDGET_TEXT')" v-model="modifyWidgetCopy.description.mainWidgetText">
        </v-text-field>
        <v-text-field data-test-id="txt-description-2" :label="translate('UI_NETWORK_ADMIN.CMS.GAME_ENTRY_LAYOUT_SELECTOR_FIELD.DFG_WIDGET.DESCRIPTION.WIDGET_SUB_TEXT')" v-model="modifyWidgetCopy.description.widgetSubText">
        </v-text-field>
        <v-text-field data-test-id="txt-description-3" :label="translate('UI_NETWORK_ADMIN.CMS.GAME_ENTRY_LAYOUT_SELECTOR_FIELD.DFG_WIDGET.DESCRIPTION.MONTHLY_FREE_GAME_TEXT')" v-model="modifyWidgetCopy.description.monthlyFreeGameText">
        </v-text-field>
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
import Lobby from '../models/Lobby'
import {RootScopeInterface} from "@/core/interface/ScopeInterface"
import LayoutDfgGameEntryItem from "@/plugin/cms/models/LayoutDfgGameEntryItem"
import DfgWidgetDescriptions from '../models/DfgWidgetDescriptions'


@Component({
  components: {
    LayoutBuilderWidget
  }
})
export default class DfgWidgetLayoutConfig extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface;
  @Inject('rootScope') readonly rootScope!: RootScopeInterface

  @Prop({required: true}) lobbyItem!: LobbyItem
  @Prop({required: true}) selectedLobby!: Lobby
  @Prop({required: true}) selectedDomain!: string
  @Prop({required: true}) selectedChannel!: string
  @Prop({required: true}) modifyWidget!: LayoutDfgGameEntryItem
  @Prop() isModify!: boolean
  snackbar: boolean = false
  snackbarColour: string = ''
  snackbarTitle: string = ''
  modifyWidgetCopy: LayoutDfgGameEntryItem | null = null

  onSaveClick() {
    if (!this.modifyWidgetCopy) {
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
      this.clearText()
    }

  onCancelClick() {
    this.clearText()
    this.$emit("onCancel")
  }

  private clearText() {
    this.isModify = false
  }

  beforeMount() {
    this.modifyWidgetCopy = Object.assign(new LayoutDfgGameEntryItem(null, '', TileSizeEnum.STANDARD), JSON.parse(JSON.stringify(this.modifyWidget)))
    if (this.modifyWidgetCopy && !this.modifyWidgetCopy.description ) {
      this.modifyWidgetCopy.description = new DfgWidgetDescriptions()
    }
  }

  translate(text: string) {
    return this.translateService.instant(text)
  }

}
</script>

<style scoped>

</style>

<template>
  <v-row data-test-id="cnt-layout-selector" v-if="hasLobbyItem && lobbySelected">
    <v-col cols="12">
      <v-icon>mdi-information</v-icon> {{ translate('UI_NETWORK_ADMIN.CMS.LAYOUT_SELECTOR.OUTPUT.WIDGET_MANAGEMENT.TITLE') }}
    </v-col>
    <v-col cols="12">
      <LayoutBuilderWidget :selectedDomain="selectedDomain" :selectedChannel="selectedChannel" :lobbyItem="lobbyItem" :selectedLobby="selectedLobby" />
    </v-col>
  </v-row>
  <div v-else-if="!hasLobbyItem" class="text-center pa-4">
    <span class="grey--text">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.OUTPUT.SELECT_SUBNAVIGATION_MESSAGE') }}</span>
  </div>
  <div v-else class="text-center pa-4">
    <span class="grey--text">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.OUTPUT.SELECT_LOBBY_MESSAGE') }}</span>
  </div>
</template>

<script lang='ts'>
import { Component, Inject, Prop, Vue } from 'vue-property-decorator'
import LayoutType from '../models/LayoutType'
import LobbyItem from '../models/LobbyItem'
import LayoutBuilderGrid from './LayoutBuilderGrid.vue'
import LayoutBuilderWidget from './LayoutBuilderWidget.vue'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface"
import SubNavItem from "@/plugin/cms/models/SubNavItem"
import DomainItem from "@/plugin/cms/models/DomainItem"
import ChannelItem from "@/plugin/cms/models/ChannelItem"
import Lobby from '../models/Lobby'

@Component({
  components: {
    LayoutBuilderGrid,
    LayoutBuilderWidget
  }
})
export default class LayoutSelector extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  @Prop() lobbyItem!: LobbyItem
  @Prop() lobbySelected!: boolean
  @Prop({required: true}) selectedLobby!: Lobby
  @Prop() selectedSubNav!: string
  @Prop() selectedDomain!: string
  @Prop() selectedChannel!: string

  layoutType: LayoutType[] = [new LayoutType(1, 'Widget')]
  selectedLayout: LayoutType | null = null

  get hasLobbyItem(): boolean {
    return !!this.lobbyItem
  }

  get isWidget(): boolean {
    if (this.selectedLayout === null) {
      return false
    }
    return this.selectedLayout.id === 1
  }

  onChange(value: LayoutType) {
    this.selectedLayout = value
    this.lobbyItem.page.layout = value.title
  }

  // onSubNavChange(subNav: SubNavItem) {
  //   if(subNav.code !== this.lobbyItem.page.secondary_nav_code)
  //     return this.$emit('onSubNavSelect', subNav.code)
  // }

  translate(text: string) {
    return this.translateService.instant(text)
  }
}
</script>

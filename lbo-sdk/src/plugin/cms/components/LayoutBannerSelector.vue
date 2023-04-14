<template>
  <v-row v-if="hasLobbyItem && lobbySelected">
    <v-col cols="12">
      <LayoutBuilderBanner :selectedLobby="selectedLobby" :lobbyItem="lobbyItem" :selectedDomain="selectedDomain" :selectedChannel="selectedChannel" />
    </v-col>
  </v-row>
  <div v-else-if="!hasLobbyItem" class="text-center pa-4">
    <span class="grey--text">{{translate("UI_NETWORK_ADMIN.CMS.GLOBAL.OUTPUT.SELECT_SUBNAVIGATION_MESSAGE")}}</span>
  </div>
  <div v-else class="text-center pa-4">
    <span class="grey--text">{{translate("UI_NETWORK_ADMIN.CMS.GLOBAL.OUTPUT.SELECT_LOBBY_MESSAGE")}}</span>
  </div>
</template>

<script lang='ts'>
import {Component, Inject, Prop, Vue} from 'vue-property-decorator'
import LayoutType from '../models/LayoutType'
import LobbyItem from '../models/LobbyItem'
import LayoutBuilderBanner from './LayoutBuilderBanner.vue'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import LayoutBuilderGridBanner from "@/plugin/cms/components/LayoutBuilderGridBanner.vue";
import Lobby from '../models/Lobby';

@Component({
  components: {
    LayoutBuilderGridBanner,
    LayoutBuilderBanner
  }
})
export default class LayoutBannerSelector extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface;

  @Prop() lobbyItem!: LobbyItem
  @Prop() lobbySelected!: boolean
  @Prop({required: true}) selectedLobby!: Lobby;
  @Prop() selectedSubNav!: string;
  @Prop() selectedDomain!: string;
  @Prop() selectedChannel!: string;


  layoutType: LayoutType[] = [new LayoutType(1, 'Banner')]
  selectedLayout: LayoutType | null = null
  postId: any
  errorMessage: any

  get hasLobbyItem(): boolean {
    return !!this.lobbyItem;
  }

  get isBanner(): boolean {
    if (this.selectedLayout === null) {
      return false
    }
    return this.selectedLayout.id === 1
  }

  onChange(value: LayoutType) {
    this.selectedLayout = value
    this.lobbyItem.page.layout = value.title;
  }

  translate(text: string) {
    return this.translateService.instant(text);
  }
}
</script>

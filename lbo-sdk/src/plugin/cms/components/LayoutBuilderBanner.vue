<template>
  <v-row no-gutters>
    <v-col cols="12" v-if="pageBanners">
      <v-row>
        <v-col cols="12" md="12">
          <v-alert
              color="red lighten-2"
              dark
              v-if="requiresReodering"
          >
            <v-icon dark>mdi-information</v-icon>
            {{ translate("UI_NETWORK_ADMIN.CMS.LAYOUT_BUILDER_BANNER.MESSAGES.UPDATE_POSITIONS") }}
          </v-alert>
        </v-col>
        <v-col cols="12" md="12" class="text-center">
          <LayoutBuilderBannerTemplate :disableAdd="pageBannersListChanged || requiresReodering" :lobbyItem="lobbyItem"
                                       :selectedDomain="selectedDomain"
                                       :selectedChannel="selectedChannel"
                                       :lobbyNotPublished="lobbyNotPublished" @onSelect="onBannerSelected"/>
          <v-col cols="12" v-if="lobbyNotPublished">
            <v-icon>mdi-information</v-icon> {{ translate('UI_NETWORK_ADMIN.CMS.BANNER_SELECTION.UNPUBLISHED_LOBBY.WARNING_MESSAGE.TITLE') }}
          </v-col>
        </v-col>
        <v-col cols="12">
          <draggable v-model=" pageBanners" :options="{disabled : requiresReodering}">
            <transition-group class="row" tag="div">
              <v-col cols="12" v-for="(item, i) in pageBanners" :key="`item-${i}`">
                <v-card>
                  <v-card-title>
                    {{ item.banner.name }}
                    <v-btn icon color="pink" @click="onRemovePageBannerClick(item)" dark small absolute right fab>
                      <v-icon>mdi-delete</v-icon>
                    </v-btn>
                  </v-card-title>
                  <v-card-subtitle>
                    <h3>{{ rruleObject(item.banner.recurrencePattern) }}</h3>
                  </v-card-subtitle>
                  <v-card-text>
                    <v-row>
                      <v-col cols="12" md="5">
                        <v-img :src="item.banner.imageUrl" :alt="`${item.banner.name} avatar`"></v-img>
                      </v-col>
                      <v-col cols="12" md="7">
                        <v-row>
                          <v-col cols="12">
                            {{ item.banner.displayText }}
                          </v-col>
                        </v-row>
                        <v-row>
                          <v-col cols="12">
                            {{ displayTime(item.banner.timeFrom, item.banner.timeTo) }}
                          </v-col>
                        </v-row>
                      </v-col>
                    </v-row>
                  </v-card-text>
                </v-card>
              </v-col>
            </transition-group>
          </draggable>

        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" md="6">
          <v-btn :disabled="!pageBannersListChanged || requiresReodering" block color="primary" @click="onRefreshClick">
            {{ translate("UI_NETWORK_ADMIN.CMS.LAYOUT_BUILDER_BANNER.BUTTONS.REFRESH") }}
          </v-btn>
        </v-col>
        <v-col cols="12" md="6">
          <v-btn :disabled="!(pageBannersListChanged || requiresReodering)" block color="primary"
                 @click="onUpdatePageBannerPositionsClick">
            {{  translate("UI_NETWORK_ADMIN.CMS.LAYOUT_BUILDER_BANNER.BUTTONS.UPDATE_POSITIONS") }}
          </v-btn>
        </v-col>
      </v-row>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import {Component, Inject, Prop, Vue, Watch} from 'vue-property-decorator'
import {RRule, rrulestr} from "rrule";
import LobbyItem from '../models/LobbyItem'
import BannerEntryLayoutSelector from "@/plugin/cms/components/BannerEntryLayoutSelector.vue";
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import ListenerServiceInterface from "@/core/interface/service/ListenerServiceInterface";
import LayoutBannerEntryItem from "@/plugin/cms/models/LayoutBannerEntryItem";
import LayoutBannerList from "@/plugin/cms/models/LayoutBannerList";
import LayoutBuilderGridBanner from "@/plugin/cms/components/LayoutBuilderGridBanner.vue";
import LayoutBuilderBannerTemplate from "@/plugin/cms/components/LayoutBuilderBannerTemplate.vue";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import {Banner} from "@/plugin/cms/models/Banner";
import {PageBanner} from "@/plugin/cms/models/PageBanner";
import Lobby from '../models/Lobby';


@Component({
  components: {
    BannerEntryLayoutSelector,
    LayoutBuilderGridBanner,
    LayoutBuilderBannerTemplate
  }
})
export default class LayoutBuilderBanner extends Vue {
  @Inject('listenerService') readonly listenerService!: ListenerServiceInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface;
  @Inject('rootScope') readonly rootScope!: RootScopeInterface

  @Prop({required: true}) lobbyItem!: LobbyItem;
  @Prop({required: true}) selectedLobby!: Lobby;
  @Prop() selectedDomain!: string;
  @Prop() selectedChannel!: string;

  pageBanners: PageBanner[] = [];
  pageBannersOld: PageBanner[] = [];
  pageBannersOldToUpdate: PageBanner[] = [];

  bannerOptions: Banner[] = []

  pageBannersListChanged: boolean = false;
  requiresReodering: boolean = false;
  lobbyNotPublished: boolean = false;

  created() {
    this.$watch('lobbyItem.page.secondary_nav_code', (newValue) => {
      this.getPageBanners();
    })
  }

  mounted() {
    if (this.selectedLobby.id === 0) {
      this.lobbyNotPublished = true;
      return;
    }
    if(this.lobbyItem) {
      this.getPageBanners();
    }
    this.loadDomainBanners();
  }

  getPageBanners() {
    const primaryNavCode = this.lobbyItem.page.primary_nav_code;
      const secondaryNavCode = this.lobbyItem.page.secondary_nav_code;
      if(this.lobbyItem) {
        this.rootScope.provide.casinoCmsProvider.getBannersForPage(this.selectedDomain, this.selectedLobby.id, this.selectedChannel, primaryNavCode, secondaryNavCode)
            .then((pageBannerResults: PageBanner[]) => {
              this.pageBannersListChanged = false;
              this.requiresReodering = false;
              this.pageBannersOldToUpdate = [];
              this.pageBanners = pageBannerResults.sort((p1: PageBanner, p2: PageBanner) => p1.position - p2.position);
              this.pageBannersOld = this.pageBanners;
              this.syncPageBannerPositions();
            })
      }
  }

  loadDomainBanners() {
    if(this.selectedDomain != null) {
      this.rootScope.provide.casinoCmsProvider.getDomainBanners(this.selectedDomain).then((banners: any[]) => {
        this.bannerOptions = banners;
      });
    }
  }

  get bannerWidgetExists(): boolean{
    if (this.lobbyItem.page.gameBanner.entries.length <= 0) {
      return true
    }
    return false
  }

  @Watch('lobbyItem.page.prim_nav_code')
  public (newMsg: string, oldMsg: string) {
    if (this.lobbyItem) {
      this.getPageBanners();
    }
  }

  displayTime(timeFrom, timeTo) {
    if(!timeFrom) return "All Day";
    if(!timeTo) return timeFrom + " - " + "End of Day";
    return timeFrom + " - " + timeTo;
  }

  get banner(): LayoutBannerList {
    return this.lobbyItem.page.gameBanner
  }

  onUpdatePageBannerPositionsClick() {
    this.rootScope.provide.casinoCmsProvider.updatePageBannersPosition(this.selectedDomain, this.selectedLobby.id, this.pageBannersOldToUpdate).then(() => {
      this.getPageBanners();
    })
  }

  onRemovePageBannerClick(pageBanner: PageBanner) {
    this.listenerService.call('dialog-confirm', {
      title: 'Remove Banner',
      text: 'Are you sure you want to remove \'' + pageBanner.banner.name + ' \' banner from \'' + pageBanner.primaryNavCode + ' - \'' + pageBanner.secondaryNavCode + '\' page?',
      btnPositive: {
        text: "Delete",
        color: "error",
        onClick: () => {
          if (pageBanner.id) {
            this.rootScope.provide.casinoCmsProvider.removePageBanner(this.selectedDomain, this.selectedLobby.id, pageBanner.id).then(() => this.getPageBanners());
          }
        }
      }
    });
  }

  @Watch("pageBanners", {deep: true})
  watchPageBanners(newPageBanners) {
    if(this.requiresReodering) {
      return;
    }
    this.pageBannersOldToUpdate = [];
    if (this.pageBannersOld.length === newPageBanners.length) {
      this.pageBannersListChanged = false;
      let i;
      for (i = 0; i < newPageBanners.length; i++) {
        if (newPageBanners[i] !== this.pageBannersOld[i]) {
          newPageBanners[i].position = i + 1;
          this.pageBannersOldToUpdate.push(newPageBanners[i])
          this.pageBannersListChanged = true;
        }
      }
    }

  }

  onBannerSelected(banner: Banner) {
    if(this.pageBanners && banner) {
      const primaryNavCode = this.lobbyItem.page.primary_nav_code;
      const secondaryNavCode = this.lobbyItem.page.secondary_nav_code;
      const position = this.pageBanners.length > 0 ? this.pageBanners[this.pageBanners.length - 1].position + 1 : 1;

      const pageBanner = new PageBanner(null, primaryNavCode, secondaryNavCode, position, this.selectedChannel, banner);
      this.rootScope.provide.casinoCmsProvider.addPageBanner(this.selectedDomain, this.selectedLobby.id, banner.id, pageBanner).then(pageBannerResult => this.getPageBanners());
    }
  }

  translate(text: string) {
    return this.translateService.instant(text);
  }

  rruleObject(rule: string): string {
    return rrulestr(rule).toText();
  }

  onRefreshClick() {
    this.getPageBanners();
  }

  private syncPageBannerPositions() {
    let i;
    this.pageBannersOldToUpdate = [];
    this.requiresReodering = false;
    for (i = 0; i < this.pageBanners.length; i++) {
      if (this.pageBanners[i].position !== (i + 1)) {
        this.pageBanners[i].position = i + 1;
        this.pageBannersOldToUpdate.push(this.pageBanners[i]);
        this.requiresReodering = true;
      }
    }
  }
}
</script>

<style scoped>
.gridItemAdd {
  border: 1px dashed grey;
  border-radius: 5px;
  min-height: 150px;
  width: 100%;
}
</style>

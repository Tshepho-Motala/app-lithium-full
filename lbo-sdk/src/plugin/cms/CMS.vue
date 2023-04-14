<template>
  <v-row no-gutters data-test-id="cnt-cms">
    <v-col cols="12" class="mb-3">
      <DomainSelector
        v-model="domain"
        horizontal
        unlocked
        :roles="['ADMIN', 'CASINO_LOBBIES_VIEW']"
        :title="translate('UI_NETWORK_ADMIN.CMS.OUTPUT.DOMAIN_SELECTOR.TITLE')"
        :description="translate('UI_NETWORK_ADMIN.CMS.OUTPUT.DOMAIN_SELECTOR.DESCRIPTION')"
        @change="onDomainSelected"
      />
    </v-col>

    <v-col cols="12" class="mb-3">
      <ChannelSelector :selectedDomain="selectedDomain" @onSelect="onChannelSelected" />
    </v-col>

    <v-col cols="12">
      <LobbySelector
        :channel="selectedChannel"
        :domain="selectedDomain"
        :loading="lobbiesLoading"
        :lobbies="lobbies"
        @onSelect="onLobbySelected"
        @onSave="onLobbySaved"
        @onPublish="onLobbyPublish"
        @onDelete="onLobbyDelete"
      />
    </v-col>

    <v-col cols="12">
      <v-card>
        <v-tabs data-test-id="cnt-page-items" v-model="lobbyTab">
          <v-tab>Navigation</v-tab>
          <v-tab>Page Builder</v-tab>
          <v-tab>Badge Management</v-tab>
          <v-tab>Banner Selector</v-tab>
          <v-tab>Game Repository</v-tab>
          <v-tab>JSON Output</v-tab>
        </v-tabs>

        <v-tabs-items v-model="lobbyTab">
          <!-- NAVIGATION -->
          <v-tab-item class="pa-4">
            <NavigationManagement
              :lobby="selectedLobby"
              :nav="currentNav"
              @onSaveNavigation="onNavigationChanged"
              @resetNav="resetNav"
              @onSubNavSelect="onSubNavSelect"
            />
          </v-tab-item>

          <!--PAGE BUILDER -->
          <v-tab-item class="pa-4">
            <LayoutSelector
              :lobbyItem="selectedLobbyItem"
              :lobbySelected="lobbySelected"
              :selectedDomain="selectedDomain"
              :selectedChannel="selectedChannel"
              :selectedLobby="selectedLobby"
            />
          </v-tab-item>

          <!-- Badges -->
          <v-tab-item class="pa-4">
            <BadgeManagement :lobby="selectedLobby" />
          </v-tab-item>

          <!-- BANNER SELECTOR -->
          <v-tab-item class="pa-4">
            <LayoutBannerSelector
              :lobbyItem="selectedLobbyItem"
              :selectedLobby="selectedLobby"
              :lobbySelected="lobbySelected"
              :selectedDomain="selectedDomain"
              :selectedChannel="selectedChannel"
            />
          </v-tab-item>

          <!-- GAME REPOSITORY -->
          <v-tab-item class="pa-4">
            <!-- <CMSGames /> -->
            <div class="text-center pa-4">
              <span class="grey--text">Coming Soon</span>
            </div>
          </v-tab-item>

          <!-- JSON OUTPUT -->
          <v-tab-item class="pa-4">
            <!-- <div class="d-flex">
              <div>
                <span class="title">JSON OUTPUT</span>
              </div>
              <v-spacer></v-spacer>
              <div>
                <v-btn icon @click="showJson = !showJson">
                  <v-icon v-if="showJson">mdi-chevron-down</v-icon>
                  <v-icon v-else>mdi-chevron-left</v-icon>
                </v-btn>
              </div>
            </div>
            <v-divider></v-divider> -->
            <div v-if="showJson" data-test-id="cnt-json-output">
              <div v-if="lobbySelected" class="pa-2">
                <v-btn-toggle v-model="outputJsonType">
                  <v-btn data-test-id="btn-full" :value="'full'">
                    {{ translate('UI_NETWORK_ADMIN.CMS.JSON.OUTPUT.FULL_JSON') }}
                    <v-icon @click="copyFullLobbyJsonToClipBoard()">mdi-content-copy</v-icon></v-btn
                  >
                  <v-btn data-test-id="btn-active" :value="'active'">
                    {{ translate('UI_NETWORK_ADMIN.CMS.JSON.OUTPUT.ACTIVE_LOBBY') }}
                    <v-icon @click="copyActiveLobbyJsonToClipBoard()">mdi-content-copy</v-icon></v-btn
                  >
                  <v-btn data-test-id="btn-page" :value="'page'">
                    {{ translate('UI_NETWORK_ADMIN.CMS.JSON.OUTPUT.SELECTED_PAGE') }}
                    <v-icon @click="copyPageJsonToClipBoard()">mdi-content-copy</v-icon></v-btn
                  >
                </v-btn-toggle>
                <pre v-if="outputJsonType === 'active'" v-text="activeLobbyJson"></pre>
                <pre v-if="outputJsonType === 'page'" v-text="selectedPageJson"></pre>
                <pre v-if="outputJsonType === 'full'" v-text="fullJson"></pre>
              </div>
              <div v-else class="text-center pa-2">
                <span class="grey--text">Please select a lobby first</span>
              </div>
            </div>
          </v-tab-item>
        </v-tabs-items>
      </v-card>
    </v-col>
    <v-dialog title="Confirm Publish" max-width="700" v-model="showPublishLobbyDialog">
      <v-card>
        <v-card-title>Are you sure you want to publish the modified lobby make it current?</v-card-title>
        <v-card-text>
          <v-col cols="12">
            <v-textarea label="Revision Description" v-model="description"></v-textarea>
          </v-col>
        </v-card-text>
        <v-card-actions>
          <v-btn text @click="onCancelPublishClick">Cancel</v-btn>
          <v-spacer></v-spacer>
          <v-btn color="primary" @click="onConfirmPublishClick">Confirm</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script lang='ts'>
import {Component, Inject, Vue} from 'vue-property-decorator'
import LobbyItem from '../cms/models/LobbyItem'
import ChannelSelector from '../cms/components/ChannelSelector.vue'
import LobbySelector from '../cms/components/LobbySelector.vue'
import NavCodeMapper from '../cms/components/NavCodeMapper.vue'
import LayoutSelector from '../cms/components/LayoutSelector.vue'
import Lobby from './models/Lobby'
import ChannelItem from '../cms/models/ChannelItem'
import {RootScopeInterface} from '@/core/interface/ScopeInterface'
import SubNavigation from '@/plugin/cms/components/SubNavigation.vue'
import Navigation from '@/plugin/cms/components/NavigationManagement.vue'
import NavigationManagement from '@/plugin/cms/components/NavigationManagement.vue'
import BadgeManagement from '@/plugin/cms/components/BadgeManagement.vue'
import LobbyNavItem from '@/plugin/cms/models/LobbyNavItem'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import DomainSelector from '@/plugin/components/DomainSelector.vue'
import JsonLobbyContainer from './models/JsonLobbyContainer'
import LayoutBannerSelector from '@/plugin/cms/components/LayoutBannerSelector.vue'
import LayoutBannerList from '@/plugin/cms/models/LayoutBannerList'
import {LayoutBannerEntryTypeEnum} from '@/plugin/cms/models/LayoutBannerEntryTypeEnum'
import LayoutBannerItem from '@/plugin/cms/models/LayoutBannerItem'
import LobbyPage from './models/LobbyPage'
import {DomainItemInterface} from './models/DomainItem'
import LayoutGameEntryItem, {TileSizeEnum} from "@/plugin/cms/models/LayoutGameEntryItem";

@Component({
  components: {
    LayoutBannerSelector,
    NavigationManagement,
    BadgeManagement,
    Navigation,
    SubNavigation,
    DomainSelector,
    ChannelSelector,
    LobbySelector,
    NavCodeMapper,
    LayoutSelector
    // CMSGames
  }
})
export default class Cms extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  get fullJson(): string {
    let allLobbyItems: LobbyItem[] = []
    if (!this.lobbies || this.lobbies.length === 0) return JSON.stringify([])
    this.lobbies.forEach((l) => {
      l.lobbyItems.forEach((li) => {
        allLobbyItems.push(li)
      })
    })
    return JSON.stringify(allLobbyItems, null, 4).trim()
  }

  get activeLobbyJson(): string {
    return JSON.stringify(this.selectedLobby ? this.selectedLobby.lobbyItems : [], null, 4).trim()
  }

  get selectedPageJson(): string {
    return JSON.stringify(this.lobbyItemSelected ? this.selectedLobbyItem : {}, null, 4).trim()
  }

  lobbyTab = 0

  selectedDomain: string = ''

  selectedChannel: string = ''

  lobbies: Lobby[] = []
  selectedLobby: Lobby | null = null
  lobbySelected = false

  currentNav = null

  selectedLobbyItem: LobbyItem | null = null

  lobbyItemSelected = false
  lobbiesLoading = false

  showJson = true

  showPublishLobbyDialog = false
  outputJsonType: string = 'active'

  modifyLobbyId: number | null = null

  description = ''

  domain: DomainItemInterface | null = null

  onDomainSelected(item: DomainItemInterface | null) {
    if (item === null) {
      return
    }

    if (this.selectedDomain !== item.name) {
      this.resetSelections()
    }
    this.selectedDomain = item.name

    this.loadLobbiesForDomainAndChannel()
  }

  onChannelSelected(channelItem: ChannelItem) {
    if (this.selectedChannel !== channelItem.name) {
      this.resetSelections()
    }
    this.selectedChannel = channelItem.name
  }

  async loadLobbiesForDomainAndChannel() {
    if (!this.selectedDomain) {
      this.lobbiesLoading = false
      return
    }

    this.lobbiesLoading = true
    this.rootScope.provide.casinoCmsProvider
      .getLobbies(this.selectedDomain)
      .then((lobbies: Lobby[]) => {
        this.lobbies = []
        if (lobbies && lobbies.length > 0) {
          this.modifyLobbyId = lobbies[0].id

          for (let l = 0; l < lobbies.length; l++) {
            lobbies[l].active = false
            if (lobbies[l].lobbyItems && lobbies[l].lobbyItems.length > 0) {
              lobbies[l].badges = lobbies[l].lobbyItems[0].badges
              if (lobbies[l].lobbyItems[0].nav) {
                lobbies[l].nav = lobbies[l].lobbyItems[0].nav
                lobbies[l].nav[0].primary_nav_code = lobbies[l].lobbyItems[0].page.primary_nav_code
                lobbies[l].channel = lobbies[l].lobbyItems[0].page.channel
              }
              for (let li = 0; li < lobbies[l].lobbyItems.length; li++) {
                let tempLobbyItem: LobbyItem = new LobbyItem()
                lobbies[l].lobbyItems[li] = Object.assign(tempLobbyItem, lobbies[l].lobbyItems[li])
                const newLobbyPage = new LobbyPage()
                lobbies[l].lobbyItems[li].page = Object.assign(newLobbyPage, lobbies[l].lobbyItems[li].page)
                if (lobbies[l].lobbyItems[li].page && lobbies[l].lobbyItems[li].page.widgets) {
                  lobbies[l].lobbyItems[li].page.gameBanner = new LayoutBannerList()
                  let bannerEntries = lobbies[l].lobbyItems[li].page.widgets
                    .filter((widget) => widget.type === LayoutBannerEntryTypeEnum.BANNER.valueOf())
                    .map((widget) => widget as LayoutBannerItem)
                  lobbies[l].lobbyItems[li].page.gameBanner.entries = bannerEntries
                  let widgets = lobbies[l].lobbyItems[li].page.widgets.filter((widget) => widget.type !== LayoutBannerEntryTypeEnum.BANNER.valueOf())
                      .map((widget) => {
                        const tempWidget = new LayoutGameEntryItem(null, '', TileSizeEnum.STANDARD)
                        return Object.assign(tempWidget, widget)
                      })

                  lobbies[l].lobbyItems[li].page.widgets = widgets
                }
              }
            }
            this.lobbies.push(lobbies[l])
          }
        } else {
          this.modifyLobbyId = null
        }
      })
      .catch(() => {
        this.modifyLobbyId = null
      })
      .finally(() => {
        this.lobbiesLoading = false
      })
  }

  onLobbySelected(lobby: Lobby) {
    this.resetSelections()
    this.selectedLobby = lobby
    this.currentNav = JSON.parse(JSON.stringify(lobby.nav[0]))
    this.lobbySelected = true
  }

  async onLobbySaved(lobby: Lobby) {
    this.lobbiesLoading = true
    this.lobbies.push(lobby)
    this.lobbiesLoading = false
  }

  onLobbyPublish() {
    if (this.selectedLobby) {
      this.description = this.selectedLobby.description
    }
    this.showPublishLobbyDialog = true
  }

  onCancelPublishClick() {
    this.showPublishLobbyDialog = false
  }

  onConfirmPublishClick() {
    this.showPublishLobbyDialog = false
    this.lobbiesLoading = true
    let jsonLobby = new JsonLobbyContainer()
    jsonLobby.description = this.description
    jsonLobby.json = this.fullJson
    if (this.modifyLobbyId != null && this.selectedDomain != null) {
      this.rootScope.provide.casinoCmsProvider
        .modifyLobby(this.selectedDomain, this.modifyLobbyId)
        .then(() => {
          if (this.modifyLobbyId != null && this.selectedDomain != null) {
            this.rootScope.provide.casinoCmsProvider
              .modifyAndSaveCurrentLobby(this.selectedDomain, this.modifyLobbyId, jsonLobby)
              .then(() => {
                this.loadLobbiesForDomainAndChannel()
              })
              .finally(() => {
                this.lobbiesLoading = false
              })
          }
        })
        .catch(() => {
          this.showPublishLobbyDialog = false
          this.lobbiesLoading = false
        })
    } else {
      if (this.selectedDomain) {
        this.rootScope.provide.casinoCmsProvider
          .add(this.selectedDomain, jsonLobby)
          .then(() => {
            this.loadLobbiesForDomainAndChannel()
          })
          .catch(() => {
            this.showPublishLobbyDialog = false
            this.lobbiesLoading = false
          })
      }
    }
  }

  onLobbyDelete(removeLobby: Lobby) {
    this.lobbiesLoading = true
    if (removeLobby && !removeLobby.lobbyItems) {
      if (this.selectedLobby === removeLobby) {
        this.currentNav = null
        this.selectedLobby = null as unknown as Lobby
        this.lobbySelected = false
      }
      const index = this.lobbies.findIndex((lobby) => lobby === removeLobby)
      this.lobbies.splice(index, 1)
      this.lobbiesLoading = false
    }
  }

  onSubNavSelect(subNavCode: string) {
    if (this.selectedLobby) {
      let index = this.selectedLobby.lobbyItems.findIndex((lobbyItem: LobbyItem) => lobbyItem.page.secondary_nav_code === subNavCode)
      if (index >= 0) this.selectedLobbyItem = this.selectedLobby.lobbyItems[index]
      else {
        if (this.selectedChannel) {
          this.selectedLobbyItem = new LobbyItem()
          this.selectedLobbyItem.page.secondary_nav_code = subNavCode
          this.selectedLobby.addLobbyItem(this.selectedLobbyItem, this.selectedChannel)
        }
      }
      this.lobbyItemSelected = true
      this.lobbyTab = 1
    }
  }

  onNavigationChanged(nav: LobbyNavItem) {
    if (this.selectedLobby) {
      this.updateLobbyItemList(nav, this.selectedLobby.nav[0])
      this.selectedLobby.nav[0] = JSON.parse(JSON.stringify(nav))
      this.selectedLobby.lobbyItems.forEach((lobbyItem) => {
        if (this.selectedLobby) {
          lobbyItem.nav[0] = JSON.parse(JSON.stringify(nav))
          lobbyItem.page.primary_nav_code = nav.primary_nav_code
        }
      })
      this.rearrangeLobbyItems(nav)
    }
  }

  private rearrangeLobbyItems(nav: LobbyNavItem) {
    if (this.selectedLobby) {
      let navItemsCopy: LobbyItem[] = this.selectedLobby.lobbyItems
      this.selectedLobby.lobbyItems = []
      nav.nav.forEach((subNav, index: number) => {
        if (this.selectedLobby) {
          const lobbyItem = navItemsCopy.find((lobbyItemFound) => lobbyItemFound.page.secondary_nav_code === subNav.code)
          if (lobbyItem) this.selectedLobby.lobbyItems[index] = lobbyItem
        }
      })
    }
  }

  private updateLobbyItemList(newNav: LobbyNavItem, oldNav: LobbyNavItem) {
    oldNav.nav.forEach((subNav) => {
      if (this.selectedLobby) {
        const subNavIndex = newNav.nav.findIndex((newSubNav) => newSubNav.code === subNav.code)
        if (subNavIndex < 0) {
          const lobbyItemIndex = this.selectedLobby.lobbyItems.findIndex((li) => li.page.secondary_nav_code === subNav.code)
          this.selectedLobby.lobbyItems.splice(lobbyItemIndex, 1)
          if (this.selectedLobbyItem) {
            if (this.selectedLobbyItem.page.secondary_nav_code === subNav.code) {
              this.selectedLobbyItem = null as unknown as LobbyItem
              this.lobbyItemSelected = false
            }
          }
        }
      }
    })

    newNav.nav.forEach((subNav) => {
      const lobbyItem = this.selectedLobby?.lobbyItems.find((li) => li.page.secondary_nav_code === subNav.code)
      if (!lobbyItem && this.selectedLobby) {
        this.selectedLobby.lobbyItems.push(this.buildLobbyItem(subNav.code))
      }
    })
  }

  private buildLobbyItem(subNavCode: string): LobbyItem {
    const newLobbyItem = new LobbyItem()
    if (this.selectedLobby && this.selectedChannel) {
      newLobbyItem.page.secondary_nav_code = subNavCode
      newLobbyItem.nav = this.selectedLobby.nav
      newLobbyItem.badges = this.selectedLobby.badges
      newLobbyItem.page.primary_nav_code = this.selectedLobby.nav[0].primary_nav_code
      newLobbyItem.page.channel = this.selectedChannel
      newLobbyItem.name = this.selectedLobby.name
      newLobbyItem.state = 'logged_in'
    }
    return newLobbyItem
  }

  resetNav() {
    if (this.selectedLobby) {
      this.currentNav = JSON.parse(JSON.stringify(this.selectedLobby.nav[0]))
    }
  }

  resetSelections() {
    this.selectedLobby = null
    this.lobbySelected = false
    this.selectedLobbyItem = null
    this.lobbyItemSelected = false
    this.currentNav = null
  }

  copyFullLobbyJsonToClipBoard() {
    navigator.clipboard
      .writeText(this.fullJson)
      .then(() => {
        alert(this.translate('UI_NETWORK_ADMIN.CMS.JSON.OUTPUT.COPY_SUCCESS'))
      })
      .catch(() => {
        alert(this.translate('UI_NETWORK_ADMIN.CMS.JSON.OUTPUT.COPY_FAILED'))
      })
  }

  copyActiveLobbyJsonToClipBoard() {
    navigator.clipboard
      .writeText(JSON.stringify(this.selectedLobby ? this.selectedLobby.lobbyItems : [], null, 4).trim())
      .then(() => {
        alert(this.translate('UI_NETWORK_ADMIN.CMS.JSON.OUTPUT.COPY_SUCCESS'))
      })
      .catch(() => {
        alert(this.translate('UI_NETWORK_ADMIN.CMS.JSON.OUTPUT.COPY_FAILED'))
      })
  }

  copyPageJsonToClipBoard() {
    navigator.clipboard
      .writeText(JSON.stringify(this.lobbyItemSelected ? this.selectedLobbyItem : {}, null, 4).trim())
      .then(() => {
        alert(this.translate('UI_NETWORK_ADMIN.CMS.JSON.OUTPUT.COPY_SUCCESS'))
      })
      .catch(() => {
        alert(this.translate('UI_NETWORK_ADMIN.CMS.JSON.OUTPUT.COPY_FAILED'))
      })
  }

  translate(text: string) {
    return this.translateService.instant(text)
  }
}
</script>

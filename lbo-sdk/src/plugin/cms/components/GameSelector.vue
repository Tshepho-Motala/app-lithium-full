<template>
  <v-card data-test-id="cnt-game-selector" flat>
    <v-autocomplete
      data-test-id="slt-game"
      @input="onGameSelected(game)"
      filled
      chips
      color="blue-grey lighten-2"
      item
      ref="game"
      v-model="game"
      :items="filteredGamesList"
      label="Game"
      placeholder="Select Game..."
      item-text="commercialName"
      clearable
      return-object
    >
    </v-autocomplete>
  </v-card>
</template>

<script lang='ts'>
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import { Component, Inject, Prop, Vue } from 'vue-property-decorator'
import GameItemInterface from '../interfaces/GameItemInterface'
import LayoutBannerItem from '../models/LayoutBannerItem'
import LobbyItem from '../models/LobbyItem'
import DomainItem from "@/plugin/cms/models/DomainItem"
import ChannelItem from "@/plugin/cms/models/ChannelItem"
import LayoutGameItem from "@/plugin/cms/models/LayoutGameItem"
import LayoutProgressiveGameItem from "@/plugin/cms/models/LayoutProgressiveGameItem"
import {ProgressiveJackpotGameBalanceInterface} from "@/plugin/cms/interfaces/ProgressiveJackpotBalanceInterface"
import {TileWidgetTypeEnum} from "@/plugin/cms/models/TileWidgetTypeEnum"

@Component
export default class GameSelector extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Prop() lobbyItem!: LobbyItem
  @Prop() selectedDomain!: string
  @Prop() selectedChannel!: string
  @Prop() gameTiles!: LayoutGameItem[]
  @Prop() widgetProgressives!: LayoutProgressiveGameItem[]
  @Prop() entryTileWidgetType!: TileWidgetTypeEnum

  games: GameItemInterface[] = []
  gamesBefore: GameItemInterface[] = []
  loading = false
  game = null

  mounted() {
    this.asyncMounted()
  }

  makeAcronym(str: string): string {
    return str.match(/\b(\w)/g)!.join('')
  }

  get filteredGamesList(): GameItemInterface[] {
    if(!this.games) return []
    return this.games.filter((game: GameItemInterface) => {
      if (this.isJackpotWidgetEntry) {
        return game.progressiveJackpot && !this.gameTiles.some((tile: LayoutGameItem) => tile.gameID === game.guid)
      }
      return !this.gameTiles.some((tile: LayoutGameItem) => tile.gameID === game.guid)
    })
  }

  get isJackpotWidgetEntry(): boolean {
    return !!this.entryTileWidgetType && (this.entryTileWidgetType.valueOf() === TileWidgetTypeEnum.JACKPOT_GRID.valueOf()
        || this.entryTileWidgetType.valueOf() === TileWidgetTypeEnum.JACKPOT_TILE.valueOf())
  }



  async asyncMounted() {
    if (!this.selectedDomain  || !this.selectedChannel) {
      return
    }

    try {
      this.loading = true

      this.games = await this.rootScope.provide.gamesProvider.getGamesByDomainAndEnabled(this.selectedDomain, true, true, this.selectedChannel)
      this.rootScope.provide.progressiveFeedsProvider.findProgressiveJackpotGameFeedsByDomain(this.selectedDomain).then((progressiveBalances: ProgressiveJackpotGameBalanceInterface[]) => {
        if (this.widgetProgressives) {
          progressiveBalances = progressiveBalances.filter(pb => this.widgetProgressives.some(wp => wp.progressiveId === pb.progressiveId))
          this.games = this.games.filter(game => progressiveBalances.some(pb => pb.game.guid === game.guid))
        }
      })
      this.loading = false
    } catch (e) {
      console.error(e)
    }
  }

  onGameSelected(game: GameItemInterface) {
    this.$emit('onSelect', game)
    this.game = null
  }
}
</script>

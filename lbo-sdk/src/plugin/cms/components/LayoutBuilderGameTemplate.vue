<template>
  <div data-test-id="cnt-layout-builder-game-template" class="d-flex align-center justify-center">
    <v-card flat @click="showGameSelector" class="gridItemAdd d-flex align-center justify-center">
      <v-icon>mdi-plus</v-icon>
    </v-card>

    <v-menu
      v-if="showGameSelectorMenu"
      v-model="showGameSelectorMenu"
      :position-x="menuX"
      :position-y="menuY"
      absolute
      offset-y
      :close-on-content-click="false"
      max-height="200px"
    >
      <GameSelector :widgetProgressives="widgetProgressives" :gameTiles="gameTiles" :selectedDomain="selectedDomain" :selectedChannel="selectedChannel" :lobbyItem="lobbyItem" @onSelect="onGameSelected" />
    </v-menu>
  </div>
</template>

<script lang='ts'>
import { Component, Prop, Vue } from 'vue-property-decorator'
import GameItemInterface from '../interfaces/GameItemInterface'
import LobbyItem from '../models/LobbyItem'
import GameSelector from './GameSelector.vue'
import DomainItem from "@/plugin/cms/models/DomainItem"
import ChannelItem from "@/plugin/cms/models/ChannelItem"
import LayoutGameItem from "@/plugin/cms/models/LayoutGameItem"
import LayoutProgressiveGameItem from "@/plugin/cms/models/LayoutProgressiveGameItem"

@Component({
  components: {
    GameSelector
  }
})
export default class LayoutBuilderGameTemplate extends Vue {
  @Prop() lobbyItem!: LobbyItem
  @Prop() selectedDomain!: string
  @Prop() selectedChannel!: string
  @Prop() gameTiles!: LayoutGameItem[]
  @Prop() widgetProgressives!: LayoutProgressiveGameItem[]

  menuX = 0
  menuY = 0
  showGameSelectorMenu = false

  showGameSelector(e: MouseEvent) {
    e.preventDefault()
    this.menuX = e.clientX
    this.menuY = e.clientY

    this.$nextTick(() => {
      this.showGameSelectorMenu = true
    })
  }

  onGameSelected(game: GameItemInterface) {
    this.showGameSelectorMenu = false
    this.$emit('onSelect', game)
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
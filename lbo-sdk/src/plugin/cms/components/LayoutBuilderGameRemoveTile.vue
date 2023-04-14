<template>
  <div data-test-id="cnt-layout-builder-game-remove-tile" class="d-flex align-center justify-center">
    <v-card flat @click="showGameSelector" class="gridItemRemove d-flex align-center justify-center">
      <v-icon>mdi-minus</v-icon>
    </v-card>

    <v-menu v-model="showGameSelectorMenu" :position-x="menuX" :position-y="menuY" absolute offset-y :close-on-content-click="false" max-height="200px">
      <GameSelector :lobbyItem="lobbyItem" @onSelect="removeGameTile" />
    </v-menu>
  </div>
</template>

<script lang='ts'>
import { Component, Prop, Vue } from 'vue-property-decorator'
import GameItemInterface from '../interfaces/GameItemInterface'
import LobbyItem from '../models/LobbyItem'
import GameSelector from './GameSelector.vue'

@Component({
  components: {
    GameSelector
  }
})
export default class LayoutBuilderGameRemoveTile extends Vue {
  @Prop() lobbyItem!: LobbyItem

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

  removeGameTile(game: GameItemInterface) {
    this.$emit('remove-game-tile', game)
  }

}
</script>

<style scoped>
.gridItemRemove {
  border: 1px dashed grey;
  border-radius: 5px;
  min-height: 150px;
  width: 100%;
}
</style>
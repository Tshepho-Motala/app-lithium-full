<template>
  <div data-test-id="cnt-layout-builder-banner-template">

    <v-btn v-show="!lobbyNotPublished" :disabled="disableAdd" icon color="primary" @click="showBannerSelector">
      <v-icon>mdi-plus</v-icon>
    </v-btn>

    <v-menu
        v-model="showBannerSelectorMenu"
        :position-x="menuX"
        :position-y="menuY"
        absolute
        offset-y
        :close-on-content-click="false"
        max-height="200px">
      <BannerSelector :lobbyItem="lobbyItem" @onSelect="onBannerSelected" :selectedDomain="selectedDomain" />
    </v-menu>
  </div>
</template>

<script lang='ts'>
import { Component, Prop, Vue } from 'vue-property-decorator'
import LobbyItem from '../models/LobbyItem'
import BannerSelector from './BannerSelector.vue'
import BannerImageInterface from '../interfaces/BannerImageInterface'

@Component({
  components: {
    BannerSelector
  }
})
export default class LayoutBuilderBannerTemplate extends Vue {
  @Prop() lobbyItem!: LobbyItem
  @Prop() selectedDomain!: string;
  @Prop() selectedChannel!: string;
  @Prop() disableAdd!: boolean;
  @Prop() lobbyNotPublished!: boolean;

  menuX = 0
  menuY = 0
  showBannerSelectorMenu = false

  showBannerSelector(e: MouseEvent) {
    e.preventDefault()
    this.menuX = e.clientX
    this.menuY = e.clientY

    this.$nextTick(() => {
      this.showBannerSelectorMenu = true;
    })
  }

  onBannerSelected(banner: BannerImageInterface) {
    this.showBannerSelectorMenu = false
    this.$emit('onSelect', banner)
  }
}
</script>

<style scoped>
/* .gridItemAdd {
  border: 1px dashed grey;
  border-radius: 5px;
  min-height: 150px;
  width: 100%;
} */
</style>

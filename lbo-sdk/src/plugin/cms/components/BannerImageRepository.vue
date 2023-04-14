<template>
  <v-list v-if="shouldDisplay">
    <v-list-item v-for="(banner, ii) in banner" :key="`banner-${ii}`">
      <v-list-item-avatar tile>
        <v-img v-if="banner.url" :alt="`${banner.name} avatar`" :src="banner.url"></v-img>
        <v-avatar tile color="primary" v-else>
          <span class="white--text">{{ makeAcronym(banner.name) }}</span>
        </v-avatar>
      </v-list-item-avatar>
      <v-list-item-content>
        <v-list-item-title v-text="banner.name"></v-list-item-title>
      </v-list-item-content>
    </v-list-item>
  </v-list>
  <div v-else class="text-center pa-4">
    <span class="grey--text">Please select a lobby first</span>
  </div>
</template>

<script lang='ts'>
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import { Component, Inject, Prop, Vue, Watch } from 'vue-property-decorator'
import LobbyItem from '../models/LobbyItem'
import BannerImageInterface from "@/plugin/cms/interfaces/BannerImageInterface";

@Component
export default class BannerImageRepository extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Prop() lobby!: LobbyItem

  banner: BannerImageInterface[] = []
  loading = false

  get shouldDisplay(): boolean {
    return this.lobby !== null
  }

  @Watch('shouldDisplay')
  onShouldDisplay(val: boolean) {
    if (val) {
      this.asyncMounted()
    } else {
      this.banner = []
    }
  }

  makeAcronym(str: string): string {
    return str.match(/\b(\w)/g)!.join('')
  }

  async asyncMounted() {
    if (this.lobby.domain === null || this.lobby.page.channel === null) {
      return
    }

    try {
      this.loading = true

      this.banner = await this.rootScope.provide.bannerImagesProvider.findByDomainName(this.lobby.domain)

      this.loading = false
    } catch (e) {
      console.error(e)
    }
  }
}
</script>
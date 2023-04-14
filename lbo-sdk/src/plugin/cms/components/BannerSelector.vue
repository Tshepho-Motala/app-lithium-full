<template>
  <v-card flat>
    <v-autocomplete
        data-test-id="slt-banner"
        @input="onBannerSelected(banner)"
        filled
        chips
        color="blue-grey lighten-2"
        item
        ref="banner"
        v-model="banner"
        :items="banners"
        label="Banner"
        placeholder="Select Banner..."
        item-text="name"
        clearable
        return-object
    >
    </v-autocomplete>
  </v-card>
</template>

<script lang='ts'>
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import { Component, Inject, Prop, Vue } from 'vue-property-decorator'
import { Banner } from '../models/Banner'
import LobbyItem from '../models/LobbyItem'

@Component
export default class BannerSelector extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Prop() lobbyItem!: LobbyItem
  @Prop() selectedDomain!: string;
  @Prop() selectedChannel!: string;

  banners: Banner[] = []
  loading = false
  banner: Banner | null = null
  imageType: string = 'Banner';

  mounted() {
    this.asyncMounted()
  }

  makeAcronym(str: string): string {
    return str.match(/\b(\w)/g)!.join('')
  }

  async asyncMounted() {
    if (!this.selectedDomain) {
      return
    }

    try {
      this.loading = true
      this.banners = await this.rootScope.provide.casinoCmsProvider.getDomainBanners(this.selectedDomain);
      this.loading = false
    } catch (e) {
      console.error(e)
    }
  }

  onBannerSelected(banner: Banner | null) {
    if (!banner) {
      return;
    }
    this.$emit('onSelect', banner)
    this.banner=null
  }

}
</script>
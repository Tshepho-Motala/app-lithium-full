<template>
  <v-card data-test-id="cnt-channel-selector">
    <v-card-text>
      <v-row align="center">
        <v-col cols="12" md="5">
          <h4 class="title text-primary">{{translate('UI_NETWORK_ADMIN.CMS.CHANNEL_SELECTOR.OUTPUT.TITLE')}}</h4>
        </v-col>
        <v-col cols="12" md="4">
          <p>{{translate('UI_NETWORK_ADMIN.CMS.CHANNEL_SELECTOR.OUTPUT.SELECT_CHANNEL')}}</p>
        </v-col>
        <v-col cols="12" md="3">
          <v-select
          data-test-id="slt-channel-selector"
              :disabled="!selectedDomain"
              outlined
              :loading="loading"
              :items="channelOptions"
              @change="onChange"
              item-text="name"
              item-value="name"
              return-object
              v-model="selectedChannel"
          >
          </v-select>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script lang='ts'>
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import ChannelItem from '@/plugin/cms/models/ChannelItem'
import {Component, Inject, Prop, Vue} from 'vue-property-decorator'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";

@Component
export default class ChannelSelector extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') translateService!: TranslateServiceInterface;

  @Prop() selectedDomain!: string;

  channelOptions: ChannelItem[] = [];
  selectedChannel: string = '';
  loading = true

  preselectChannel = false

  mounted() {
    this.asyncMounted()
  }

  async asyncMounted() {
    this.loading = true
    this.channelOptions = await this.rootScope.provide.gamesProvider.getChannels()
    this.loading = false

    if (this.preselectChannel) {
      // Convenience for dev
      this.selectedChannel = this.channelOptions[1].name
      this.onChange(this.selectedChannel)
    }
  }

  onChange(channel: string) {
    this.$emit('onSelect', channel)
  }

  translate(text: string) {
    return this.translateService.instant(text);
  }
}
</script>

<style>
</style>

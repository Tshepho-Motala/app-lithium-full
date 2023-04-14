<template>
  <v-container fluid class="pt-0">
    <page-header  @onSelect="onDomainSelected" :parentVue="true" :description="pageDescription" :title="pageTitle" />
    <v-row class="v-reset-row" v-if="domain">
      <v-col cols="12">
        <v-card>
          <v-card-text>
            <v-tabs v-model="assetType">
              <v-tab v-for="(type, i) in types" :key="i">
                {{ getTabTranslation(type) }}
              </v-tab>
            </v-tabs>

            <v-tabs-items v-model="assetType">
              <v-tab-item v-for="(type, i) in types" :key="i">
                <v-card flat>
                  <v-card-text>
                    <asset-tab :type="type" :domain="selectedDomain" @upload-message="onMessage" />
                  </v-card-text>
                </v-card>
              </v-tab-item>
            </v-tabs-items>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-snackbar v-model="snack.show" :color="snack.type" :timeout="snack.timeout">
      {{ snack.message }}
    </v-snackbar>
  </v-container>
</template>

<script lang="ts">
import TranslationMixin from "@/core/mixins/translationMixin";
import { Component, Inject, Mixins } from "vue-property-decorator";
import CmsAssetType from "../models/CmsAssetType";
import AssetTab from "./AssetTab.vue";
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import AssetMixin from '@/plugin/cms/mixins/AssetMixin';
import PageHeader from "@/plugin/components/PageHeader.vue";

@Component({
  components: {
    AssetTab,
    PageHeader,
  },
})
export default class Assets extends Mixins(AssetMixin, TranslationMixin) {
  private types: CmsAssetType[] = [CmsAssetType.Style, CmsAssetType.Font]

  assetType: CmsAssetType = CmsAssetType.Font

  @Inject('userService')
  readonly userService!: UserServiceInterface

  getTabTranslation(type: CmsAssetType) {
    return this.$translate('UI_NETWORK_ADMIN.CMS.ASSETS.TABS.' + type.toUpperCase())
  }

  get pageTitle() {
    return "UI_NETWORK_ADMIN.CMS.ASSETS.TITLE"
  }

  get pageDescription() {
    return "UI_NETWORK_ADMIN.CMS.ASSETS.DESCRIPTION"
  }
}
</script>

<style scoped></style>

<template>
  <v-container fluid class="pt-0">
    <page-header  @onSelect="onDomainSelected" :parentVue="true" :description="pageDescription" :title="pageTitle" />
    <v-row class="v-reset-row" v-if="domain">
      <v-col cols="12">
        <v-card>
          <v-card-text>
            <v-tabs v-model="imageType">
              <v-tab v-for="type in types" :key="type.type" :disabled="!tabEnabled(type.manageRole)">
                {{ getTabTranslation(type) }}
              </v-tab>
            </v-tabs>

            <v-tabs-items v-model="imageType">
              <v-tab-item v-for="type in types" :key="'tab-item' + type.type">
                <v-card flat>
                  <v-card-text>
                    <image-tab :type="type" :domain="selectedDomain" @upload-message="onMessage"/>
                  </v-card-text>
                </v-card>
              </v-tab-item>
            </v-tabs-items>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-snackbar
      v-model="snack.show"
      :color="snack.type"
      :timeout="snack.timeout"
    >
      {{ snack.message }}
    </v-snackbar>
  </v-container>
</template>

<script lang="ts">
import TranslationMixin from "@/core/mixins/translationMixin";
import { Component, Inject, Mixins } from "vue-property-decorator";
import ImageTab from "./ImageTab.vue";
import CmsAssetType from "../models/CmsAssetType"
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import AssetMixin from "../mixins/AssetMixin";
import PageHeader from "../../components/PageHeader.vue"

@Component({
  components: {
    ImageTab,
    PageHeader
  },
})
export default class Images extends Mixins( AssetMixin,TranslationMixin) {
  types = [
    { type: CmsAssetType.Banner,  addRole: 'BANNER_IMAGE_ADD', deleteRole: 'BANNER_IMAGE_DELETE', manageRole: 'BANNER_IMAGE_MANAGE'},
    { type: CmsAssetType.Tile, addRole: 'GAME_TILE_ADD', deleteRole: 'GAME_TILE_DELETE',  manageRole: 'GAME_TILE_MANAGE'}
  ];

  imageType = {};

  @Inject('userService') 
  readonly userService!: UserServiceInterface;

  mounted(){
    document.title=this.pageHeaderTitle;
  }

  getTabTranslation(type: any) {
    return this.$translate(
      "UI_NETWORK_ADMIN.CASINO.IMAGES.TABS." + type.type.toUpperCase()
    );
  }

  get pageTitle() {
    return "UI_NETWORK_ADMIN.CASINO.IMAGES.TITLE";
  }
  get pageHeaderTitle() {
    return this.$translate("UI_NETWORK_ADMIN.CASINO.IMAGES.HEADER.TITLE");
  }

  get pageDescription() {
    return "UI_NETWORK_ADMIN.CASINO.IMAGES.DESCRIPTION";
  }

  tabEnabled(role: string): boolean {
    if(this.domain) {
      return this.userService.hasRoleForDomain( this.domain?.title ? this.domain?.title : this.domain?.name, role);
    } else {
      return false
    }
  }
}
</script>

<style scoped></style>

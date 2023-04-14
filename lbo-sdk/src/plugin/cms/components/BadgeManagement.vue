<template>
  <v-container data-test-id="cnt-badge-management" v-if="lobby">
    <v-row>
      <v-col cols="12" md="6">
        <v-text-field
          data-test-id="txt-badge-code"
          dense
          clearable
          v-model="badge.code"
          :placeholder="translate('UI_NETWORK_ADMIN.CMS.BADGE_MANAGEMENT.FIELDS.BADGE_CODE.PLACEHOLDER')"
        ></v-text-field>
      </v-col>
      <v-col cols="12" md="6">
        <v-text-field
          data-test-id="txt-badge-image"
          dense
          clearable
          v-model="badge.image"
          :placeholder="translate('UI_NETWORK_ADMIN.CMS.BADGE_MANAGEMENT.FIELDS.BADGE_IMAGE.PLACEHOLDER')"
        ></v-text-field>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12">
        <v-btn data-test-id="btn-add" class="ma-2" color="primary" @click="addBadge" :disabled="isAddBadgeBtnDisabled(badge.code, badge.image)">{{ translate('UI_NETWORK_ADMIN.CMS.BADGE_MANAGEMENT.BUTTON.ADD') }}</v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <h3>{{ translate('UI_NETWORK_ADMIN.CMS.BADGE_MANAGEMENT.OUTPUT.BADGE_LIST') }}</h3>
      </v-col>
    </v-row>
    <v-card class="mt-2" v-for="badgeItem in lobby.badges" :item="badgeItem" :key="badgeItem.code">
      <v-row class="ml-3">
        <v-col cols="12">
          <h4>{{ badgeItem.code }}</h4>
        </v-col>
        <v-col cols="12" md="10">
          <img :src="badgeItem.image" />
        </v-col>
        <v-col cols="12" md="2">
          <v-btn data-test-id="btn-remove-badge" icon color="error" @click="removeBadge(badgeItem.code)">
            <v-icon> mdi-close </v-icon>
          </v-btn>
        </v-col>
      </v-row>
    </v-card>
    <div class="text-center">
      <v-snackbar v-model="snackbar" :color="snackbarColour" :right="true" :timeout="1500">{{ snackbarTitle}}<template v-slot:action="{ attrs }">
          <v-btn data-test-id="btn-close" color="black" text v-bind="attrs" @click="snackbar = false">
            <v-icon dark>mdi-close</v-icon>
          </v-btn>
        </template>
      </v-snackbar>
    </div>
  </v-container>
  <v-container v-else>
    <div class="text-center pa-4">
      <span class="grey--text">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.OUTPUT.SELECT_LOBBY_MESSAGE') }}</span>
    </div>
  </v-container>
</template>

<script lang="ts">
import { Component, Inject, Prop, Vue } from 'vue-property-decorator'
import Badge from '@/plugin/cms/models/Badge'
import Lobby from '@/plugin/cms/models/Lobby'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
@Component({})
export default class BadgeManagement extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop() lobby!: Lobby
  badge: Badge = new Badge()
  snackbar: boolean = false
  snackbarColour: string = ''
  snackbarTitle: string = ''

  addBadge() {
    if (this.lobby.badges.length != 0) {
      for (let value of this.lobby.badges) {
        if (value.code === this.badge.code || value.image === this.badge.image) {
          this.buildSnackBarProperties("duplicate");
          return;
        }
      }
    }
    this.lobby.badges.push(this.badge)
    this.badge = new Badge()
    this.buildSnackBarProperties("success");
  }

  translate(text: string) {
    return this.translateService.instant(text)
  }

  removeBadge(code: string): void {
    let index = this.lobby.badges.findIndex((b) => b.code === code)
    this.lobby.badges.splice(index, 1)
  }

  isAddBadgeBtnDisabled(code: string, image: string ) {
    if ((code && image) && (code.trim().length > 0  && image.trim().length > 0)) {
      return false
    }
    return true;
  }

  buildSnackBarProperties(type: string){
    switch (type) {
      case "duplicate":
        this.snackbarColour = "error";
        this.snackbarTitle = this.translate("UI_NETWORK_ADMIN.CMS.BADGE_MANAGEMENT.ADD.DUPLICATE");
        this.snackbar = true;
        break;
      case "success":
        this.snackbarColour = "success";
        this.snackbarTitle = this.translate("UI_NETWORK_ADMIN.CMS.BADGE_MANAGEMENT.ADD.SUCCESS");
        this.snackbar = true;
        break;
    }
  }
}
</script>

<style>
</style>
<template>
  <v-container data-test-id="cnt-navigation-management" v-if="lobby">
    <v-form data-test-id="frm-navigation-management" ref="form" v-model="valid" lazy-validation>
      <v-row>
        <v-col cols="12" md="6">
          <v-text-field
            data-test-id="txt-nav-code"
            dense
            clearable
            v-model="nav.code"
            :disabled="modifyDisabled"
            :rules="inputRules"
            :placeholder="translate('UI_NETWORK_ADMIN.CMS.NAVIGATION_MANAGEMENT.FIELDS.NAV_CODE.PLACEHOLDER')"
          >
            <template #label>
              <span class="red--text"><strong>* </strong></span>{{ translate('UI_NETWORK_ADMIN.CMS.NAVIGATION_MANAGEMENT.FIELDS.CODE.LABEL') }}
            </template>
          </v-text-field>
          <span>
            <v-chip v-if="primaryCodeValidity === 'invalid'" class="ma-2" small color="red">
              {{ translate('UI_NETWORK_ADMIN.CMS.NAVIGATION_MANAGEMENT.FIELDS.PRIMARY_REQUIRED_NEW.PLACEHOLDER') }}
            </v-chip></span
          >
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            data-test-id="txt-nav-title"
            dense
            clearable
            v-model="nav.title"
            :disabled="modifyDisabled"
            :rules="inputRules"
            :placeholder="translate('UI_NETWORK_ADMIN.CMS.NAVIGATION_MANAGEMENT.FIELDS.NAV_TITLE.PLACEHOLDER')"
          >
            <template #label>
              <span class="red--text"><strong>* </strong></span>{{ translate('UI_NETWORK_ADMIN.CMS.NAVIGATION_MANAGEMENT.FIELDS.TITLE.LABEL') }}
            </template>
          </v-text-field>
          <v-chip v-if="primaryTitleValidity === 'invalid'" class="ma-2" small color="red">
            {{ translate('UI_NETWORK_ADMIN.CMS.NAVIGATION_MANAGEMENT.FIELDS.PRIMARY_REQUIRED_NEW.PLACEHOLDER') }}
          </v-chip>
        </v-col>
        <v-col cols=" 12" md="12">
          <v-text-field
            data-test-id="txt-nav-primary-code"
            dense
            clearable
            v-model="nav.primary_nav_code"
            :disabled="modifyDisabled"
            :placeholder="translate('UI_NETWORK_ADMIN.CMS.NAVIGATION_MANAGEMENT.FIELDS.PRIMARY_NAV_CODE.PLACEHOLDER')"
          ></v-text-field>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12">
          <div>
            <v-alert v-if="subNavigationValidity === 'invalid'" text prominent type="error" icon="mdi-cloud-alert">
              {{ translate('UI_NETWORK_ADMIN.CMS.NAVIGATION_MANAGEMENT.FIELDS.PRIMARY_ADD_NEW.PLACEHOLDER') }}
            </v-alert>
          </div>
          <sub-navigation :nav="nav" :modifyDisabled="modifyDisabled" @onSubNavSelect="onSubNavSelect"></sub-navigation>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" text-right>
          <v-btn data-test-id="btn-modify-disable" v-if="modifyDisabled" color="warning" @click="toggleModifyDisabled">{{
            translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.MODIFY')
          }}</v-btn>
          <v-btn data-test-id="btn-modify-cancel" v-if="!modifyDisabled" color="error" @click="cancelModify">{{
            translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.CANCEL')
          }}</v-btn>
          <v-btn data-test-id="btn-modify-save" class="ml-1" color="primary" @click="onSave" v-show="!modifyDisabled">{{
            translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.SAVE')
          }}</v-btn>
        </v-col>
      </v-row>
    </v-form>
  </v-container>
  <v-container v-else>
    <div class="text-center pa-4">
      <span class="grey--text">{{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.OUTPUT.SELECT_LOBBY_MESSAGE') }}</span>
    </div>
  </v-container>
</template>

<script lang="ts">
import { Component, Inject, Prop, Vue } from 'vue-property-decorator'
import SubNavigation from '@/plugin/cms/components/SubNavigation.vue'
import Lobby from '@/plugin/cms/models/Lobby'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import LobbyNavItem from '@/plugin/cms/models/LobbyNavItem'

@Component({
  components: { SubNavigation }
})
export default class NavigationManagement extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop() lobby!: Lobby
  @Prop() nav!: LobbyNavItem

  modifyDisabled = true

  valid = true

  inputRules = [(v) => v.length > 0 || 'This is a required field' || 'required']

  primaryCodeValidity: String = 'pending'
  primaryTitleValidity: String = 'pending'
  subNavigationValidity: String = 'pending'

  modifyButtonText = 'Modify'

  translate(text: string) {
    return this.translateService.instant(text)
  }

  toggleModifyDisabled() {
    this.subNavigationValidity = 'pending'
    this.modifyDisabled = !this.modifyDisabled
  }

  cancelModify() {
    this.$emit('resetNav')
    this.modifyDisabled = !this.modifyDisabled
  }

  onSubNavSelect(subNavCode: string) {
    if (!this.modifyDisabled) return
    this.$emit('onSubNavSelect', subNavCode)
  }

  onSave() {
    if (this.nav.code.trim() === '') {
      this.primaryCodeValidity = 'invalid'
    } else if (this.nav.title.trim() === '') {
      this.primaryCodeValidity = 'pending'
      this.primaryTitleValidity = 'invalid'
    } else if (this.nav.nav.length < 1) {
      this.primaryTitleValidity = 'pending'
      this.subNavigationValidity = 'invalid'
    } else {
      this.$emit('onSaveNavigation', this.nav)
      this.toggleModifyDisabled()
      this.clearText();
    }
  }

  private clearText() {
    this.primaryCodeValidity = '';
    this.primaryTitleValidity = '';
  }
}
</script>
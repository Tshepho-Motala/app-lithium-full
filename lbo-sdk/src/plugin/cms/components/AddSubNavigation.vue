<template>
  <v-container data-test-id="cnt-add-sub-navigation">
    <v-card tile>
      <v-row dense>
        <v-col cols="12" md="3" class="text-left">
          <v-list-item-content>
            <h4 class="ml-3">{{translate("UI_NETWORK_ADMIN.CMS.ADD_SUB_NAVIGATION.OUTPUT_FIELDS.ADD_SUB_NAVIGATION")}}</h4>
          </v-list-item-content>
        </v-col>
        <v-col cols="12" md="7">
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field data-test-id="txt-sub-nav-code" dense clearable v-model="subNav.code" :disabled="modifyDisabled"
                            :placeholder="translate('UI_NETWORK_ADMIN.CMS.ADD_SUB_NAVIGATION.FIELDS.CODE.PLACEHOLDER')">
                <template #label>
                  <span class="red--text"><strong>* </strong></span>{{translate('UI_NETWORK_ADMIN.CMS.ADD_SUB_NAVIGATION.FIELDS.CODE.LABEL')}}
                </template>
              </v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field data-test-id="txt-sub-nav-title" dense clearable v-model="subNav.title" :disabled="modifyDisabled"
                            :placeholder="translate('UI_NETWORK_ADMIN.CMS.ADD_SUB_NAVIGATION.FIELDS.TITLE.PLACEHOLDER')">
                <template #label>
                  <span class="red--text"><strong>* </strong></span>{{translate('UI_NETWORK_ADMIN.CMS.ADD_SUB_NAVIGATION.FIELDS.TITLE.LABEL')}}
                </template>
              </v-text-field>
            </v-col>
          </v-row>
        </v-col>
        <v-col cols="12" md="2">
          <v-btn data-test-id="btn-add" color="primary" @click="addSubNavigation()" :disabled="isAddButtonDisabled(subNav.title, subNav.code)">
            <v-icon dark>
              mdi-plus
            </v-icon>
          </v-btn>
        </v-col>
      </v-row>
    </v-card>
  </v-container>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from 'vue-property-decorator'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import SubNavItem from "@/plugin/cms/models/SubNavItem";

@Component
export default class AddSubNavigation extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface;
  @Prop() modifyDisabled!: boolean;

  subNav: SubNavItem = new SubNavItem();

  addSubNavigation() {
    this.$emit('add-sub-navigation', this.subNav);
    this.subNav = new SubNavItem();
  }

  isAddButtonDisabled(code: string, title: string ) {
    if ((code && title) &&
        (code.trim().length > 0  && title.trim().length > 0)) {
      return false
    }
      return true;
  }

  translate(text: string) {
    return this.translateService.instant(text);
  }
}

</script>

<style>
</style>

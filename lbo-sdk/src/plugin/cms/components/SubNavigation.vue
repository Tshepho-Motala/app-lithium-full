<template>
  <v-container data-test-id="cnt-sub-navigation" v-if="nav">
    <v-card tile>
      <v-row dense>
        <v-col cols="12" md="2" class="text-left">
          <v-list-item-content>
            <h4 class="ml-3">{{ translate('UI_NETWORK_ADMIN.CMS.SUB_NAVIGATION.OUTPUT_FIELDS.SUB_NAVIGATION') }}</h4>
          </v-list-item-content>
        </v-col>
        <v-col cols="12" md="10">
          <div style="overflow-x: auto; white-space: nowrap" tile>
            <draggable :options="{ disabled: modifyDisabled }" v-model="nav.nav">
              <transition-group>
                <v-btn
                  data-test-id="btn-configure"
                  class="ma-2"
                  color="primary"
                  @click="configurePage(item)"
                  v-for="item in nav.nav"
                  :item="item"
                  :key="item.code"
                >
                  {{ item.title }}
                </v-btn>
              </transition-group>
            </draggable>
          </div>
        </v-col>
      </v-row>
    </v-card>
    <br />
    <add-sub-navigation :modifyDisabled="modifyDisabled" @add-sub-navigation="addSubNavigation"></add-sub-navigation>
    <span  class="text-center"></span>

    <br>
    <remove-sub-navigation :modifyDisabled="modifyDisabled" :items="nav.nav" @remove-sub-navigation="removeSubNavigation"/>
    <div class="text-center">
      <v-snackbar v-model="snackbar" :color="snackbarColour" :right="true"
        >{{ snackbarTitle
        }}<template v-slot:action="{ attrs }">
          <v-btn data-test-id="btn-close" color="black" text v-bind="attrs" @click="snackbar = false">
            <v-icon dark>mdi-close</v-icon>
          </v-btn>
        </template>
      </v-snackbar>
    </div>
  </v-container>
  <div v-else class="text-center pa-4">
    <span class="grey--text">
      {{ translate('UI_NETWORK_ADMIN.CMS.SUB_NAVIGATION.OUTPUT_FIELDS.LOBBY_NOT_SELECTED_MESSAGE') }}
    </span>
  </div>
</template>

<script lang='ts'>
import AddSubNavigation from './AddSubNavigation.vue'
import RemoveSubNavigation from './RemoveSubNavigation.vue'
import { Component, Inject, Prop, Vue } from 'vue-property-decorator'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'
import SubNavItem from '../models/SubNavItem'
import LobbyNavItem from '@/plugin/cms/models/LobbyNavItem'
import draggable from 'vuedraggable'

@Component({
  components: {
    RemoveSubNavigation,
    AddSubNavigation,
    draggable
  }
})
export default class SubNavigation extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop() nav!: LobbyNavItem
  @Prop() modifyDisabled!: boolean
  @Prop() snackbar: boolean = false
  @Prop() snackbarColour: string = ''
  @Prop() snackbarTitle: string = ''

  subNavValidity = 'pending'

  addSubNavigation(subNav: SubNavItem) {
    if (this.nav.nav.length != 0) {
      for (let value of this.nav.nav) {
        if (value.title === subNav.title || value.code === subNav.code) {
          this.buildSnackBarProperties("duplicate");
          return;
        }
      }
    }
    this.buildSnackBarProperties("success");
    this.nav.nav.push(subNav);
    }

  removeSubNavigation(code: string) {
    let index = this.nav.nav.findIndex((x) => x.code === code)
    this.nav.nav.splice(index, 1)
  }

  translate(text: string) {
    return this.translateService.instant(text)
  }

  configurePage(item: SubNavItem) {
    this.$emit('onSubNavSelect', item.code)
  }

  buildSnackBarProperties(type: string){
    switch (type) {
      case "duplicate":
        this.snackbarColour = "error";
        this.snackbarTitle = this.translate("UI_NETWORK_ADMIN.CMS.SUB_NAVIGATION.ADD.ERROR_MESSAGE.DUPLICATE_ENTRY");
        this.snackbar = true;
        break;
      case "success":
        this.snackbarColour = "success";
        this.snackbarTitle = this.translate("UI_NETWORK_ADMIN.CMS.SUB_NAVIGATION.ADD.SUCCESS_MESSAGE");
        this.snackbar = true;
        break;
    }
  }
}
</script>

<style>
</style>

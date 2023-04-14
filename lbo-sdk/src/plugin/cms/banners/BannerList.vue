<template>
  <v-row no-gutters data-test-id="cnt-banner-list">
    <v-col cols="12" class="mb-3">
      <DomainSelector
          v-model="domain"
          horizontal
          unlocked
          :roles="['ADMIN', 'CASINO_BANNERS_VIEW', 'CASINO_BANNERS_EDIT', 'CASINO_BANNERS_ADD']"
          title="Banner Management"
          description="To manage banners"
          @change="onDomainSelected"
      />
    </v-col>
    <v-col v-if="selectedDomain" cols="12">
      <v-data-table :loading="loading"
                    :headers="headers"
                    :items="banners"
                    :items-per-page="20"
                    :footer-props="{'items-per-page-options': [5, 10, 15, 20, 100]}"
                    sort-by="id"
                    class="elevation-1"
                    @click:row="onItemClick">
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>Banners</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn :disabled="!domain" data-test-id="btn-add-banner" @click="onNewBannerClick" color="primary">
              <v-icon left>mdi-plus</v-icon>
            </v-btn>
          </v-toolbar>
        </template>
        <template v-slot:[`item.date`]="{ item }">
          <span>{{ timestampToDateString(item.startDate) }}</span>
        </template>
        <template v-slot:[`item.actions`]="{ item }">
          <v-icon data-test-id="btn-delete-lobby" small class="mr-2" color="red" @click="deleteBanner(item)">
            mdi-delete
          </v-icon>
          <v-icon data-test-id="btn-edit-lobby" small class="mr-2" @click="onUpdateBannerClick(item.id)">
            mdi-pencil-box-outline
          </v-icon>
        </template>

        <template v-slot:no-data>
          <span>Please add a new lobby to continue.</span>
        </template>
      </v-data-table>
    </v-col>

    <v-dialog v-if="selectedDomain" persistent :title="translate('UI_NETWORK_ADMIN.CASINO.MODIFY_LOBBY.SELECTOR.TITLE')"
              max-width="1000" v-model="showModifyBannerDialog">
      <BannerCreator v-if="showModifyBannerDialog" @banner-edit-cancel="onCancelBannerEdit"
                     @banner-edit-submit="onSubmitBanner" :editBannerId="editBannerId"
                     :selectedDomain="selectedDomain"/>
    </v-dialog>

  </v-row>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from 'vue-property-decorator'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface"
import {RootScopeInterface} from "@/core/interface/ScopeInterface"
import {DomainItemInterface} from "@/plugin/cms/models/DomainItem"
import DomainSelector from "@/plugin/components/DomainSelector.vue"
import BannerCreator from "@/plugin/cms/banners/BannerCreator.vue"
import {Banner} from "@/plugin/cms/models/Banner"
import LayoutGameEntryItem from "@/plugin/cms/models/LayoutGameEntryItem"
import ListenerServiceInterface from "@/core/interface/service/ListenerServiceInterface"
import {string} from "yargs";

@Component({
  components: {
    DomainSelector,
    BannerCreator
  }
})
export default class BannerList extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface;
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('listenerService') readonly listenerService!: ListenerServiceInterface

  selectedDomain: string | null = null;
  selectedChannel!: string;

  domain: DomainItemInterface | null = null

  headers: any[] = []

  loading: boolean = false;
  showModifyBannerDialog: boolean = false;

  banners: Banner[] = [];
  editBannerId: number | null = null;

  beforeMount() {
    this.headers = [
      {
        text: this.translate("UI_NETWORK_ADMIN.CMS.BANNERS_LIST.HEADERS.TITLE.ID"),
        value: 'id'
      },
      {
        text: this.translate("UI_NETWORK_ADMIN.CMS.BANNERS_LIST.HEADERS.TITLE.NAME"),
        value: 'name'
      },
      {
        text: this.translate("UI_NETWORK_ADMIN.CMS.BANNERS_LIST.HEADERS.TITLE.START_DATE"),
        value: 'date'
      },
      {
        text: this.translate("UI_NETWORK_ADMIN.CMS.BANNERS_LIST.HEADERS.TITLE.LOGGED_IN"),
        value: 'loggedIn'
      },
      {
        text: this.translate("UI_NETWORK_ADMIN.CMS.BANNERS_LIST.HEADERS.TITLE.ACTIONS"),
        value: 'actions',
        sortable: false
      }
    ];
  }

  onDomainSelected(item: DomainItemInterface | null) {
    if (item === null) {
      return
    }

    if (this.selectedDomain !== item.name) {
      this.selectedDomain = item.name
      this.loadDomainBanners()
    }
  }

  loadDomainBanners() {
    if (this.selectedDomain != null) {
      this.rootScope.provide.casinoCmsProvider.getDomainBanners(this.selectedDomain).then((banners: Banner[]) => {
        this.banners = banners;
      });
    }
  }

  onCancelBannerEdit() {
    this.showModifyBannerDialog = false;
    this.editBannerId = null;
  }

  onSubmitBanner(banner: Banner) {
    this.showModifyBannerDialog = false;
    this.editBannerId = null;
    if (banner.id) {
      this.rootScope.provide.casinoCmsProvider.updateBanner(this.selectedDomain, banner.id, banner).then((banner) => {
        this.loadDomainBanners();
      })
    } else {
      this.rootScope.provide.casinoCmsProvider.saveBanner(this.selectedDomain, banner).then((banner) => {
        this.loadDomainBanners();
      })
    }

  }


  onItemClick(item: any) {

  }

  onNewBannerClick() {
    this.showModifyBannerDialog = true;
  }

  deleteBanner(banner) {
    this.listenerService.call('dialog-confirm', {
      title: 'Confirm Delete',
      text: 'Are you sure you want to delete banner \'' + banner.name + '\'',
      btnPositive: {
        text: 'Delete',
        color: 'error',
        onClick: () => {
          if (this.selectedDomain) {
            this.rootScope.provide.casinoCmsProvider.deleteBanner(this.selectedDomain, banner.id).then(() => {
              this.loadDomainBanners();
            });
          }
        }
      }
    });
  }

  onUpdateBannerClick(id: number) {
    this.editBannerId = id;
    this.showModifyBannerDialog = true;
  }

  translate(text: string) {
    return this.translateService.instant(text)
  }

  timestampToDateString(timestamp: number): string {
    if (!timestamp) {
      return ''
    }
    return (new Date(timestamp)).toDateString();
  }

}

</script>

<style>
</style>

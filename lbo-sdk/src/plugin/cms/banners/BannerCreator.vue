<template>
  <v-container data-test-id="cnt-banner-creator-">

    <v-card>
      <v-card-title>{{ title }}</v-card-title>
      <v-card-text>
        <v-row no-gutters>
          <v-col cols="12" v-if="selectedDomain && !banner">
            <layout-image-selector-template ocurre @onImageSelect="onBannerImageSelected"
              :selectedDomain="selectedDomain" :type="bannerType"></layout-image-selector-template>
          </v-col>
          <v-col cols="12" v-if="selectedDomain && banner">
            <v-img v-if="banner.imageUrl" :alt="`${banner.name} avatar`" :src="banner.imageUrl"></v-img>
          </v-col>
          <v-col cols="12" v-if="banner">
            <v-stepper v-model="slider.step" non-linear class="elevation-0">
              <v-stepper-header class="elevation-0 rounded" style="border: 1px solid #ccc">
                <v-stepper-step :complete="stepOneComplete" editable :edit-icon="stepOneComplete ? 'mdi-check' : ''"
                  complete-icon="mdi-check" step="1">
                  Basic Details
                </v-stepper-step>
                <v-divider></v-divider>
                <v-stepper-step :complete="stepTwoComplete" editable :edit-icon="stepTwoComplete ? 'mdi-check' : ''"
                  complete-icon="mdi-check" step="2">
                  Scheduling
                </v-stepper-step>
              </v-stepper-header>
              <v-stepper-items v-if="banner">
                <v-stepper-content step="1" class="px-1">
                  <banner-item-editor :banner="banner"></banner-item-editor>
                </v-stepper-content>
                <v-stepper-content step="2" class="px-1">
                  <banner-scheduling @change="onRRuleChange" :schedule="schedule" :banner="banner"></banner-scheduling>
                </v-stepper-content>
              </v-stepper-items>
            </v-stepper>
          </v-col>
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
        </v-row>
      </v-card-text>
      <v-card-actions>
        <v-progress-circular v-if="uploading" color="primary" size="50" indeterminate>{{
          progress
        }}%
        </v-progress-circular>
        <v-spacer></v-spacer>
        <v-btn color="error" text @click.prevent="cancel" :disabled="uploading">Cancel</v-btn>
        <v-btn color="blue" text @click="onSubmit" :disabled="!valid || uploading">Submit</v-btn>
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script lang="ts">
import { Component, Inject, Prop, Vue } from 'vue-property-decorator'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import { RootScopeInterface } from "@/core/interface/ScopeInterface";
import BannerImageInterface from '../interfaces/BannerImageInterface';
import { DomainItemInterface } from "@/plugin/cms/models/DomainItem";
import LayoutBuilderBannerTemplate from "@/plugin/cms/components/LayoutBuilderBannerTemplate.vue";
import { BannerInterface } from "@/plugin/cms/interfaces/BannerInterface";
import BannerItemEditor from "@/plugin/cms/banners/BannerItemEditor.vue";
import BannerScheduling from "@/plugin/cms/banners/BannerScheduling.vue";
import LayoutBannerItem from "@/plugin/cms/models/LayoutBannerItem";
import DomainSelector from "@/plugin/components/DomainSelector.vue";
import { Banner } from "@/plugin/cms/models/Banner";
import LayoutImageSelectorTemplate from '../images/LayoutImageSelectorTemplate.vue';
import CmsAssetType from '../models/CmsAssetType';
import { Schedule } from '@/plugin/promotions/Promotion';
import { rrulestr } from 'rrule';
import { RRuleContract } from '@/plugin/components/RRule';


@Component({
  components: {
    DomainSelector,
    LayoutBuilderBannerTemplate,
    BannerItemEditor,
    BannerScheduling,
    LayoutImageSelectorTemplate
  }
})
export default class BannerCreator extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface;
  @Inject('rootScope') readonly rootScope!: RootScopeInterface

  @Prop() selectedDomain!: string;
  @Prop() editBannerId!: number;
  @Prop() snackbar: boolean = false
  @Prop() snackbarColour: string = ''
  @Prop() snackbarTitle: string = ''

  imageType: string = 'Banner';
  bannerImages: BannerImageInterface[] = [];
  selectedBannerImage: BannerImageInterface | null = null;

  banner: Banner | null = null;

  domain: DomainItemInterface | null = null
  uploading: boolean = false;

  slider = {
    step: 1,
    steps: 3,

    allowNext: true,
    allowPrev: false,
    allowSave: false
  }

  created() {
    this.$watch('editBannerId', (newValue: number | null) => {
      if (newValue !== null) {
        if (this.editBannerId) {
          this.rootScope.provide.casinoCmsProvider.getBanner(this.selectedDomain, newValue).then((banner: Banner) => {
            this.banner = banner;
          })
        }
      }
    })
  }

  get title() {
    return this.editBannerId ? "Edit Banner" : "Create Banner";
  }

  get selectedBanner() {
    return this.banner;
  }

  mounted() {
    if (this.editBannerId) {
      this.rootScope.provide.casinoCmsProvider.getBanner(this.selectedDomain, this.editBannerId).then((banner: Banner) => {
        this.banner = banner;
      })
    }
  }

  onDomainSelected(item: DomainItemInterface | null) {
    if (item === null) {
      return
    }

    if (this.selectedDomain !== item.name) {
      this.selectedDomain = item.name
    }
  }

  onBannerImageSelected(bannerImage: BannerImageInterface) {
    if (!bannerImage) return;
    this.banner = new Banner(bannerImage.name, bannerImage.url);
    this.selectedBannerImage = bannerImage;
  }

  get stepOneComplete(): boolean {
    return !!this.banner
      && !!this.banner.name
      && !!this.banner.imageUrl
  }

  get stepTwoComplete(): boolean {
    return !!this.banner
      && !!this.banner.recurrencePattern
      && !!this.banner.startDate
      && !!this.banner.lengthInDays
      && this.banner.lengthInDays > 0
  }

  cancel() {
    this.$emit('banner-edit-cancel')
    this.banner = null;
  }

  onSubmit() {
    if (this.banner && this.banner.displayText && this.banner.displayText.length > 512) {
      this.buildSnackBarProperties("invalid");
      return
    }
    this.$emit('banner-edit-submit', this.banner)
    this.banner = null;
  }

  get valid(): boolean {

    return this.stepOneComplete && this.stepTwoComplete;
  }

  onRRuleChange(newRule) {
    if (this.banner) {
      this.banner.recurrencePattern = newRule.rule.toString();
      console.log("Pattern: ", this.banner.recurrencePattern)
      this.banner.startDate = newRule.start as Date;
      this.banner.singleDay = newRule.singleDay as boolean;
      this.banner.lengthInDays = Number(newRule.length);
    }
  }

  translate(text: string) {
    return this.translateService.instant(text)
  }

  get bannerType() {
    return CmsAssetType.Banner;
  }

  get schedule(): RRuleContract | null {
    if (!this.banner?.recurrencePattern) return null;
    const rrule = rrulestr(this.banner.recurrencePattern);

    const newSchedule: RRuleContract = {
      rruleString: this.banner.recurrencePattern,
      lengthInDays: '' + this.banner.lengthInDays,
      dateStart: rrule.options.dtstart,
      dateUntil: rrule.options.until
    }

    return newSchedule;
  }

  buildSnackBarProperties(type: string) {
    switch (type) {
      case "invalid":
        this.snackbarColour = "error";
        this.snackbarTitle = this.translate("UI_NETWORK_ADMIN.CMS.BANNER_CREATION.DISPLAY_TEXT.ERROR.MESSAGE");
        this.snackbar = true;
        break;
    }
  }

}

</script>

<style>

</style>

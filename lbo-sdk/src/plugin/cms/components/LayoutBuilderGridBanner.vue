<template>
  <div>

  </div>
</template>

<script lang='ts'>
import {Component, Prop, Vue, Watch} from 'vue-property-decorator'
import LayoutGameEntryItem, {TileSizeEnum} from '../models/LayoutGameEntryItem'
import LayoutBannerItem from '../models/LayoutBannerItem'
import LobbyItem from '../models/LobbyItem'
import LayoutBuilderGameItem from './LayoutBuilderGameItem.vue'
import LayoutBuilderGameRemoveTile from './LayoutBuilderGameRemoveTile.vue'
import draggable from "vuedraggable"
import {LayoutWidgetEntryTypeEnum} from "@/plugin/cms/models/LayoutGameEntryTypeEnum";
import LayoutBuilderBannerTemplate from "@/plugin/cms/components/LayoutBuilderBannerTemplate.vue";
import BannerImageInterface from "@/plugin/cms/interfaces/BannerImageInterface";
import LayoutBannerEntryItem from "@/plugin/cms/models/LayoutBannerEntryItem";
import {PageBanner} from "@/plugin/cms/models/PageBanner";
import {RRule, rrulestr} from 'rrule'
import { Banner } from '../models/Banner'

@Component({
  components: {
    LayoutBuilderBannerTemplate,
    LayoutBuilderGameItem,
    LayoutBuilderGameRemoveTile,
    draggable
  }
})
export default class LayoutBuilderGridBanner extends Vue {
  @Prop({required: true}) lobbyItem!: LobbyItem
  @Prop({required: true}) banners!: PageBanner[];
  @Prop({default: false}) allowTitle!: boolean
  @Prop() selectedDomain!: string;
  @Prop() selectedChannel!: string;

  selectedGame: BannerImageInterface | null = null;
  selected: BannerImageInterface | null = null;

  pageBannersListChanged = false;

  get type() {
    return LayoutWidgetEntryTypeEnum.BANNER;
  }

  @Watch("banners", {deep: true})
  watchPageBanners(newPageBanners) {
    if (newPageBanners.length != this.banners.length) {
      this.pageBannersListChanged = true;
      return;
    } else {
      let i = 0;
      for(; i < this.banners.length; i++) {
        if (newPageBanners[i] !== this.banners[i]) {
          this.pageBannersListChanged = true;
          return;
        }
      }
    }
    this.pageBannersListChanged = false;
  }

  onBannerSelected(banner: Banner) {
    if(this.banners && banner) {
    const primaryNavCode = this.lobbyItem.page.primary_nav_code;
    const secondaryNavCode = this.lobbyItem.page.secondary_nav_code;
    const position = this.banners.length + 1;

      const pageBanner = new PageBanner(null, primaryNavCode, secondaryNavCode, position, this.selectedChannel, banner);
      if (this.banners.filter(x => x.banner.name === banner.name).length === 0) {
        this.banners.push(pageBanner);
      }
    }
  }

  rruleObject(rule: string): string {
    return rrulestr(rule).toText();
  }

  onRemovePageBannerClick(pageBanner: PageBanner) {
    this.$emit("remove-page-banner", pageBanner);
  }
}
</script>


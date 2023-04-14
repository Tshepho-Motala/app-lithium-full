import LayoutBannerItem from "@/plugin/cms/models/LayoutBannerItem";
import {LayoutBannerEntryTypeEnum} from "@/plugin/cms/models/LayoutBannerEntryTypeEnum";

export enum TileSizeEnum {
  STANDARD = 'standard',
  CUSTOM = 'custom'
}

export default class LayoutBannerEntryItem {
  type : LayoutBannerEntryTypeEnum | null;
  banners: LayoutBannerItem[] = [];

  constructor(type: LayoutBannerEntryTypeEnum | null) {
    this.type = type;
  }

  addBanner(banner: LayoutBannerItem) {
    this.banners.push(banner)
  }

  removeGameTile(gameId: string) {
    let index = this.banners.findIndex(x => x.gameID === gameId);
    this.banners.splice(index, 1);
  }
}

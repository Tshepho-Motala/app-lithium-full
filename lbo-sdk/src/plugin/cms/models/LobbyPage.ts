import ChannelItem from "./ChannelItem";
import LayoutBannerEntryItem from "./LayoutBannerEntryItem";
import LayoutGridList from "./LayoutGridList";
import LayoutType from "./LayoutType";
import LayoutWidgetList from "./LayoutWidgetList";
import LayoutBannerList from "@/plugin/cms/models/LayoutBannerList";
import LayoutGameEntryItem from "@/plugin/cms/models/LayoutGameEntryItem";

export default class LobbyPage {
  channel: string = ''
  primary_nav_code = '';
  secondary_nav_code = '';
  layout = ''

  banners: LayoutBannerEntryItem[] = []

  widgets: any[] = []
  gameGrid: LayoutGridList = new LayoutGridList()
  gameBanner: LayoutBannerList = new LayoutBannerList()

  // get widgets(): (LayoutGameEntryItem)[] {
  //   return [
  //     ...this.banners,
  //     ...this.widgets.entries,
  //   ]
  // }

  get banner(): (LayoutBannerEntryItem | LayoutGameEntryItem)[] {
    return [
      //...this.widgets,
      ...this.gameBanner.getEntries().concat(this.widgets),
    ]
  }

  setChannel(channel: ChannelItem | null) {
    if(channel === null) {
      return
    }
    this.channel = channel.name
  }

  hasChannel(channel: ChannelItem): boolean {
    if(this.channel === null) {
      return false
    }
    return this.channel === channel.name
  }

  setLayout(layout: LayoutType) {
    this.layout = layout.title
  }

  toJSON(): any {
    return {
      channel: this.channel,
      widgets: this.banner,
      //banner: this.banner,
      primary_nav_code: this.primary_nav_code,
      secondary_nav_code: this.secondary_nav_code
    }
  }
}

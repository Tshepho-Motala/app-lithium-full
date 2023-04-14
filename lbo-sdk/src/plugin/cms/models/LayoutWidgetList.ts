import LayoutGameEntryItem from './LayoutGameEntryItem'
import LayoutBannerEntryItem from "@/plugin/cms/models/LayoutBannerEntryItem";
import LayoutBannerItem from "@/plugin/cms/models/LayoutBannerItem";

export default class LayoutWidgetList {
  entries: any[] = []

  addEntry(entry: LayoutGameEntryItem) {
      this.entries.push(entry)
  }

}

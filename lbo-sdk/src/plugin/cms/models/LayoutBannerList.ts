import LayoutBannerEntryItem from "@/plugin/cms/models/LayoutBannerEntryItem";

export default class LayoutBannerList {
    entries: any[] = []

    addEntry(entry: LayoutBannerEntryItem) {
        this.entries.push(entry)
    }

    getEntries() {
        return this.entries;
    }


}

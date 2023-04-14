import LayoutGameEntryItem, {TileSizeEnum} from './LayoutGameEntryItem'
import {LayoutWidgetEntryTypeEnum} from "@/plugin/cms/models/LayoutGameEntryTypeEnum";
import {TileWidgetTypeEnum} from "@/plugin/cms/models/TileWidgetTypeEnum";

export default class LayoutGridList {
  entry: LayoutGameEntryItem = new LayoutGameEntryItem(TileWidgetTypeEnum.GRID, '', TileSizeEnum.STANDARD)
}

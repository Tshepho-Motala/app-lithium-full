import {LayoutWidgetEntryTypeEnum} from "@/plugin/cms/models/LayoutGameEntryTypeEnum"
import {TileWidgetTypeEnum} from "@/plugin/cms/models/TileWidgetTypeEnum"
import LayoutGameEntryItem, { TileSizeEnum } from "./LayoutGameEntryItem"
import LayoutProgressiveGameItem from "./LayoutProgressiveGameItem"
import ProgressiveWidgetLink from "./ProgressiveWidgetLink"


export default class LayoutJackpotGameEntryItem extends LayoutGameEntryItem{
  description: string = ''
  widgetLink: ProgressiveWidgetLink | null = null
  progressives: LayoutProgressiveGameItem[] = []
  jackpotLogo?: string
  id: string = ''

  constructor(tileWidgetType: TileWidgetTypeEnum | null, title: string, tileSize: TileSizeEnum) {
    super(tileWidgetType, title, tileSize)
  }

  addProgressive(tile: LayoutProgressiveGameItem) {
    this.progressives.push(tile)
  }

  removeGameTile(gameId: string) {
    let index = this.tiles.findIndex(x => x.gameID === gameId)
    this.tiles.splice(index, 1)
  }

  get getType(): LayoutWidgetEntryTypeEnum | null {
    switch (this.tileWidgetType) {
      case TileWidgetTypeEnum.BANNER:
        return LayoutWidgetEntryTypeEnum.BANNER
      case TileWidgetTypeEnum.GRID:
        return LayoutWidgetEntryTypeEnum.GRID
      case TileWidgetTypeEnum.TILES:
        return LayoutWidgetEntryTypeEnum.TILES
      case TileWidgetTypeEnum.TOP_GAMES:
        return LayoutWidgetEntryTypeEnum.TOP_GAMES
      case TileWidgetTypeEnum.RECENTLY_PLAYED_GAMES:
        return LayoutWidgetEntryTypeEnum.TILES
      case TileWidgetTypeEnum.ATOZ:
        return LayoutWidgetEntryTypeEnum.ATOZ
      case TileWidgetTypeEnum.RECOMMENDED_GAMES:
        return LayoutWidgetEntryTypeEnum.TILES
      case TileWidgetTypeEnum.TAGGED_GAMES:
        return LayoutWidgetEntryTypeEnum.TILES
      default:
        return null
    }
  }

  get showWidgetEntries(): boolean {
    if (this.tileWidgetType === TileWidgetTypeEnum.RECENTLY_PLAYED_GAMES
        || this.tileWidgetType === TileWidgetTypeEnum.ATOZ
        || this.tileWidgetType === TileWidgetTypeEnum.RECOMMENDED_GAMES
        || this.tileWidgetType === TileWidgetTypeEnum.TAGGED_GAMES) {
      return false
    }
    return true
  }


}

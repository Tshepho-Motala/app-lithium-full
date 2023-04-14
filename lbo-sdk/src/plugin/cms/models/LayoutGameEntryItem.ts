import LayoutGameItem from "@/plugin/cms/models/LayoutGameItem"
import {LayoutWidgetEntryTypeEnum} from "@/plugin/cms/models/LayoutGameEntryTypeEnum"
import {TileWidgetTypeEnum} from "@/plugin/cms/models/TileWidgetTypeEnum"

export enum TileSizeEnum {
  STANDARD = 'standard',
  CUSTOM = 'custom',
  EMPTY = ''
}

export default class LayoutGameEntryItem {
  type: LayoutWidgetEntryTypeEnum | null
  title = ''
  tiles: LayoutGameItem[] = []
  tile_size = TileSizeEnum.STANDARD
  tileWidgetType: TileWidgetTypeEnum | null

  constructor(tileWidgetType: TileWidgetTypeEnum | null, title: string, tileSize: TileSizeEnum) {
    this.title = title
    this.tile_size = tileSize
    this.tileWidgetType = tileWidgetType
    this.type = this.getType
  }

  addTile(tile: LayoutGameItem) {
    this.tiles.push(tile)
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
      case TileWidgetTypeEnum.JACKPOT_GRID:
        return LayoutWidgetEntryTypeEnum.JACKPOT_GRID
      case TileWidgetTypeEnum.JACKPOT_TILE:
        return LayoutWidgetEntryTypeEnum.JACKPOT_TILE
      case TileWidgetTypeEnum.DFG:
        return LayoutWidgetEntryTypeEnum.DFG
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

import { nanoid } from 'nanoid'
import LayoutBannerItem from './LayoutBannerItem'

export default class GameProvider {
  id = nanoid()
  active = false

  constructor(public name: string, public games: LayoutBannerItem[] = [], public domain: string, public url: string) {}

  addGame(game: LayoutBannerItem) {
    this.games.push(game)
  }
}

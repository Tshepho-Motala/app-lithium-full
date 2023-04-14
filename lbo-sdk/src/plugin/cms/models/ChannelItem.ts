export default class ChannelItem {
  id: number
  name: string

  constructor(id: number, name: string) {
    this.id = id
    this.name = name
  }

  toJSON(): any {
    return this.name
  }
}

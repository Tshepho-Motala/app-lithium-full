import LobbyItem from './models/LobbyItem'

class Data {
  domain = ''
  channel = ''
  
  revisions: LobbyItem[] = []
  defaultRevisionId: string | null = null

  get defaultRevision(): LobbyItem | null {
    if(this.defaultRevisionId === null) {
      return null
    }
    const revision = this.revisions.find(r => r.id === this.defaultRevisionId)
    return revision || null
  }

  getRevision(revisionId: string): LobbyItem | undefined {
    return this.revisions.find((r) => r.id === revisionId)
  }

  setDefaultRevision(lobbyItem: LobbyItem) {
    this.defaultRevisionId = lobbyItem.id
  }
}

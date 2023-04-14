import LobbyPage from './LobbyPage'
import LobbyNavItem from "@/plugin/cms/models/LobbyNavItem";
import Badge from "@/plugin/cms/models/Badge";

export default class  LobbyItem {
  // METADATA
  name = ''
  domain: string | null = null
  version = '1'
  id = ''
  active = false

  get status(): string {
    return this.active ? 'Active' : 'Inactive'
  }

  // REQUIRED PROPERTIES
  state = ''
  maintenance = false
  nav: LobbyNavItem[] = [new LobbyNavItem()];
  badges: Badge[] = [];
  page: LobbyPage = new LobbyPage();

  hasDomain(domain: string): boolean {
    if(this.domain === null) {
      return false
    }
    return this.domain === domain
  }

  toJSON(): any {
    return {
      state: this.state,
      maintenance: this.maintenance,
      name: this.name,
      nav: this.nav,
      badges: this.badges,
      page: this.page
    }
  }
}

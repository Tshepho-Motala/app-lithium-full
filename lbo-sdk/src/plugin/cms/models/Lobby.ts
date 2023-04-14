import LobbyItem from './LobbyItem'
import LobbyNavItem from "@/plugin/cms/models/LobbyNavItem";
import Badge from "@/plugin/cms/models/Badge";
import User from "@/plugin/cms/models/User";

export default class Lobby {

  id: number = 0;
  name: string;
  description: string = '';
  version: number = 1;
  active: boolean = false;
  modifiedDate: Date | null = null;
  modifiedBy: User = new User();
  lobbyItems: LobbyItem[] = [];
  channel: string;
  nav: LobbyNavItem[] = [new LobbyNavItem()];
  badges: Badge[] = [];

  public constructor(name: string, channel: string) {
    this.name = name;
    this.channel = channel;
  }

  get status(): string {
    return this.active ? 'Active' : 'Inactive'
  }

  addLobbyItem(item: LobbyItem, channel: string) {
    item.nav = this.nav;
    item.badges = this.badges;
    item.page.primary_nav_code = this.nav[0].primary_nav_code;
    item.page.channel = channel;
    item.state = "logged_in";
    item.name = this.name
    this.lobbyItems.push(item);
  }

}

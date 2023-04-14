import SubNavItem from "@/plugin/cms/models/SubNavItem";

export default class LobbyNavItem {
    code: string = '';
    title: string = '';
    primary_nav_code = '';
    nav: SubNavItem[] = [];

    // getCopy(): LobbyNavItem {
    //     let lobbyNavItemCopy = new LobbyNavItem();
    //     lobbyNavItemCopy.code = this.code;
    //     lobbyNavItemCopy.title = this.title;
    //     lobbyNavItemCopy.primary_nav_code = this.primary_nav_code;
    //     this.nav.forEach(subNavItem => lobbyNavItemCopy.nav.push(subNavItem.getCopy()));
    //     return lobbyNavItemCopy;
    // }

    toJSON(): any {
        return {
            code: this.code,
            title: this.title,
            nav: this.nav
        }
    }
}
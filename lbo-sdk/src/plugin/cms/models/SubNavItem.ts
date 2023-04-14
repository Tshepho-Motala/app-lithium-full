export default class SubNavItem {
    code: string = '';
    title: string = '';

    getCopy(): SubNavItem {
        const subNavItem = new SubNavItem();
        subNavItem.code = this.code;
        subNavItem.title = this.title;
        return subNavItem;
    }

}
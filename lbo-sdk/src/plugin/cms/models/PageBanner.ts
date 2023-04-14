import {Banner} from "@/plugin/cms/models/Banner";

export class PageBanner {

    id: number| null;
    channel: string;
    primaryNavCode: string;
    secondaryNavCode: string;
    position: number;
    deleted: boolean = false;
    banner: Banner;


    constructor(id: number | null, primaryNavCode: string, secondaryNavCode: string, position: number, channel: string, banner: Banner) {
        this.id = id;;
        this.primaryNavCode = primaryNavCode;
        this.secondaryNavCode = secondaryNavCode;
        this.position = position;
        this.channel = channel;
        this.banner = banner;
    }

}
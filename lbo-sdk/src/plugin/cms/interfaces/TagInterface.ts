import {DomainItemInterface} from "@/plugin/cms/models/DomainItem";

export interface TagInterface {
    id: number;
    name: string;
    domain: DomainItemInterface;
}
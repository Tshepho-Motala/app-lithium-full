import DomainItem from "@/plugin/cms/models/DomainItem";

export class Banner {
    id!: number;
    version: number = 0;
    name: string;
    imageUrl: string;
    startDate: Date;
    timeFrom: string | null = null;
    timeTo: string | null = null;
    link: string = '';
    recurrencePattern: string = '';
    loggedIn: boolean | null;
    termsUrl!: string;
    displayText!: string;
    lengthInDays: number = 1;
    singleDay!: boolean;
    deleted: boolean = false;

    constructor(name: string, imageUrl: string) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.startDate = new Date()
        this.loggedIn = null;
    }
}
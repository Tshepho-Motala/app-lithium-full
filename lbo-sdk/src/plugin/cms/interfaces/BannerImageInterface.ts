export interface BannerImageResponseInterface {
    data: BannerImageInterface[];
    data2?: any;
    message?: any;
    status: number;
    successful: boolean;
}

export default interface BannerImageInterface {
    id: number;
    deleted: boolean;
    version: number;
    domain: BannerImageDomainInterface;
    name: string;
    url: string;
    type: string;
    uploadedDate: Date;
    size: string;
}

export interface BannerImageDomainInterface {
    id: number;
    version: number;
    name: string;
}





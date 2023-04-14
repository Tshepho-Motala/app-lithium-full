import BannerImageInterface from "@/plugin/cms/interfaces/BannerImageInterface";

export interface ResponseInterface<T> {
    data: T;
    data2?: any;
    message?: any;
    status: number;
    successful: boolean;
}
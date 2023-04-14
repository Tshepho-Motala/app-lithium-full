import CmsPaginatedRequest  from "@/plugin/cms/models/CmsPaginatedRequest";

export default interface CmsProviderInterface {
     getAssets(request: CmsPaginatedRequest): Promise<any>;
     upload(domain: string, data: FormData, onProgress: Function): Promise<any>;
     getProviderConfig(domain: string): Promise<any>;
     deleteImage(domain: string, image: any): Promise<any>;
     findAssetByNameAndDomainAndType(name: string, domain: string, type: string): Promise<any>;
}
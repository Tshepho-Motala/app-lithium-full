import CmsProviderInterface from "@/core/interface/provider/CmsProviderInterface";
import { CmsAsset } from "@/plugin/cms/models/CmsAsset";
import CmsAssetType from "@/plugin/cms/models/CmsAssetType";
import CmsPaginatedRequest from "@/plugin/cms/models/CmsPaginatedRequest";

export default class CmsProviderMock implements CmsProviderInterface{
    private assets: Array<CmsAsset>  = [
        {id: 1,name: 'Anele', size:'20', type: CmsAssetType.Tile, uploadedDate: '2021-06-06 13:21:01', url:'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'},
        {id: 2,name: 'Rivalani', size:'15', type: CmsAssetType.Banner, uploadedDate: '2021-06-06 13:21:01', url:'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'},
        {id: 3,name: 'Darren', size:'20', type: CmsAssetType.Tile, uploadedDate: '2021-06-06 13:21:01', url:'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'},
        {id: 4,name: 'Anathi', size:'12', type: CmsAssetType.Tile, uploadedDate: '2021-06-06 13:21:01', url:'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'},
        {id: 5,name: 'Senzo', size:'23', type: CmsAssetType.Banner, uploadedDate: '2021-06-06 13:21:01', url:'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'},
        {id: 6,name: 'Busi', size:'10', type: CmsAssetType.Tile, uploadedDate: '2021-06-06 13:21:01', url:'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'},

        {id: 5,name: 'Senzo', size:'23', type: CmsAssetType.Font, uploadedDate: '2021-06-06 13:21:01', url:'https://lbo.lithium-develop.ls-g.net/webassets/app.tiff'},
        {id: 6,name: 'Busi', size:'10', type: CmsAssetType.Style, uploadedDate: '2021-06-06 13:21:01', url:'https://lbo.lithium-develop.ls-g.net/webassets/app.css'}
    ];

    getAssets(request: CmsPaginatedRequest): Promise<any> {
        return new Promise<any>((resolve, reject) => {
            setTimeout(() => {

                let items = this.assets.filter(s => s.type === request.type);
                resolve({
                    data: {
                        totalItems: items.length,
                        data: items
                    }
                });
            }, 2000)
        });
    }

    upload(domain: string, data: FormData, onProgress: Function): Promise<any> {
        let progress = 0;

        return new Promise((resolve, reject) => {
            let interval = setInterval(() => {
                progress +=20;
                onProgress(progress)

                if(progress === 100) {
                    clearInterval(interval);
                    resolve('Done');
                };

            }, 1000)
        })
    }

    getProviderConfig(domain: string): Promise<any> {
        return new Promise((resolve, reject) => {
            resolve({
                uri: 'https://lbo.lithium-develop.ls-g.net/',
                bucketCmsImagePrefix: 'cms-images'
            })
        });
    }

    deleteImage(domain: string, image: any): Promise<any> {
        return new Promise((resolve, reject) => {
            resolve('deleted')
        })
    }

    findAssetByNameAndDomainAndType(name: string, domain: string, type: string): Promise<any> {
        return new Promise((resolve, reject) => {
            let random = Math.floor(Math.random()* 2);
            
            if(random) {
                return resolve(this.assets[0]);
            }

            return resolve(null);
        });
    }

}

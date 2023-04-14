import BannerImageInterface from "@/plugin/cms/interfaces/BannerImageInterface";
import BannerImageProviderMockInterface from "@/core/interface/provider/BannerImageProviderMockInterface";

export default class BannerImageProviderMock implements BannerImageProviderMockInterface{
    findByDomainName(domainName: string): Promise<BannerImageInterface[]> {
        // Shamelessly dumped from the API
        return new Promise((res) => {
Promise.resolve(JSON.parse(`[
        {
            "id": 1,
            "deleted": false,
            "version": 0,
            "domain": {
                "id": 3,
                "name": "livescore_uk"
            },
            "name": "victor",
            "url": "https://ppc2.lsb-uk.ls-g.net/uk/about-us/3K-Lightning-Blackjack-Giveaway-39909.png",
            "type": "png",
            "uploadedDate": 1636650491000,
            "size": "standard"
        }
    ]`))
                .then(banners => {
                    banners = banners.filter(banner => banner.isBanner == true)
                    res(banners)
                })
        })
    }

    findByDomainNameAndType(domainName: string, type: string): Promise<BannerImageInterface[]> {
        console.log("Domain name: ")
        return new Promise((res) => {
            Promise.resolve(JSON.parse(`[
        {
            "id": 1,
            "deleted": false,
            "version": 0,
            "domain": {
                "id": 3,
                "name": "livescore_uk"
            },
            "name": "victor",
            "url": "https://ppc2.lsb-uk.ls-g.net/uk/about-us/3K-Lightning-Blackjack-Giveaway-39909.png",
            "type": "png",
            "uploadedDate": 1636650491000,
            "size": "standard",
            "isBanner": true
        }
    ]`))
                .then(banners => {
                    // banners = banners.filter(banner => banner.isBanner == true)
                    console.log("Banners: ", banners)
                    res(banners)
                })
        })
    }
}
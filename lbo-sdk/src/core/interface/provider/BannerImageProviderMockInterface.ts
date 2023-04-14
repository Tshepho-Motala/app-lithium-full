import BannerImageInterface from "@/plugin/cms/interfaces/BannerImageInterface";

export default interface BannerImageProviderMockInterface {
    findByDomainName(domainName: string): Promise<BannerImageInterface[]>
    findByDomainNameAndType(domainName: string, type: string): Promise<BannerImageInterface[]>
}
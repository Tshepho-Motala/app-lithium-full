import {
    DomainDropDown,
    TagDropDown,
    RestrictionDropDown,
    StatusDropDown,
    GameSupplierDropDown,
    GameProviderDropDown, GameConfigsDropDown, AffiliateParamsInterface
} from '@/core/interface/DropDownMenuInterface'

export default interface DropDownMenuInterface {
    domainsChange(data:DomainDropDown[]):Promise<any>
    domainList():Promise<DomainDropDown[]>
    tagsChange(data:TagDropDown[]):Promise<any>
    tagList():Promise<TagDropDown[]>
    statusesChange(data:StatusDropDown[]):Promise<any>
    statusList():Promise<TagDropDown[]>
    ecosystemList():Promise<TagDropDown[]>
    restrictionsList():Promise<RestrictionDropDown[]>
    restrictionsChange(data:RestrictionDropDown[]):Promise<any>
    ecosystemChange(data:any[]):Promise<any>
    gameSuppliersChange(data:GameSupplierDropDown[]):Promise<any>
    gameSuppliersList():Promise<GameSupplierDropDown[]>
    gameProvidersChange(data:GameProviderDropDown[]):Promise<any>
    gameProvidersList():Promise<GameProviderDropDown[]>
    gameConfigsChange(data:GameConfigsDropDown[]):Promise<any>
    gameConfigsList():Promise<GameConfigsDropDown[]>
    changeAffiliate(data:AffiliateParamsInterface[]):Promise<any>
}
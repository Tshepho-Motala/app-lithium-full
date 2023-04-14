import DropDownMenuInterface from "@/core/interface/provider/DropDownMenuInterface";
import {
    DomainDropDown,
    TagDropDown,
    RestrictionDropDown,
    StatusDropDown,
    GameSupplierDropDown, GameProviderDropDown, GameConfigsDropDown, AffiliateParamsInterface
} from '@/core/interface/DropDownMenuInterface'
export default class DropDownMenuProviderMock implements DropDownMenuInterface {
    domainsChange(data:DomainDropDown[]): Promise<any> {
        return new Promise((res, rej) => {});
    }
    domainList() : Promise<DomainDropDown[]> {
        return new Promise((res, rej) => {});
    }

    tagsChange(data:TagDropDown[]): Promise<any> {
        return new Promise((res, rej) => {});
    }
    tagList() : Promise<TagDropDown[]> {
        return new Promise((res, rej) => {});
    }
    statusesChange(data:StatusDropDown[]): Promise<any> {
        return new Promise((res, rej) => {});
    }
    statusList() : Promise<TagDropDown[]> {
        return new Promise((res, rej) => {});
    }
    ecosystemList() : Promise<TagDropDown[]> {
        return new Promise((res, rej) => {});
    }
    restrictionsList() : Promise<RestrictionDropDown[]> {
        return new Promise((res, rej) => {});
    }
    restrictionsChange(data:RestrictionDropDown[]): Promise<any> {
        return new Promise((res, rej) => {});
    }
    ecosystemChange(data:RestrictionDropDown[]): Promise<any> {
        return new Promise((res, rej) => {});
    }

    gameSuppliersChange(data: GameSupplierDropDown[]): Promise<any> {
        return new Promise((res, rej) => {});
    }

    gameSuppliersList(): Promise<GameSupplierDropDown[]> {
        return new Promise((res, rej) => {});
    }

    gameProvidersChange(data: GameProviderDropDown[]): Promise<any> {
        return new Promise((res, rej) => {});
    }

    gameProvidersList(): Promise<GameProviderDropDown[]> {
        return new Promise((res, rej) => {});
    }

    gameConfigsChange(data: GameConfigsDropDown[]): Promise<any> {
        return new Promise((res, rej) => {});
    }

    gameConfigsList(): Promise<GameConfigsDropDown[]> {
        return new Promise((res, rej) => {});
    }

    changeAffiliate(): Promise<AffiliateParamsInterface[]> {
        return new Promise((res, rej) => {});
    }
}
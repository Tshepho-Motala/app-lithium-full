import {AxiosResponse} from "axios";
import {AffiliateParamsInterface} from "@/core/interface/DropDownMenuInterface";
export default interface UserApiInterface {
    userFindFromGuid(domainName:string, params: any): Promise<AxiosResponse>
    userGetRestrictions(domainName:string, params: any): Promise<AxiosResponse>
    tranTypeSummaryByOwnerGuid(domainName:string, params: any): Promise<AxiosResponse>
    summaryAccountByOwnerGuid(domainName:string, params: any): Promise<AxiosResponse>
    getEscrowWalletPlayerBalance(params: any): Promise<AxiosResponse>
    userBalance(domainName: string, accountCode:string,accountType:string,currencyCode:string, ownerGuid:string): Promise<AxiosResponse>
    userAffiliates(params: AffiliateParamsInterface): Promise<AxiosResponse>
    userList(domainName:string, params: any): Promise<AxiosResponse>
}
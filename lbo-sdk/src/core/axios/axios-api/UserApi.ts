import {AxiosResponse} from "axios";
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import ApiListUrl from "@/core/api/ApiListUrl";
import UserApiInterface from "@/core/interface/axios-api/UserApiInterface";
import {AffiliateParamsInterface} from "@/core/interface/DropDownMenuInterface";
import AxiosBasicClient from '../AxiosBasicClient';

export default class UserApi extends AxiosBasicClient implements UserApiInterface {

    // !IMPORTANT!  We need to send "userService" to this Class
    constructor(userService: UserServiceInterface) {
        super(userService)
    }


    userFindFromGuid(domainName:string, params: any): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.userApi.userFindFromGuidUrl(domainName), {params: params});
    }

    userGetRestrictions(domainName:string, params: any): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.userApi.userGetRestrictionsUrl(domainName), {params: params});
    }



    tranTypeSummaryByOwnerGuid(domainName:string, params: any): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.userApi.tranTypeSummaryByOwnerGuidUrl(domainName), {params: params});
    }

    summaryAccountByOwnerGuid(domainName:string, params: any): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.userApi.summaryAccountByOwnerGuidUrl(domainName), {params: params});
    }
    getEscrowWalletPlayerBalance (params: any): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.userApi.getEscrowWalletPlayerBalanceUrl(), {params: params});
    }

    userBalance(domainName: String, accountCode:String,accountType:String,currencyCode:String, ownerGuid:String): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.userApi.userBalanceUrl(domainName,accountCode,accountType, currencyCode,ownerGuid));
    }

    userAffiliates(params: AffiliateParamsInterface): Promise<AxiosResponse> {
        return this.provider.post(ApiListUrl.userApi.userAffiliatesUrl(),  null,  {params: params},);
    }

    userList(domainName:string, params: any): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.userApi.userListUrl(domainName), {params: params});
    }

}

import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import MailApiInterface from "@/core/interface/axios-api/MailApiInterface";
import {AxiosResponse} from "axios";
import ApiListUrl from "@/core/api/ApiListUrl";
import AxiosBasicClient from "../AxiosBasicClient";

export default class MailApi extends AxiosBasicClient implements MailApiInterface {

    // !IMPORTANT!  We need to send "userService" to Class  "AxiosApiClient"
    constructor(userService: UserServiceInterface) {
        super(userService)
    }

    loadMailTemplates(domainName: String): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.mailApi.loadMailTemplatesUrl(domainName));
    }

    loadMailTemplate(id: number): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.mailApi.loadMailTemplateUrl(id));
    }

    loadMailPlaceholder(id: number, params: any): Promise<AxiosResponse> {
        return this.provider.get(ApiListUrl.mailApi.loadMailPlaceholderUrl(id), {params: params});
    }

    sendUserTemplateMail(id: number, params: any): Promise<AxiosResponse> {
        return this.provider.post(ApiListUrl.mailApi.sendUserTemplateMailUrl(id), null, {params: params});
    }

    sendUserMail(id: number, user: number, params: any): Promise<AxiosResponse> {
        return this.provider.post(ApiListUrl.mailApi.sendUserMailUrl(id, user), params);
    }
}
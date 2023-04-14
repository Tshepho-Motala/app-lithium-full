import {AxiosResponse} from "axios";

export default interface MailApiInterface {
    loadMailTemplates(domainName: String): Promise<AxiosResponse>
    loadMailTemplate(id: number): Promise<AxiosResponse>
    loadMailPlaceholder(id: number, params:any): Promise<AxiosResponse>
    sendUserTemplateMail(id: number, params:any): Promise<AxiosResponse>
    sendUserMail(id: number,user:number, params:any): Promise<AxiosResponse>
}
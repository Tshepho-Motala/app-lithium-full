import CmsProviderInterface from "@/core/interface/provider/CmsProviderInterface";
import { RootScopeInterface } from "@/core/interface/ScopeInterface";
import ListenerServiceInterface from "@/core/interface/service/ListenerServiceInterface";
import LogServiceInterface from "@/core/interface/service/LogServiceInterface";
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import { isEmpty } from "@/core/utils/objectUtils";
import { Component, Inject, Prop, Vue } from "vue-property-decorator";
import CmsPaginatedRequest from "../models/CmsPaginatedRequest";
import {format, utcToZonedTime} from "date-fns-tz";


@Component
export default class AssetTabMixin extends Vue {
    @Prop({ required: true, type: String })
    domain!: string;

    providerConfig = {};

    options: any = {};

    totalItems = 0;

    currentPage = 1;

    assets = [];

    showDialog = false;

    loading = false;

    @Inject('userService')
    readonly userService!: UserServiceInterface;

    @Inject("rootScope")
    readonly rootScope!: RootScopeInterface;

    @Inject("logService")
    logService!: LogServiceInterface;

    @Inject("listenerService")
    readonly listenerService!: ListenerServiceInterface;

    get hasConfig(): boolean {
        return !isEmpty(this.providerConfig);
    }

    get cms(): CmsProviderInterface {
        return this.rootScope.provide.cmsProvider;
    }

    canDelete(role: string) {
        return this.userService.hasRoleForDomain(this.domain, role) && this.hasConfig;
    }

    canAdd(role: string) {
        return this.userService.hasRoleForDomain(this.domain, role);
    }

    async getConfig() {
        try {
            this.providerConfig = await this.cms.getProviderConfig(this.domain);
        } catch (error) {
            this.logService.error(error);
        }
    }

    async getAssets(type: string) {
        this.loading = true;

        const { page, sortBy, descending, itemsPerPage } = this.options;

        let sortDirection = descending ? "desc" : "asc";

        try {
            let response = await this.cms.getAssets({
                domain: this.domain,
                type: type,
                size: itemsPerPage || 20,
                sortBy: (sortBy && sortBy[0]) || "uploadedDate",
                sortOrder: sortDirection,
                page: page || 1,
            } as CmsPaginatedRequest);

            this.assets = response.data.data;
            this.totalItems = response.data.totalItems;
        } catch (error) {
            this.logService.error(error);
        }

        this.loading = false;
    }


    convertSize(size: string): string {
        return (parseInt(size) / 1000).toFixed(2) + ' KB'
    }

    timestampToDate(timestamp: string): string {
        let localtimestamp = parseInt(timestamp)
        let date = new Date(localtimestamp);
        let month: any = date.getMonth() + 1;
        let day: any = date.getDate();
        let hours: any = date.getHours()
        let mins: any = date.getMinutes()
        let secs: any = date.getSeconds()

        month = month < 10 ? '0' + month : month;
        day = day < 10 ? '0' + day : day;
        hours = hours < 10 ? '0' + hours : hours;
        mins = mins < 10 ? '0' + mins : mins;
        secs = secs < 10 ? '0' + secs : secs;

        return `${date.getFullYear()}-${month}-${day} ${hours}:${mins}:${secs}`
    }

    onFormClose() {
        this.showDialog = false;
    }

    success(message: string) {
        this.$emit('upload-message', {
            message: message,
            type: 'success'
        })
    }

    error(message: string) {
        this.$emit('upload-message', {
            message: message,
            type: 'error',
        })
    }


    onNotification(params: any) {
        this.showDialog = false;
        this.getAssets(params.assetType)
        this.$emit('upload-message', params)
    }


}
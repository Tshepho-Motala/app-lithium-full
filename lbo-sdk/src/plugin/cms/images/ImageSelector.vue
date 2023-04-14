<template>
  <v-card flat>
    <v-autocomplete
        data-test-id="slt-banner"
        @input="onImageSelected()"
        filled
        chips
        color="blue-grey lighten-2"
        item
        ref="asset"
        v-model="asset"
        :items="assets"
        label="Banner"
        placeholder="Select Banner Image..."
        item-text="name"
        clearable
        return-object
    >
    </v-autocomplete>
  </v-card>
</template>

<script lang="ts">
import CmsProviderInterface from "@/core/interface/provider/CmsProviderInterface";
import { RootScopeInterface } from "@/core/interface/ScopeInterface";
import ListenerServiceInterface from "@/core/interface/service/ListenerServiceInterface";
import LogServiceInterface from "@/core/interface/service/LogServiceInterface";
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import { isEmpty } from "@/core/utils/objectUtils";
import { Prop, Inject, Component, Vue } from "vue-property-decorator";
import CmsPaginatedRequest from "../models/CmsPaginatedRequest";
import BannerImageProviderMockInterface from "@/core/interface/provider/BannerImageProviderMockInterface";
import DomainProviderInterface from "@/core/interface/provider/DomainProviderInterface";
import BannerImageInterface from "@/plugin/cms/interfaces/BannerImageInterface";

@Component
export default class ImageSelector extends Vue{

    @Prop({ required: true, type: String })
    domain!: string;
    @Prop({ required: true, type: String })
    type!: string;

    providerConfig = {};

    options: any = {};

    totalItems = 0;

    currentPage = 1;

    assets: BannerImageInterface[] = [];

    asset: any = {}

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

    created() {
        this.getConfig();
        this.getAssets(this.type);
    }

    get hasConfig(): boolean {
        return !isEmpty(this.providerConfig);
    }

    get cms(): BannerImageProviderMockInterface {
        return this.rootScope.provide.bannerImagesProvider;
    }

    get domainProvider(): DomainProviderInterface {
      return this.rootScope.provide.domainProvider;
    }

    canDelete(role: string) {
        return this.userService.hasRoleForDomain(this.domain, role) && this.hasConfig;
    }

    canAdd(role: string) {
        return this.userService.hasRoleForDomain(this.domain, role);
    }

    async getConfig() {
        try {
            // this.providerConfig = await this.domainProvider.getProviderConfig(this.domain);
        } catch (error) {
            this.logService.error(error);
        }
    }

    async getAssets(type: string) {
        this.loading = true;

        const { page, sortBy, descending, itemsPerPage } = this.options;

        let sortDirection = descending ? "desc" : "asc";

        try {
            const response: BannerImageInterface[] = await this.cms.findByDomainNameAndType(this.domain, type);

            this.assets = response;
        } catch (error) {
            this.logService.error(error);
        }

        this.loading = false;
    }

    onImageSelected() {
        this.$emit('onSelect', this.asset);
    }
}
</script>

<style>

</style>
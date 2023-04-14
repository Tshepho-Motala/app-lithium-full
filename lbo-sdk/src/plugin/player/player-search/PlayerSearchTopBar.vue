<template>
 <div>
   <AffiliateDropDown  @changeAffiliate="changeAffiliate"></AffiliateDropDown>
   <RestrictionDropDownMenu  @changeRestriction="changeRestriction" :restrictions="restrictions"></RestrictionDropDownMenu>
   <TagDropDownMenu @changeTag="changeTag" :tags="tags"></TagDropDownMenu>
   <DomainDropDownMenu v-if="domains.length" @changeDomain="changeDomain" :domains="domains"></DomainDropDownMenu>
 </div>
</template>

<script lang="ts">
import {Component, Inject, Vue} from "vue-property-decorator";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import DomainDropDownMenu from "@/plugin/components/drop-down/DomainDropDownMenu.vue";
import TagDropDownMenu from "@/plugin/components/drop-down/TagDropDownMenu.vue";
import RestrictionDropDownMenu from "@/plugin/components/drop-down/RestrictionDropDownMenu.vue";
import { DomainDropDown,TagDropDown, RestrictionDropDown} from   '@/core/interface/DropDownMenuInterface'
import AffiliateDropDown from "@/plugin/components/drop-down/AffiliateDropDown.vue";

@Component({
  components: {
    DomainDropDownMenu, TagDropDownMenu, RestrictionDropDownMenu, AffiliateDropDown
  }
})
export default class PlayerSearchTopBar extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  domains: DomainDropDown[] = []
  tags: TagDropDown[] = []
  restrictions: RestrictionDropDown[] = []

 mounted() {
   this.loadDomain()
 }


 async loadDomain()  {
   this.domains = await this.rootScope.provide.dropDownMenuProvider.domainList()
   await this.changeDomain(this.domains)
 }
 async loadTypes() {
   this.tags = await this.rootScope.provide.dropDownMenuProvider.tagList()
   this.restrictions  = await this.rootScope.provide.dropDownMenuProvider.restrictionsList()
 }
 async changeTag(data: TagDropDown[]){
   await this.rootScope.provide.dropDownMenuProvider.tagsChange(data)
 }
 async changeRestriction(data: RestrictionDropDown[]){
   await this.rootScope.provide.dropDownMenuProvider.restrictionsChange(data)
 }
 async changeDomain(data:DomainDropDown[]){
    await this.rootScope.provide.dropDownMenuProvider.domainsChange(data)
    await this.loadTypes()
 }
 async changeAffiliate(data:any){
   await this.rootScope.provide.dropDownMenuProvider.changeAffiliate(data)
 }
}
</script>

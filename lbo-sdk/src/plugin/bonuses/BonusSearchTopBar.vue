<template>
  <div data-test-id="cnt-bonus-search-top-bar">
    <StatusDropDownMenu  @changeStatus="changeStatus" :statuses="statuses"></StatusDropDownMenu>
    <TypeDropDownMenu @changeTag="changeTag" :tags="tags"></TypeDropDownMenu>
    <DomainDropDownMenu v-if="domains.length" @changeDomain="changeDomain" :domains="domains"></DomainDropDownMenu>
  </div>
</template>

<script lang="ts">
import {Component, Inject, Vue} from "vue-property-decorator";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import DomainDropDownMenu from "@/plugin/components/drop-down/DomainDropDownMenu.vue";
import TypeDropDownMenu from "@/plugin/components/drop-down/TypeDropDownMenu.vue";
import StatusDropDownMenu from "@/plugin/components/drop-down/StatusDropDownMenu.vue";
import { DomainDropDown,TagDropDown, StatusDropDown} from '@/core/interface/DropDownMenuInterface'
@Component({
  components: {
    DomainDropDownMenu, TypeDropDownMenu, StatusDropDownMenu
  }
})
export default class BonusSearchTopBar extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  domains: DomainDropDown[] = []
  tags: TagDropDown[] = []
  statuses: StatusDropDown[] = []

  mounted() {
    this.loadDomain()
    this.loadTypes()
  }


  async loadDomain()  {
    this.domains = await this.rootScope.provide.dropDownMenuProvider.domainList()
  }
  async loadTypes() {
    this.tags = await this.rootScope.provide.dropDownMenuProvider.tagList()
    this.statuses = await this.rootScope.provide.dropDownMenuProvider.statusList()
  }
  async changeTag(data: TagDropDown[]){
    await this.rootScope.provide.dropDownMenuProvider.tagsChange(data)
  }
  async changeStatus(data: StatusDropDown[]){
    await this.rootScope.provide.dropDownMenuProvider.statusesChange(data)
  }
  async changeDomain(data:DomainDropDown[]){
    await this.rootScope.provide.dropDownMenuProvider.domainsChange(data)

  }
}
</script>

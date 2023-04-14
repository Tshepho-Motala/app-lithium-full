<template>
  <div>
    <EcosystemDropDownMenu v-if="ecosystems.length"  @ecosystemChange="ecosystemChange" :ecosystems="ecosystems"></EcosystemDropDownMenu>
    <DomainDropDownMenu v-if="domains.length" @changeDomain="changeDomain" :domains="domains"></DomainDropDownMenu>
  </div>
</template>

<script lang="ts">
import {Component, Inject, Vue} from "vue-property-decorator";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import DomainDropDownMenu from "@/plugin/components/drop-down/DomainDropDownMenu.vue";
import EcosystemDropDownMenu from "@/plugin/components/drop-down/EcosystemDropDownMenu.vue";
import {DomainDropDown, EcosystemDropDown} from '@/core/interface/DropDownMenuInterface'
@Component({
  components: {
    DomainDropDownMenu, EcosystemDropDownMenu
  }
})
export default class PlayerSearchTopBar extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  domains: DomainDropDown[] = []
  ecosystems: EcosystemDropDown[] = []

  mounted() {
    this.loadTypes()
    this.loadDomain()
  }
  async loadDomain()  {
    this.domains = await this.rootScope.provide.dropDownMenuProvider.domainList()
  }
  async loadTypes() {
    this.ecosystems = await this.rootScope.provide.dropDownMenuProvider.ecosystemList()
  }

  async changeDomain(data:DomainDropDown[]){
    await this.rootScope.provide.dropDownMenuProvider.domainsChange(data)
  }
  async ecosystemChange(data:EcosystemDropDown[]){
    await this.rootScope.provide.dropDownMenuProvider.ecosystemChange(data)
  }
}
</script>

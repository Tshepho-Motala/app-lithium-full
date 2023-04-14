<template>
  <div class="d-flex">
    <DomainDropDownMenu v-if="domains.length" @changeDomain="changeDomain" :domains="domains"></DomainDropDownMenu>
  </div>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import DomainDropDownMenu from "@/plugin/components/drop-down/DomainDropDownMenu.vue";
import { DomainDropDown } from   '@/core/interface/DropDownMenuInterface'
@Component({
  components: {
    DomainDropDownMenu
  }
})
export default class DomainSelect extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  @Prop({ default: false, required: false }) hasDomains!: boolean
  @Prop({ required: false }) domainsList!: DomainDropDown[]

  domains: DomainDropDown[] = []


  mounted() {
    if(this.hasDomains) {
      this.domains = this.domainsList
    } else{
      this.loadDomain()
    }
  }
  async loadDomain()  {
    this.domains = await this.rootScope.provide.dropDownMenuProvider.domainList()
    await this.changeDomain(this.domains)
  }

  async changeDomain(data:DomainDropDown[]){
    if(this.hasDomains) {
      this.$emit('changeDomain', data)
    } else{
      await this.rootScope.provide.dropDownMenuProvider.domainsChange(data)
    }
  }
}
</script>

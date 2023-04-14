<template>
  <v-menu
      v-model="menuIsVisible"
      :close-on-content-click="false"
      nudge-width="300"
      max-width="300"
      max-height="500"
      hide-details
      nudge-top="-40"
      right
  >
    <template v-slot:activator="{ on, attrs }">
      <v-btn
          v-bind="attrs"
          v-on="on"
          color="primary"
          style="text-transform: none;"
          dark
      >
        {{ allDomainsAreSelected ? translate("DOMAIN.ALL")  : domainsAreSelectedText }}
        <v-icon
            dense
            right
          color="white"
      >
        mdi-chevron-down
      </v-icon>
      </v-btn>
    </template>
    <v-card class="px-4 py-4">
      <v-checkbox
          color="info"
          @change="setAllDomainSelected"
          label="All domains"
          :indeterminate="someDomainsAreSelected"
          v-model="allDomainsAreSelected"
          class="mb-0 mt-0"
          hide-details
      >
      </v-checkbox>
      <v-divider class="mb-0 mt-2" />
      <div >
        <v-checkbox
            v-for="item in domainsList"
            :key="`dom_${item.$$hashKey}`"
            v-model="item.selected"
            color="info"
            :label="item.name"
            hide-details
            @change="selectDomain"
            class="mt-2"
        >
        </v-checkbox>
      </div>
    </v-card>
  </v-menu>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import { DomainDropDown } from   '@/core/interface/DropDownMenuInterface'

@Component
export default class DomainDropDownMenu extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({ required: true }) domains!: DomainDropDown[]

  menuIsVisible: boolean = false
  domainsList:DomainDropDown[] = []

  mounted(){
    this.domainsList = JSON.parse(JSON.stringify(this.domains))
  }

  get someDomainsAreSelected(): boolean {
    return this.domainsList.some( (i: DomainDropDown) => i.selected === true) && this.domainsList.some( (i: DomainDropDown) => i.selected === false)
  }

  get allDomainsAreSelected(): boolean {
    return this.domainsList.every( (i:DomainDropDown) => i.selected === true)
  }
  get domainsAreSelectedText(): string {
    const list = this.domainsList.filter( (i:DomainDropDown) => i.selected === true)
    if(list.length === 0 ) {
      return  this.translate("DOMAIN.NO")
    }
    else if(list.length > 1 ) {
      return list.length + ' ' + this.translate("DOMAIN.MANY")
    }
    return  list.length + ' ' + this.translate("DOMAIN.ONE")
  }

  setAllDomainSelected(val: boolean) {
    if(this.domainsList.length) {
      this.domainsList = this.domainsList.map((i:DomainDropDown) => { return { ...i, selected : val}  })
    }
    const domain = this.domainsList.filter( (i:DomainDropDown) => i.selected === true)
    this.$emit('changeDomain', domain)
  }

  selectDomain() {
    const domain = this.domainsList.filter( (i:DomainDropDown) => i.selected === true)
    this.$emit('changeDomain', domain)
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.DROP_DOWN_MENU." + text);
  }
}
</script>
<style scoped>
</style>
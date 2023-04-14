<template>
  <div>
    <div class="card card-block mb-2">
      <div class="d-flex justify-content-start">
        <div class="p-2 mr-auto w-30">
          <span class="title">{{ translate(textTitle) }}</span>
          <span class="description">{{ translate(textDescr)}}</span>
        </div>
        <div class="p-2 text-center">
          <span class="title">{{ activeDomain ? activeDomain.displayName : '' }}</span>
          <span class="description">{{ activeDomain ? translate(textSubtitle) : translate(textSelectDomain) }}</span>
        </div>
        <div class="p-2 ml-auto text-right w-30">
          <v-autocomplete
              data-test-id="slt-domain"
              v-model="activeDomain"
              :items="domains"
              item-text="displayName"
              item-value="name"
              return-object
              @change="selectDomain"
              outlined
              dense
              clearable
              click:clear="clearDomain"
          ></v-autocomplete>
        </div>
      </div>
    </div>
  </div>
</template>


<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import {BankAccountDomainInterface, DomainSingleSelectInterface} from "@/core/interface/DomainSingleSelectInterface";

@Component
export default class PageHeader extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  @Prop() parentVue?: boolean
  @Prop() description?: string
  @Prop() title?: string
  @Prop() subtitle?: string

  isBankAccountLookupPage: boolean = false
  domains: DomainSingleSelectInterface[] | BankAccountDomainInterface[] = []
  textTitle: string = ''
  textDescr: string = ''
  textSubtitle: string = ''
  textSelectDomain: string = 'UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP.HEADER.SELECT_DOMAIN_DESC'
  activeDomain: DomainSingleSelectInterface | BankAccountDomainInterface | null = null

  created() {
    if (window.location.href.indexOf("bank-account-lookup") != -1) {
      this.isBankAccountLookupPage = true
    }
  }

  mounted() {
    this.loadDomains();

    if(this.parentVue) {
      if(this.title) this.textTitle = this.title
      if(this.description) this.textDescr = this.description
      if(this.subtitle) this.textSubtitle = this.subtitle
    } else {
      this.textTitle = this.rootScope.provide.pageHeaderProvider.textTitle()
      this.textDescr = this.rootScope.provide.pageHeaderProvider.textDescr()
      if (this.rootScope.provide.pageHeaderProvider.textSubtitle) {
        this.textSubtitle = this.rootScope.provide.pageHeaderProvider.textSubtitle()
      } else {
        this.textSubtitle = this.textDescr
      }
    }

    if (window.location.href.indexOf("bank-account-lookup") === -1) {
      let localDomain = window.localStorage.getItem('domain-name')
      if (localDomain) {
        this.selectDomain(JSON.parse(localDomain))
      }
    }
  }

  async loadDomains() {
    if(!this.parentVue) {
      if (this.isBankAccountLookupPage) {
        let tempDomains = await this.rootScope.provide.pageHeaderProvider.getDomains()

        let res: any = []

        for ( let i = 0; i< tempDomains.length; i++){
          if(tempDomains[i].current) {

            for ( let j = 0; j < tempDomains[i].current.labelValueList.length; j++) {

              if(tempDomains[i].current.labelValueList[j].labelValue.label.name == 'bank_account_lookup' && tempDomains[i].current.labelValueList[j].labelValue.value === "true") {
                res.push(tempDomains[i])
              }
            }
          }
        }
        this.domains = res;

        let localDomain = window.localStorage.getItem('domain-name')
        if (localDomain) {
          let parsedLocalDomain = JSON.parse(localDomain)

          if(parsedLocalDomain) {
            this.domains.forEach( item => {
              if(item.name === parsedLocalDomain.name) {
                this.selectDomain(item)
              }
            })
          }
        }

      } else {
        this.domains = await this.rootScope.provide.pageHeaderProvider.getDomainsList()
      }
     } else {
        this.domains = await this.rootScope.provide.pageHeaderProvider.getDomains()
      }
  }

  selectDomain(domain: DomainSingleSelectInterface | BankAccountDomainInterface | null) {
    let activeDomain: DomainSingleSelectInterface | null = null
    if (domain) {
      activeDomain = {
        name: domain.name,
        displayName: domain?.displayName,
        pd: domain.pd ? domain.pd : true
      }
    }

    if(!this.parentVue) {
      const localDomain = window.localStorage.getItem('domain-name')
      const urls: string[] = window.location.href.split('/')

      if(localDomain) {
        const parsedDomain = JSON.parse(localDomain)
        let domainInUrl: string = ''

        if (parsedDomain ) { // there is domain saved in localStorage

          urls.forEach(str => {
            let result = this.domains.some( el => {
              return el.name === str
            })

            if (result) domainInUrl = str
          })

          if (domainInUrl !== parsedDomain.name) { // it's not on the same domain

            if (window.location.href.indexOf(domainInUrl + "/view") !== -1) {
              this.rootScope.provide.pageHeaderProvider.domainSelect(domain, false)
            } else {
              this.rootScope.provide.pageHeaderProvider.domainSelect(domain, true)
            }

          } else {// it's on the same domain
            this.rootScope.provide.pageHeaderProvider.domainSelect(domain, true)
          }
        } else {//else if parsed === null
          this.rootScope.provide.pageHeaderProvider.domainSelect(domain, true)
        }

      } else { //else there is no saved domain in localStorage
        this.rootScope.provide.pageHeaderProvider.domainSelect(domain, true)
      }

    } else { // if its imported in Vue component
      this.$emit('onSelect', domain)// perhaps in future need to change domain to activeDomain
    }

    this.activeDomain = domain
    window.localStorage.setItem('domain-name', JSON.stringify(activeDomain))
  }


  clearDomain() {
    if(!this.parentVue) {
      this.rootScope.provide.pageHeaderProvider.clearSelectedDomain()
    }
  }

  translate(text: string): string {
    return this.translateService.instant(text);
  }

}
</script>

<style scoped>

</style>
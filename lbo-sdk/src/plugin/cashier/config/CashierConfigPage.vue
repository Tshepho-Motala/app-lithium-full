<template>
  <div data-test-id="cashier-config-page">
    <page-header  @onSelect="onDomainSelected" :parentVue="true" :description="pageDescription" :title="pageTitle" :subtitle="pageSubtitle" />
    <div v-if="activeDomain">
      <v-toolbar dense>
        <v-toolbar-title>{{$translate(pageSubtitle)}}</v-toolbar-title>
        <template v-slot:extension>
          <v-tabs
              v-model="mainTab"
              align-with-title
          >
            <v-tab
              v-for="( tab, index) in mainTabItems"
              :key="index"
            >
              {{tab.displayName}}
            </v-tab>
          </v-tabs>
        </template>
      </v-toolbar>
      <v-tabs-items v-model="mainTab">
        <v-tab-item
            v-for="tab in mainTabItems"
            :key="tab.id"
        >
          <v-card flat>
            <!--            METHODS    -->
            <v-toolbar v-if="tab.name === 'Methods'" class="pb-2" dense>

              <template v-slot:extension>
                <v-tabs
                    v-model="methodTab"
                    align-with-title
                >
                  <v-tab
                      v-for="subTab in methodsTabItems"
                      :key="subTab.id"
                  >
                    {{subTab.displayName}}
                  </v-tab>
                </v-tabs>
              </template>
            </v-toolbar>
            <!--            PROFILES           -->
            <v-toolbar v-if="tab.name === 'Profiles'" dense>
                    <p>Profiles</p>
            </v-toolbar>
            <v-tabs-items v-model="methodTab" v-if="tab.name === 'Methods'" dense>
            <!--            INNER ITEMS        -->
              <v-tab-item
                  v-for="subTab in methodsTabItems"
                  :key="subTab.id"
              >
                <v-card  class="p-2" v-show="hasRole('CASHIER_CONFIG,CASHIER_CONFIG_VIEW')">
                  <cashier-wrapper-method :domain="activeDomain.name" :currency="currency" :type="subTab.name" :tab="methodTab" />
                </v-card>
              </v-tab-item>
            </v-tabs-items>
          </v-card>
        </v-tab-item>
      </v-tabs-items>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Inject, Mixins} from "vue-property-decorator";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import PageHeader from "@/plugin/components/PageHeader.vue";
import CashierWrapperMethod from "@/plugin/cashier/config/CashierWrapperMethod.vue";
import LogServiceInterface from "@/core/interface/service/LogServiceInterface";
import {DomainSingleSelectInterface} from "@/core/interface/DomainSingleSelectInterface";
import {MainTab} from "@/core/interface/cashierConfig/CashierConfigInterface";
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import TranslationMixin from "@/core/mixins/translationMixin";

@Component({
  components: {
    PageHeader,
    CashierWrapperMethod
  }
})
export default class CashierConfigPage extends Mixins(TranslationMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('userService') readonly userService!: UserServiceInterface

  domains: DomainSingleSelectInterface[] = []
  activeDomain: DomainSingleSelectInterface | null = null
  currency: string = ''
  mainTab: boolean = true
  methodTab: boolean = true
  mainTabItems: MainTab[] = [
    {
      name: 'Methods',
      displayName: this.$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.METHODS'),
      id: 1
    },
    {
      name: 'Profiles',
      displayName: this.$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.PROFILES'),
      id: 2
    }
  ]
  methodsTabItems: MainTab[] = [
    {
      displayName: this.$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.DEPOSIT_METHODS'),
      name: 'deposit',
      id: 1
    },
    {
      displayName: this.$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.WITHDRAWAL_METHODS'),
      name: 'withdraw',
      id: 2
    }
  ]


  async mounted() {
    this.loadDomains()
  }

  async loadDomains() {
    try {
      let result = await this.rootScope.provide.pageHeaderProvider.getDomains()

      if (result) {
        this.domains = result
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  async getCurrency(item: DomainSingleSelectInterface) {
    try {
      let result =  await this.rootScope.provide.cashierConfigProvider.getCurrencyMethod(item.name)

      if (result) {
        this.currency = result.currencySymbol
      }
    } catch (err) {
      this.logService.error(err)
    }
  }

  onDomainSelected(item: DomainSingleSelectInterface) {
    this.activeDomain = item
    if(item) {
      this.getCurrency(item)
    }
  }

  hasRole(role: string) {
    let arr: string[] = role.split(',')

    return arr.some( r => this.userService.hasRole(r) )
  }

  get pageTitle() {
    return "UI_NETWORK_ADMIN.PAGE_HEADER.CASHIER_CONFIG.TITLE"
  }

  get pageDescription() {
    return "UI_NETWORK_ADMIN.PAGE_HEADER.CASHIER_CONFIG.DESCRIPTION"
  }

  get pageSubtitle() {
    return "UI_NETWORK_ADMIN.PAGE_HEADER.CASHIER_CONFIG.SUBTITLE"
  }

}
</script>

<style scoped>

</style>
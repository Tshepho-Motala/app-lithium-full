<template>
  <div>
    <GameSupplierDropDownMenu @changeGameSuppliers="changeGameSuppliers" :gameSuppliers="gameSuppliers"></GameSupplierDropDownMenu>
    <GameProviderDropDownMenu @changeGameProviders="changeGameProviders" :gameProviders="gameProviders"></GameProviderDropDownMenu>
<!--    consider using GameConfigsDropDownMenu in future for game configs-->
<!--    <GameConfigsDropDownMenu @changeGameConfigs="changeGameConfigs" :gameConfigs="gameConfigs"></GameConfigsDropDownMenu>-->
  </div>
</template>

<script lang="ts">
import {Component, Inject, Vue} from "vue-property-decorator";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import {GameConfigsDropDown, GameProviderDropDown, GameSupplierDropDown} from "@/core/interface/DropDownMenuInterface";
import GameSupplierDropDownMenu from "@/plugin/components/drop-down/GameSupplierDropDownMenu.vue";
import GameProviderDropDownMenu from "@/plugin/components/drop-down/GameProviderDropDownMenu.vue";
import GameConfigsDropDownMenu from "@/plugin/components/drop-down/GameConfigsDropDownMenu.vue";
@Component( {
  components: {
    GameConfigsDropDownMenu,
    GameProviderDropDownMenu,
    GameSupplierDropDownMenu
  }
})
export default class GameSearchTopBar extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  gameSuppliers: GameSupplierDropDown[] = []
  gameProviders: GameProviderDropDown[] = []
  gameConfigs: GameConfigsDropDown[] = []

  mounted() {
    this.loadGameConfigs();
    this.loadGameProviders();
    this.loadGameSuppliers();
  }


  async loadGameSuppliers() {
    this.gameSuppliers  = await this.rootScope.provide.dropDownMenuProvider.gameSuppliersList()
  }

  async changeGameSuppliers(data: GameSupplierDropDown[]){
    await this.rootScope.provide.dropDownMenuProvider.gameSuppliersChange(data)
    await this.loadGameSuppliers()
  }

  async loadGameProviders() {
    this.gameProviders  = await this.rootScope.provide.dropDownMenuProvider.gameProvidersList()
  }

  async changeGameProviders(data: GameProviderDropDown[]){
    await this.rootScope.provide.dropDownMenuProvider.gameProvidersChange(data)
    await this.loadGameProviders()
  }

  async loadGameConfigs() {
    this.gameConfigs  = await this.rootScope.provide.dropDownMenuProvider.gameConfigsList()
  }

  async changeGameConfigs(data: GameConfigsDropDown[]){
    await this.rootScope.provide.dropDownMenuProvider.gameConfigsChange(data)
    await this.loadGameConfigs()
  }

}
</script>

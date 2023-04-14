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
        {{ allGameProvidersAreSelected && gameProviders.length ? translate("GAME_PROVIDERS.ALL")  : gameProvidersAreSelectedText }}
        <v-icon
            dense
            right
            color="white"
        >
          mdi-chevron-down
        </v-icon>
      </v-btn>
    </template>
    <v-card class="px-4 drop-down-menu__wrap py-4">
      <template v-if="gameProviders.length">
        <v-checkbox
            color="info"
            @change="setAllGameProvidersSelected"
            label="All game providers"
            :indeterminate="someGameProvidersAreSelected"
            v-model="allGameProvidersAreSelected"
            class="mb-0 mt-0"
            hide-details
        >
        </v-checkbox>
        <v-divider class="mb-0 mt-2" />
        <div >
          <v-checkbox
              v-for="item in gameProviders"
              :key="item.id"
              v-model="item.selected"
              color="info"
              :label="item.name"
              hide-details
              @change="selectGameProviders"
              class="mt-2"
          >
          </v-checkbox>
        </div>
      </template>


      <template v-else>
        <v-alert
            shaped
            outlined
            type="warning"
            class="mb-0"
        >
          {{translate("GAME_PROVIDERS.EMPTY")}}
        </v-alert>
      </template>
    </v-card>
  </v-menu>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import {GameProviderDropDown} from '@/core/interface/DropDownMenuInterface'

@Component
export default class GameProviderDropDownMenu extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({ required: true }) gameProviders!: GameProviderDropDown[]

  menuIsVisible: boolean = false
  gameProviderList:GameProviderDropDown[] = []

  mounted(){
    this.gameProviderList = this.gameProviders
  }

  get someGameProvidersAreSelected(): boolean {
    return this.gameProviders.some( (i: GameProviderDropDown) => i.selected === true) && this.gameProviders.some( (i: GameProviderDropDown) => i.selected === false)
  }

  get allGameProvidersAreSelected(): boolean {
    return this.gameProviders.every( (i:GameProviderDropDown) => i.selected === true)
  }
  get gameProvidersAreSelectedText(): string {
    const list = this.gameProviders.filter( (i:GameProviderDropDown) => i.selected === true)
    if(list.length === 0 || !this.gameProviders.length) {
      return  this.translate("GAME_PROVIDERS.NO")
    }
    else if(list.length > 1 ) {
      return list.length + ' ' + this.translate("GAME_PROVIDERS.MANY")
    }
    return list.length + ' ' + this.translate("GAME_PROVIDERS.ONE")
  }

  setAllGameProvidersSelected(val: boolean) {
    if(this.gameProviders.length) {
      this.gameProviders = this.gameProviders.map((i: GameProviderDropDown) => { return { ...i, selected : val}  })
    }
    const gameProviders = this.gameProviders.filter( (i: GameProviderDropDown) => i.selected === true)
    this.$emit('changeGameProviders', gameProviders)
  }

  selectGameProviders() {
    const gameProvidersList = [...this.gameProviders.filter( (i: GameProviderDropDown) => i.selected === true)]
    this.$emit('changeGameProviders', gameProvidersList)
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.DROP_DOWN_MENU." + text);
  }
}
</script>
<style scoped>
</style>
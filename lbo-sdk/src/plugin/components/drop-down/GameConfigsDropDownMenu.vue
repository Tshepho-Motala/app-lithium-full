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
        {{ allGameConfigsAreSelected && gameConfigs.length ? translate("GAME_CONFIGS.ALL")  : gameConfigsAreSelectedText }}
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
      <template v-if="gameConfigs.length">
        <v-checkbox
            color="info"
            @change="setAllGameConfigsSelected"
            label="All game configurations"
            :indeterminate="someGameConfigsAreSelected"
            v-model="allGameConfigsAreSelected"
            class="mb-0 mt-0"
            hide-details
        >
        </v-checkbox>
        <v-divider class="mb-0 mt-2" />
        <div >
          <v-checkbox
              v-for="item in gameConfigs"
              :key="item.id"
              v-model="item.selected"
              color="info"
              :label="item.name"
              hide-details
              @change="selectGameConfigs"
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
          {{translate("GAME_CONFIGS.EMPTY")}}
        </v-alert>
      </template>
    </v-card>
  </v-menu>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import {GameConfigsDropDown} from '@/core/interface/DropDownMenuInterface'

@Component
export default class GameConfigsDropDownMenu extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({ required: true }) gameConfigs!: GameConfigsDropDown[]

  menuIsVisible: boolean = false
  gameConfigsList:GameConfigsDropDown[] = []

  mounted(){
    this.gameConfigsList = this.gameConfigs
  }

  get someGameConfigsAreSelected(): boolean {
    return this.gameConfigs.some( (i: GameConfigsDropDown) => i.selected === true) && this.gameConfigs.some( (i: GameConfigsDropDown) => i.selected === false)
  }

  get allGameConfigsAreSelected(): boolean {
    return this.gameConfigs.every( (i:GameConfigsDropDown) => i.selected === true)
  }
  get gameConfigsAreSelectedText(): string {
    const list = this.gameConfigs.filter( (i:GameConfigsDropDown) => i.selected === true)
    if(list.length === 0 || !this.gameConfigs.length) {
      return  this.translate("GAME_CONFIGS.NO")
    }
    else if(list.length > 1 ) {
      return list.length + ' ' + this.translate("GAME_CONFIGS.MANY")
    }
    return list.length + ' ' + this.translate("GAME_CONFIGS.ONE")
  }

  setAllGameConfigsSelected(val: boolean) {
    if(this.gameConfigs.length) {
      this.gameConfigs = this.gameConfigs.map((i: GameConfigsDropDown) => { return { ...i, selected : val}  })
    }
    const gameConfigs = this.gameConfigs.filter( (i: GameConfigsDropDown) => i.selected === true)
    this.$emit('changeGameConfigs', gameConfigs)
  }

  selectGameConfigs() {
    const gameConfigsList = [...this.gameConfigs.filter( (i: GameConfigsDropDown) => i.selected === true)]
    this.$emit('changeGameConfigs', gameConfigsList)
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.DROP_DOWN_MENU." + text);
  }
}
</script>
<style scoped>
</style>
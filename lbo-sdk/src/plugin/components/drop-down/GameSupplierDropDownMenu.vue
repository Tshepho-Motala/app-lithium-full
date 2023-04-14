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
        {{ allGameSuppliersAreSelected && gameSuppliers.length ? translate("GAME_SUPPLIERS.ALL")  : gameSuppliersAreSelectedText }}
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
      <template v-if="gameSuppliers.length">
        <v-checkbox
            color="info"
            @change="setAllGameSuppliersSelected"
            label="All game suppliers"
            :indeterminate="someGameSuppliersAreSelected"
            v-model="allGameSuppliersAreSelected"
            class="mb-0 mt-0"
            hide-details
        >
        </v-checkbox>
        <v-divider class="mb-0 mt-2" />
        <div >
          <v-checkbox
              v-for="item in gameSuppliers"
              :key="item.id"
              v-model="item.selected"
              color="info"
              :label="item.name"
              hide-details
              @change="selectGameSuppliers"
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
          {{translate("GAME_SUPPLIERS.EMPTY")}}
        </v-alert>
      </template>
    </v-card>
  </v-menu>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import {GameSupplierDropDown} from '@/core/interface/DropDownMenuInterface'

@Component
export default class GameSupplierDropDownMenu extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({ required: true }) gameSuppliers!: GameSupplierDropDown[]

  menuIsVisible: boolean = false
  gameSuppliersList:GameSupplierDropDown[] = []

  mounted(){
    this.gameSuppliersList = this.gameSuppliers
  }

  get someGameSuppliersAreSelected(): boolean {
    return this.gameSuppliers.some( (i: GameSupplierDropDown) => i.selected === true) && this.gameSuppliers.some( (i: GameSupplierDropDown) => i.selected === false)
  }

  get allGameSuppliersAreSelected(): boolean {
    return this.gameSuppliers.every( (i:GameSupplierDropDown) => i.selected === true)
  }
  get gameSuppliersAreSelectedText(): string {
    const list = this.gameSuppliers.filter( (i:GameSupplierDropDown) => i.selected === true)
    if(list.length === 0 || !this.gameSuppliers.length) {
      return  this.translate("GAME_SUPPLIERS.NO")
    }
    else if(list.length > 1 ) {
      return list.length + ' ' + this.translate("GAME_SUPPLIERS.MANY")
    }
    return list.length + ' ' + this.translate("GAME_SUPPLIERS.ONE")
  }

  setAllGameSuppliersSelected(val: boolean) {
    if(this.gameSuppliers.length) {
      this.gameSuppliers = this.gameSuppliers.map((i: GameSupplierDropDown) => { return { ...i, selected : val}  })
    }
    const gameSuppliers = this.gameSuppliers.filter( (i: GameSupplierDropDown) => i.selected === true)
    this.$emit('changeGameSuppliers', gameSuppliers)
  }

  selectGameSuppliers() {
    const gameSuppliersList = [...this.gameSuppliers.filter( (i: GameSupplierDropDown) => i.selected === true)]
    this.$emit('changeGameSuppliers', gameSuppliersList)
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.DROP_DOWN_MENU." + text);
  }
}
</script>
<style scoped>
</style>
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
        {{ allEcosystemsAreSelected ? translate("ECOSYSTEM.ALL")  : ecosystemsAreSelectedText }}
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
      <template v-if="ecosystems.length">
        <v-checkbox
            color="info"
            @change="setAllEcosystemSelected"
            label="All ecosystems"
            :indeterminate="someEcosystemsAreSelected"
            v-model="allEcosystemsAreSelected"
            class="mb-0 mt-0"
            hide-details
        >
        </v-checkbox>
        <v-divider class="mb-0 mt-2" />
        <div >
          <v-checkbox
              v-for="item in ecosystemsList"
              :key="`dom_${item.$$hashKey}`"
              v-model="item.selected"
              color="info"
              :label="item.name"
              hide-details
              @change="selectEcosystem"
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
          {{translate("RESTRICTION.EMPTY")}}
        </v-alert>
      </template>
    </v-card>
  </v-menu>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import { EcosystemDropDown } from   '@/core/interface/DropDownMenuInterface'

@Component
export default class EcosystemDropDownMenu extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({ required: true }) ecosystems!: EcosystemDropDown[]

  menuIsVisible: boolean = false
  ecosystemsList:EcosystemDropDown[] = []

  mounted(){
    this.ecosystemsList = this.ecosystems
  }

  get someEcosystemsAreSelected(): boolean {
    return this.ecosystemsList.some( (i: EcosystemDropDown) => i.selected === true) && this.ecosystemsList.some( (i: EcosystemDropDown) => i.selected === false)
  }

  get allEcosystemsAreSelected(): boolean {
    return this.ecosystemsList.every( (i:EcosystemDropDown) => i.selected === true)
  }
  get ecosystemsAreSelectedText(): string {
    const list = this.ecosystemsList.filter( (i:EcosystemDropDown) => i.selected === true)
    if(list.length === 0 ) {
      return  this.translate("ECOSYSTEM.NO")
    }
    else if(list.length > 1 ) {
      return list.length + ' ' + this.translate("ECOSYSTEM.MANY")
    }
    return  list.length + ' ' + this.translate("ECOSYSTEM.ONE")
  }

  setAllEcosystemSelected(val: boolean) {
    if(this.ecosystemsList.length) {
      this.ecosystemsList = this.ecosystemsList.map((i:EcosystemDropDown) => { return { ...i, selected : val}  })
    }
    const ecosystem = this.ecosystemsList.filter( (i:EcosystemDropDown) => i.selected === true)
    this.$emit('ecosystemChange', ecosystem)
  }

  selectEcosystem() {
    const ecosystem = this.ecosystemsList.filter( (i:EcosystemDropDown) => i.selected === true)
    this.$emit('ecosystemChange', ecosystem)
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.DROP_DOWN_MENU." + text);
  }
}
</script>
<style scoped>
</style>
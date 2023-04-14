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
        {{ allRestrictionsAreSelected && restrictions.length ? translate("RESTRICTION.ALL")  : restrictionsAreSelectedText }}
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
      <template v-if="restrictions.length">
        <v-checkbox
            color="info"
            @change="setAllRestrictionSelected"
            label="All restrictions"
            :indeterminate="someRestrictionsAreSelected"
            v-model="allRestrictionsAreSelected"
            class="mb-0 mt-0"
            hide-details
        >
        </v-checkbox>
        <v-divider class="mb-0 mt-2" />
        <div >
          <v-checkbox
              v-for="item in restrictions"
              :key="item.id"
              v-model="item.selected"
              color="info"
              :label="item.name"
              hide-details
              @change="selectRestriction"
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
import {  RestrictionDropDown} from   '@/core/interface/DropDownMenuInterface'

@Component
export default class RestrictionDropDownMenu extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({ required: true }) restrictions!: RestrictionDropDown[]

  menuIsVisible: boolean = false
  restrictionsList:RestrictionDropDown[] = []

  mounted(){
    this.restrictionsList = this.restrictions
  }

  get someRestrictionsAreSelected(): boolean {
    return this.restrictions.some( (i: RestrictionDropDown) => i.selected === true) && this.restrictions.some( (i: RestrictionDropDown) => i.selected === false)
  }

  get allRestrictionsAreSelected(): boolean {
    return this.restrictions.every( (i:RestrictionDropDown) => i.selected === true)
  }
  get restrictionsAreSelectedText(): string {
    const list = this.restrictions.filter( (i:RestrictionDropDown) => i.selected === true)
    if(list.length === 0 || !this.restrictions.length) {
      return  this.translate("RESTRICTION.NO")
    }
    else if(list.length > 1 ) {
      return list.length + ' ' + this.translate("RESTRICTION.MANY")
    }
    return list.length + ' ' + this.translate("RESTRICTION.ONE")
  }

  setAllRestrictionSelected(val: boolean) {
    if(this.restrictions.length) {
      this.restrictions = this.restrictions.map((i: RestrictionDropDown) => { return { ...i, selected : val}  })
    }
    const restriction = this.restrictions.filter( (i: RestrictionDropDown) => i.selected === true)
    this.$emit('changeRestriction', restriction)
  }

  selectRestriction() {
    const restrictionsList = [...this.restrictions.filter( (i: RestrictionDropDown) => i.selected === true)]
    this.$emit('changeRestriction', restrictionsList)
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.DROP_DOWN_MENU." + text);
  }
}
</script>
<style scoped>
</style>
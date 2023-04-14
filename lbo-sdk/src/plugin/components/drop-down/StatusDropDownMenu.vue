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
        {{ allStatusesAreSelected && statuses.length ? translate("STATUS.ALL")  : statusesAreSelectedText }}
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
      <template v-if="statuses.length">
        <v-checkbox
            color="info"
            @change="setAllStatusSelected"
            label="All statuses"
            :indeterminate="someStatusesAreSelected"
            v-model="allStatusesAreSelected"
            class="mb-0 mt-0"
            hide-details
        >
        </v-checkbox>
        <v-divider class="mb-0 mt-2" />
        <div >
          <v-checkbox
              v-for="item in statuses"
              :key="item.id"
              v-model="item.selected"
              color="info"
              :label="item.name"
              hide-details
              @change="selectStatus"
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
          {{translate("STATUS.EMPTY")}}
        </v-alert>
      </template>


    </v-card>
  </v-menu>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import { StatusDropDown} from   '@/core/interface/DropDownMenuInterface'

@Component
export default class StatusDropDownMenu extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({ required: true }) statuses!: StatusDropDown[]

  menuIsVisible: boolean = false
  statusesList:StatusDropDown[] = []

  mounted(){
    this.statusesList =  this.statuses
  }

  get someStatusesAreSelected(): boolean {
    return this.statuses.some( (i: StatusDropDown) => i.selected === true) && this.statuses.some( (i: any) => i.selected === false)
  }

  get allStatusesAreSelected(): boolean {
    return this.statuses.every( (i:StatusDropDown) => i.selected === true)
  }
  get statusesAreSelectedText(): string {
    const list = this.statuses.filter( (i:StatusDropDown) => i.selected === true)
    if(list.length === 0 || !this.statuses.length ) {
      return  this.translate("STATUS.NO")
    }
    else if(list.length > 1 ) {
      return list.length + ' ' + this.translate("STATUS.MANY")
    }
    return list.length + ' ' + this.translate("STATUS.ONE")
  }

  setAllStatusSelected(val: boolean) {
    if(this.statuses.length) {
      this.statuses = this.statuses.map((i: StatusDropDown) => { return { ...i, selected : val}  })
    }
    const status = this.statuses.filter( (i: StatusDropDown) => i.selected === true)
    this.$emit('changeStatus', status)
  }

  selectStatus() {
    const statusesList = [...this.statuses.filter( (i: StatusDropDown) => i.selected === true)]
    this.$emit('changeStatus', statusesList)
  }
  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.DROP_DOWN_MENU." + text);
  }
}
</script>
<style scoped>
</style>
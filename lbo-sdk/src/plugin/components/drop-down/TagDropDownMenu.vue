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
        {{ allTagsAreSelected && tags.length ? translate("TAG.ALL")  : tagsAreSelectedText }}
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
      <template v-if="tags.length">
        <v-checkbox
            color="info"
            @change="setAllTagSelected"
            label="All tags"
            :indeterminate="someTagsAreSelected"
            v-model="allTagsAreSelected"
            class="mb-0 mt-0"
            hide-details
        >
        </v-checkbox>
        <v-divider class="mb-0 mt-2" />
        <div >
          <v-checkbox
              v-for="item in tags"
              :key="item.id"
              v-model="item.selected"
              color="info"
              :label="item.name"
              hide-details
              @change="selectTag"
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
          {{translate("TAG.EMPTY")}}
        </v-alert>
      </template>


    </v-card>
  </v-menu>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import { TagDropDown} from   '@/core/interface/DropDownMenuInterface'

@Component
export default class TagDropDownMenu extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({ required: true }) tags!: TagDropDown[]

  menuIsVisible: boolean = false
  tagsList:TagDropDown[] = []

  mounted(){
    this.tagsList =  this.tags
  }

  get someTagsAreSelected(): boolean {
    return this.tags.some( (i: TagDropDown) => i.selected === true) && this.tags.some( (i: any) => i.selected === false)
  }

  get allTagsAreSelected(): boolean {
    return this.tags.every( (i:TagDropDown) => i.selected === true)
  }
  get tagsAreSelectedText(): string {
    const list = this.tags.filter( (i:TagDropDown) => i.selected === true)
    if(list.length === 0 || !this.tags.length ) {
      return  this.translate("TAG.NO")
    }
    else if(list.length > 1 ) {
      return list.length + ' ' + this.translate("TAG.MANY")
    }
    return list.length + ' ' + this.translate("TAG.ONE")
  }

  setAllTagSelected(val: boolean) {
    if(this.tags.length) {
      this.tags = this.tags.map((i: TagDropDown) => { return { ...i, selected : val}  })
    }
    const tag = this.tags.filter( (i: TagDropDown) => i.selected === true)
    this.$emit('changeTag', tag)
  }

  selectTag() {
    const tagsList = [...this.tags.filter( (i: TagDropDown) => i.selected === true)]
    this.$emit('changeTag', tagsList)
  }
  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.DROP_DOWN_MENU." + text);
  }
}
</script>
<style scoped>
</style>
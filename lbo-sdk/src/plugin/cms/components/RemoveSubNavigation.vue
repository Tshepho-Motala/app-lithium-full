<template>
  <v-container data-test-id="cnt-remove-suv-navigation">
    <v-card tile>
      <v-row dense>
        <v-col cols="12" md="3" >
          <v-list-item-content><h4 class="ml-3">{{translate("UI_NETWORK_ADMIN.CMS.REMOVE_SUB_NAVIGATION.OUTPUT_FIELDS.REMOVE_SUB_NAVIGATION")}}</h4></v-list-item-content>
        </v-col>
        <v-col cols="12" md="7">
          <v-select data-test-id="slt-sub-nav" ref="subNavDropdown" :disabled="modifyDisabled" :items="items" @change="onChange" item-text="title" item-value="code" return-object></v-select>
        </v-col>
        <v-col cols="12" md="2">
          <v-btn data-test-id="btn-remove" color="primary" @click="removeSubNavigation" :disabled="modifyDisabled">
            <v-icon dark>
              mdi-minus
            </v-icon>
          </v-btn>
        </v-col>
      </v-row>
    </v-card>
  </v-container>
</template>

<script lang="ts">
import {Component, Inject, Prop, Vue} from "vue-property-decorator";
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import SubNavItem from "../models/SubNavItem";

@Component
export default class RemoveSubNavigation extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface;
  @Prop() modifyDisabled!: boolean;
  @Prop() items!: SubNavItem[];

  item: SubNavItem = new SubNavItem();

  onChange(item: SubNavItem) {
    this.item = item;
  }

  removeSubNavigation() {
    this.$emit('remove-sub-navigation', this.item.code);
  }

  translate(text: string) {
    return this.translateService.instant(text);
  }

}
</script>

<style>
</style>
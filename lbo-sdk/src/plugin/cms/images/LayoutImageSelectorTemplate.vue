<template>
  <v-card>
    <v-card-text>
        <v-row>
          <v-col  cols="12" class="pa-1 pt-4">
      <v-card flat @click="onNewImageClicked" class="gridItemAdd d-flex align-center justify-center">
        <v-icon>mdi-plus</v-icon>
      </v-card>

      <v-menu
        v-model="showImageSelectorMenu"
        :position-x="menuX"
        :position-y="menuY"
        absolute
        offset-y
        :close-on-content-click="false"
        max-height="200px"
    >
      <image-selector :type="type" @onSelect="onImageSelected" :domain="selectedDomain" />
    </v-menu>
    </v-col>
        </v-row>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import TranslationMixin from "@/core/mixins/translationMixin";
import { Component, Mixins, Prop } from "vue-property-decorator";
import ImageSelector from "./ImageSelector.vue";

@Component(
  {
    components: {
      ImageSelector
    }
  }
)
export default class LayoutImageSelectorTemplate extends Mixins (TranslationMixin) {

  @Prop({required: true, type: String}) selectedDomain!: string;
  @Prop({required: true, type: String}) type!: string;

  menuX: number | null = null;
  menuY: number | null = null;

  showImageSelectorMenu = false;

  onNewImageClicked (e: MouseEvent) {
    this.menuX = e.clientX;
    this.menuY = e.clientY;
    this.showImageSelectorMenu = true;
  }

  onImageSelected(image) {
    this.$emit("onImageSelect", image);
  }

}
</script>

<style>
  .gridItemAdd {
    border: 1px dashed grey;
    border-radius: 5px;
    min-height: 150px;
    width: 100%;
  }
</style>
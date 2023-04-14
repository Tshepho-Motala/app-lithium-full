<template>
  <div data-test-id="cnt-layout-builder-widget" class="widgetItemAdd d-flex align-center justify-center">
    <v-icon>mdi-plus</v-icon>
  </div>
</template>

<script lang='ts'>
import { Component, Vue } from 'vue-property-decorator'

@Component
export default class LayoutBuilderWidgetTemplate extends Vue {}
</script>

<style scoped>
.widgetItemAdd {
  border: 1px dashed grey;
  border-radius: 5px;
  min-height: 150px;
  width: 100%;
}
</style>
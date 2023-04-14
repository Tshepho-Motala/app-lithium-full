<template>
  <div style="height: 100%">
    <v-list class="pa-0 ma-0" style="height: 100%">
      <draggable :list="items" group="tags" style="min-height: 40px; height: 100%" @change="$emit('change')" @start="$emit('start')" @end="$emit('end')">
        <template v-if="hasItems">
          <v-list-item dense draggable v-for="(e, i) in items" :key="`b_${i}`" style="cursor: move">
            <v-list-item-title>
              {{ e.name }}
            </v-list-item-title>
          </v-list-item>
        </template>

        <template v-else>
          <v-list-item style="height: 100%">
            <span class="text-caption grey--text"> No items to display </span>
          </v-list-item>
        </template>
      </draggable>
    </v-list>
  </div>
</template>

<script lang='ts'>
import { Vue, Component, Prop } from 'vue-property-decorator'
import draggable from 'vuedraggable'

@Component({
  components: {
    draggable
  }
})
export default class TagDraggable extends Vue {
  @Prop({ required: true }) readonly items!: any[]

  get hasItems(): boolean {
    return this.items.length > 0
  }
}
</script>

<style scoped>
</style>
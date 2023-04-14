<template>
  <div>
    <v-btn @click="allowDrag = true">Enable draggable</v-btn>
    <draggable tag="v-row" v-model="items" class="w-100" :options="{disabled : !allowDrag}" @change="OrderWasChanged = true">
<!--      tag     - put tag name which you want the parent was -->
<!--      v-model     - could be data value or computed value( like filtered) -->
<!--      :options     - here if you want to disable draggable -->
<!--      @change     - used here to know that there were made some changes -->
      <div v-for="(item, index) in items" :key="`${index}_${item.id}`">
        <!--      :key     -  DON'T USE just index here because it won't work -->
        {{item.name}}
      </div>
    </draggable>
  </div>

</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import draggable from "vuedraggable";

@Component({
  components: {
    draggable
  }
})

export default class DraggableDemo extends Vue {
  items: any = [
    {
      id: 1,
      name: 'test 1'
    },
    {
      id: 1,
      name: 'test 1'
    },
    {
      id: 2,
      name: 'test 2'
    },{
      id: 3,
      name: 'test 3'
    },
    {
      id: 4,
      name: 'test 4'
    }
  ]
  orderWasChanged: boolean = false
  allowDrag: boolean = false


}
</script>

<style scoped>

</style>
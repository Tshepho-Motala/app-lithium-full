<template>
  <v-container data-cy="c-hello-world">
    <v-row class="text-center">
      <v-col cols="12">
      </v-col>

      <v-col class="mb-4">
        <h1 class="display-2 font-weight-bold mb-3">
          {{ msg }}
        </h1>

      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
  import Vue from 'vue';
  export default Vue.extend({
    name: 'HelloWorld',
    props: {
        msg: String,
    }
  });
</script>
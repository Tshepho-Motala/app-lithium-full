<template>
  <div class="mt-6 mb-7">
    <h2>{{ translate('UI_NETWORK_ADMIN.CHANGELOG.TITLE') }}</h2>
    <v-divider class="mt-2 mb-4"></v-divider>
    <change-log-item v-for="log in logList" :key="log.id" :log="log"></change-log-item>

    <div v-if="hasItem" style="border-left: 4px solid #ddd" class="d-flex align-start changelog-item ">
      <div class="d-flex align-center justify-center pa-2 mx-3 mt-5 grey lighten-2" style="border-radius: 50%;">
        <v-icon>
          mdi-cached
        </v-icon>
      </div>

      <v-btn
          class="mt-5"
          text
          color="primary"
          @click="loadMore"
      >
        Loaded ...
      </v-btn>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Inject, Mixins, Prop} from "vue-property-decorator";
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface';
import AssetTabMixin from "@/plugin/cms/mixins/AssetTabMixin";
import ChangeLogItem from "@/plugin/components/change-log/ChangeLogItem.vue";
import {ChangeLogItemInterface} from "@/core/interface/components-interfaces/ChangeLogInterface";


@Component({
  components: {
    ChangeLogItem
  }
})
export default class ChangeLog extends Mixins(AssetTabMixin) {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  @Prop({required: true}) logList?: ChangeLogItemInterface[]
  @Prop({required: true, default: false}) hasItem?: Boolean

  loadMore() {
    this.$emit('loadMore')
  }

  translate(transStr: string) {
    return this.translateService.instant(transStr)
  }
}
</script>
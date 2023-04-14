<template>
  <v-row>
    <v-col cols="12">
      <span class="text-h4">DEMO - Translations</span>
    </v-col>

    <v-col cols="12">
      <v-divider></v-divider>
    </v-col>

    <v-col cols="12">
      <div class="text-h5">Translation through Component</div>

      <Translator translationKey="UI_NETWORK_ADMIN.MAIL.TEMPLATES.ADD.TITLE" />
    </v-col>

    <v-col cols="12">
      <v-divider></v-divider>
    </v-col>

    <v-col cols="12">
      <div class="text-h5">Translation through Directive</div>

      <div>
        This is translated text: <span v-translate="'UI_NETWORK_ADMIN.MAIL.TEMPLATES.ADD.TITLE'"></span>
      </div>
    </v-col>

    <v-col cols="12">
      <v-divider></v-divider>
    </v-col>

    <v-col cols="12">
      <div class="text-h5">Interactive Translation</div>

      <v-text-field label="Translation Key" v-model="translateKey"></v-text-field>

      Dynamically translated text: <span v-translate="translateKey"></span>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import { Component, Vue, Inject } from 'vue-property-decorator'
import Translator from '@/plugin/components/Translator.vue'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'

// !!IMPORTANT!! Reference needed when wanting to use directives
import '@/core/directive/translate/TranslateDirective'

@Component({
  components: {
    Translator
  }
})
export default class TranslateDemo extends Vue {
  // !!IMPORTANT!! TranslateService is required for translations
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  translateKey = 'UI_NETWORK_ADMIN.MAIL.TEMPLATES.ADD.TITLE'
}
</script>
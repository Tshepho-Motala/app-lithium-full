<template>
  <span v-translate="translationKey"></span>
</template>

<script lang='ts'>
import { Component, Vue, Prop, Inject } from 'vue-property-decorator'
import '@/core/directive/translate/TranslateDirective'
import TranslateServiceInterface from '@/core/interface/service/TranslateServiceInterface'

@Component
export default class Translator extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  @Prop({ default: '' }) translationKey!: string
}
</script>
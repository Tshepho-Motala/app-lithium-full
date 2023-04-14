import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import { Component, Inject, Vue } from "vue-property-decorator";

@Component
export default class TranslationMixin extends Vue {
    @Inject("translateService")
    private readonly translateService!: TranslateServiceInterface;

    $translate(key: string): string {
        return this.translateService?.instant(key);
    }
}

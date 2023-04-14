import { isEmpty } from "@/core/utils/objectUtils";
import { Component, Inject, Vue } from "vue-property-decorator";
import DomainItem from "../models/DomainItem";
import {BankAccountDomainInterface, DomainSingleSelectInterface} from "@/core/interface/DomainSingleSelectInterface";


@Component
export default class AssetMixin extends Vue {
    domain: DomainSingleSelectInterface | BankAccountDomainInterface | null = null;

    snack = {
        show: false,
        message: "",
        type: "success",
        timeout: 5000,
    };

    get selectedDomain() {
        if(this.domain) {
            if (this.domain.title) {
                return this.domain.title;
            } else if (this.domain.name) {
                return this.domain.name;
            }
        } else {
            return ''
        }
    }

    onDomainSelected(domain: DomainSingleSelectInterface | BankAccountDomainInterface | null) {
        this.domain = domain;
    }
    
    onMessage(message: any) {
        this.snack = { ...this.snack, ...message, show: true}
    }
}
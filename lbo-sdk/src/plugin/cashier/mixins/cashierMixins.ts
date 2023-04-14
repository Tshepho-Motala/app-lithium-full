import {RootScopeInterface} from "@/core/interface/ScopeInterface";

import LogServiceInterface from "@/core/interface/service/LogServiceInterface";
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";

import {Component, Inject, Vue} from "vue-property-decorator";

import {format, utcToZonedTime} from "date-fns-tz";
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";
import {SnackbarInterface} from "@/core/interface/AutoWithdrawalInterface";


@Component
export default class cashierMixins extends Vue {

    @Inject('translateService') readonly translateService!: TranslateServiceInterface
    @Inject('userService')
    readonly userService!: UserServiceInterface;

    @Inject("rootScope")
    readonly rootScope!: RootScopeInterface;

    @Inject("logService")
    logService!: LogServiceInterface;
    snackbar: SnackbarInterface = {
        show: false,
        text: '',
        color: 'success',
    }
    rules:any = {
        required: v => !!v || this.translateAll("UI_NETWORK_ADMIN.CASHIER.TRANSACTION.MANUAL_ADJUSTMENT.REQUIRED_FIELD")
    }


    formatCurrency(currencyCode, amount): string {
        const amountValue = Number(amount)
        if (amount !== null  && amountValue >= 0) {
            return currencyCode + amountValue.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
        } else {
            return 'Not specified'
        }
    }
    formatCurrencyNumber(amount): string {
        const amountValue = Number(amount)
        if (amount !== null ) {
            return amountValue.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
        } else {
            return '0'
        }
    }

    formatCurrencyNumberCent(amount): string {
        const amountValue = Number(amount) / 100
        if (amount !== null ) {
            return amountValue.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
        } else {
            return '0'
        }
    }

    formatDate(millis: number): string {
        const date = new Date(millis)
        const zonedDate = utcToZonedTime(date, 'Etc/GMT')

        return format(zonedDate, 'yyyy-MM-dd HH:mm:ss')
    }


    formatCurrencyCent(currencyCode, amount): string {
        const amountValue = Number(amount) / 100
        if (amountValue >= 0) {
            return currencyCode + amountValue.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
        } else {
            return 'Not specified'
        }
    }

    getColorStatus(status: string) {
        if (['SUCCESS', 'APPROVED', 'AUTO_APPROVED'].includes(status)) {
            return 'success'
        } else if (['DECLINED', 'FATALERROR', 'CANCEL', 'PLAYER_CANCEL'].includes(status)) {
            return 'error'
        } else if (['WAITFORAPPROVAL'].includes(status)) {
            return 'info'
        }
        else if (['WAITFORPROCESSOR'].includes(status)) {
            return 'blue-grey'
        }
        else {
            return 'secondary'
        }
    }

    hasRole(role:string){
        return this.userService.hasRole(role)
    }

    // Translation Methods
    translateAll(transStr: string) {
        return this.translateService.instant(transStr)
    }
}
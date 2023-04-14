import { Component, Inject, Vue } from "vue-property-decorator";
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import {
    AutoWithdrawalItemField,
    AutoWithdrawalItemOperator,
    SnackbarInterface
} from "@/core/interface/AutoWithdrawalInterface";
import AutoWithdrawalRulesetsApiInterface from "@/core/interface/axios-api/AutoWithdrawalRulesetsApiInterface";
import AutoWithdrawalRulesetsApiService from '@/core/axios/axios-api/AutoWithdrawalRulesetsApi'

@Component

export default class AutoWithdrawalRuleMixin extends Vue {
    @Inject('rootScope') readonly rootScope!: RootScopeInterface
    @Inject('userService') readonly userService!: UserServiceInterface
    rulesFields: AutoWithdrawalItemField[] = []
    rulesOperators:AutoWithdrawalItemOperator[] = []
    snackbar: SnackbarInterface = {
        show: false,
        text: '',
        color: 'success',
    }

    AutoWithdrawalRulesetsApiService: AutoWithdrawalRulesetsApiInterface = new AutoWithdrawalRulesetsApiService(this.userService)

     created(){
         this.doWork()
    }

    async doWork() {
       await this.loadFields()
       await this.loadOperators()
    }


    async loadFields() {
        try {
            const response = await this.AutoWithdrawalRulesetsApiService.ruleFieldsDataUrl()
            if (response?.data && response.data.successful) {
                this.rulesOperators = response.data.data
            } else {
                this.rulesOperators = []
            }
        } catch (err) {
            console.error(err.message)
        }
    }

    async loadOperators() {
        try {
            const response = await this.AutoWithdrawalRulesetsApiService.ruleOperatorsList()
            if (response?.data && response.data.successful) {
                this.rulesFields = response.data.data
            } else {
                this.rulesFields = []
            }

        } catch (err) {
            console.error(err.message)
        }
    }

    hasRoleForDomain (role, domain) {
        if(Array.isArray(role)) {
            const results:any = []
            role.forEach((el:any) => {
                let isHas =  this.userService.hasRoleForDomain(domain, el)
                results.push(isHas)
            })
            const isNotHasRole:boolean = results.find((elem:any) => elem === false)
            return !isNotHasRole ? false : true
        }
        return  this.userService.hasRoleForDomain(domain, role)
    }

    findNameRulesFields(item) {
        let ruleField:any = undefined
        this.rulesOperators.forEach((el:any) => {
            if(el.id === item.field){
                ruleField = el
            }
        })
        if(ruleField) {
            return ruleField.displayName
        }
        return ''
    }
    findNameRulesOperator(item) {
        let ruleOperator:any = undefined
        this.rulesFields.forEach((el:any) => {
            if(Number(el.id) === Number(item.operator)){
                ruleOperator = el
            }
        })
        if(ruleOperator) {
            return ruleOperator.displayName
        }
        return ''
    }
}
import { Component, Vue } from 'vue-property-decorator'

@Component
export default class RulesMixin extends Vue {
  $refs!: {
    form: HTMLFormElement
  }

  rulesBasic = {
    required: [(v) => !!v || 'Required'],
    gtZero: [(v) => (v && v > 0) || 'Value must be above zero']
  }

  rules = {
    required: this.rulesBasic.required,
    gtZero: [...this.rulesBasic.required, ...this.rulesBasic.gtZero],
    selectedGtZero: [(v) => !!(v && v.length) || 'required']
  }
}

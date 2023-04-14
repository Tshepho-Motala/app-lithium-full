import { Vue } from 'vue-property-decorator'
import Translate from './Translate'

Vue.directive('translate', {
  bind: (element, binding, vnode) => {
    const { translateService } = vnode.context as any

    if (!translateService) {
      console.error('Please inject the TranslateService to allow for translation.')
      return
    }

    const translate = new Translate(translateService)
    element.innerHTML = translate.this(binding.value)
  },
  update(element, binding, vnode) {
    const { translateService } = vnode.context as any

    if (!translateService) {
      console.error('Please inject the TranslateService to allow for translation.')
      return
    }

    const translate = new Translate(translateService)
    element.innerHTML = translate.this(binding.value)
  }
})

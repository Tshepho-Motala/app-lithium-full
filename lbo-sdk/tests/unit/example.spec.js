import { shallowMount } from '@vue/test-utils'
import './vuetify-setup'
import HelloWorld from '../../src/core/components/HelloWorld.vue'

describe('HelloWorld.vue', () => {
  it('renders props.msg when passed', () => {
    // const msg = "Welcome to Vuetify"
    const wrapper = shallowMount(HelloWorld, {
      propsData: { 
        msg: "Welcome to Vuetify"
       }
    })
    expect(wrapper.text()).toContain("Welcome to Vuetify")
  })
})

import { Vue } from 'vue-property-decorator'
import RoleCheck from './RoleCheck'

Vue.directive('role', {
  bind: (element, binding, vnode) => {
    // We want to do the check as the control is bound
    // to not 'flash' a valid control before deleting
    const { userService } = vnode.context as any

    if (!userService) {
      console.error('Please inject the UserService to allow for role checks.')
      return
    }

    const roleCheck = new RoleCheck(userService)
    // Technical debt to relook at this: https://jira.livescore.com/browse/PLAT-3222
    let hasRole = roleCheck.checkByUnknown(binding.value, vnode?.data?.attrs?.domain)

    if (!hasRole) {
      // Remove the original controls
      element.innerHTML = '-Invalid Role-'
    }
  },
  inserted(element, binding, vnode) {
    // We can only delete the control once its inserted.
    // This may seem double work but it's necessary
    const { userService } = vnode.context as any

    if (!userService) {
      console.error('Please inject the UserService to allow for role checks.')
      return
    }

    const roleCheck = new RoleCheck(userService)
    let hasRole = roleCheck.checkByUnknown(binding.value, vnode?.data?.attrs?.domain)

    if (!hasRole) {
      // Remove the actual node
      element.parentElement?.removeChild(element)
    }
  },
  update(element, binding, vnode) {
    // This is just here for the dynamic update, and won't really be called in the wild
    const { userService } = vnode.context as any

    if (!userService) {
      console.error('Please inject the UserService to allow for role checks.')
      return
    }

    const roleCheck = new RoleCheck(userService)
    element.hidden = !roleCheck.checkByUnknown(binding.value, vnode?.data?.attrs?.domain)
  }
})

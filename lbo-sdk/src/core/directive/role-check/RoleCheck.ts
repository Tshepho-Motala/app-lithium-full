import UserServiceInterface from '@/core/interface/service/UserServiceInterface'

export default class RoleCheck {
  defaultVisibility = false
  userService: UserServiceInterface

  constructor(userService: UserServiceInterface) {
    this.userService = userService
  }

  checkByUnknown(roles: undefined | string | string[], domain?: string | undefined) {
    if (Array.isArray(roles)) {
      return this.checkList(roles, domain)
    }
    return this.checkCommaSeparated(roles, domain)
  }

  checkCommaSeparated(roles: string | undefined, domain?: string | undefined) {
    if (!roles || roles.length === 0) {
      return this.defaultVisibility
    }

    const roleList = roles.split(',').map((role) => role.trim())
    return this.checkList(roleList, domain)
  }

  checkList(roleList: string[], domain?: string | undefined) {
    if (!roleList || roleList.length === 0) {
      return this.defaultVisibility
    }

    for (const role of roleList) {
      if (this.checkOptional(role, domain)) {
        return true
      }
    }
    return false
  }

  checkOptional(role: string, domain?: string | undefined) {
    if (!domain) {
      return this.check(role)
    }
    return this.checkWithDomain(role, domain)
  }

  check(role: string) {
    return this.userService.hasRole(role)
  }

  checkWithDomain(role: string, domain: string) {
    return this.userService.hasRoleForDomain(domain, role)
  }
}

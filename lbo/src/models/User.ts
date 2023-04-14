import { UserRole } from './UserRole'

export default class User {
  firstName = ''
  lastName = ''
  readonly fullName: string = ''
  userRole: UserRole = UserRole.Anonymous

  dateCreated: Date = new Date()
  enabled = true

  toModel(): string {
    return JSON.stringify({
      firstName: {
        component: 'text'
      }
    })
  }
}

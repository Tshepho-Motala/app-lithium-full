import User from './User'

export default class Accounts {
  users: User[] = []

  getById(id: string): User {
    return new User()
  }
}

import ApiAuth from './apiConnections/apiAuth'
import ApiConfig from './apiConnections/apiConfig'
import ApiUser from './apiConnections/apiUser'

class ApiConnect {
  auth: ApiAuth
  user: ApiUser
  config: ApiConfig

  constructor() {
    this.auth = new ApiAuth()
    this.user = new ApiUser()
    this.config = new ApiConfig()
  }
}

export default new ApiConnect()

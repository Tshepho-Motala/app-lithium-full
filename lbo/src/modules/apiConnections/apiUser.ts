import { Config } from '@/common/config'
import { UserEndpoint } from '@/common/enums'
import ProfileModel from '@/common/models/ProfileModel'

export default class ApiUser {
  url = process.env['API_URL'] || Config.ApiUrl

  async profile(): Promise<ProfileModel> {
    const response = await fetch(`${this.url}${UserEndpoint.profileGet}`, {
      credentials: 'include'
    })
    return (await response.json()) as ProfileModel
  }
}

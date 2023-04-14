import { DomainItemInterface } from "@/plugin/cms/models/DomainItem";
import { CredentialsInterface } from "../provider/AuthenticationProviderInterface";

export default interface UserServiceInterface {
  authenticated: boolean

  roles: () => string[]
  rolesForDomain: (domain: string) => string[]

  domainsWithAnyRole: (roles: string[]) => DomainItemInterface[]
  domainsWithRole: (role: string) => DomainItemInterface[]

  hasAdminRole: () => boolean
  hasAdminRoleForDomain: (domain: string) => boolean
  hasRole: (role: string) => boolean
  hasRoleForDomain: (domain: string, role: string) => boolean

  playerDomainsWithAnyRole: (roles: string[]) => any[]

  authenticate: (credentials?: CredentialsInterface) => Promise<void>
  logout: () => void
  _refreshToken: () => void
}

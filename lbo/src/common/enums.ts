export enum Role {
  Anonymous,
  User,
  Administrator
}

export enum ConfigEndpoint {
  sideMenuItemsGet = '/config.sideMenuItems',
  routerItemsGet = '/config.routerItems',
  pingGet = '/config.ping'
}

export enum AuthEndpoint {
  loginPost = '/auth.login',
  logoutPost = '/auth.logout'
}

export enum UserEndpoint {
  profileGet = '/user.profile'
}

export enum Token {
  Anonymous = 'token-anonymous',
  User = 'token-user',
  Admin = 'token-admin'
}

export enum PagePosition {
  Auto = 'auto'
}

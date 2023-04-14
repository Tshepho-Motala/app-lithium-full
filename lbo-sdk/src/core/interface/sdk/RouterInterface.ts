import { VueRouter } from 'vue-router/types/router'

export interface RouterHookInterface {
  router: VueRouter
  add(uid: string, item: RouterInterface): void
}

export interface RouterInterface {
  path: string
  name: string

  parentName?: string | null
  redirect?: string | { [key: string]: any } | undefined
  meta?: { [key: string]: any } | undefined
}

import { RouteConfig } from 'vue-router/types/router'
import { RouterInterface } from '../interfaces'

export default class RouteModel implements RouterInterface {
  path: string
  name: string

  parentName: string | null
  redirect: string | { [key: string]: any } | undefined
  meta: { [key: string]: any } | undefined

  constructor(
    name: string,
    path: string,
    { parentName = null, redirect, meta }: RouteModelConstructor
  ) {
    this.name = name
    this.path = path
    this.parentName = parentName
    this.redirect = redirect
    this.meta = meta
  }

  toRouteConfig(): RouteConfig {
    const RouteMap: { [key: string]: () => Promise<typeof import('*.vue')> } = {
      private: () => import('@/views/PrivateView.vue'),
      admin: () => import('@/views/AdminView.vue'),
      home: () => import('@/views/UserPages/UserHomePage.vue'),
      userDashboard: () => import('@/views/UserPages/UserDashboardPage.vue'),
      adminDashboard: () => import('@/views/AdminPages/AdminDashboardPage.vue')
    }

    return {
      path: this.path,
      name: this.name,
      component: RouteMap[this.name],
      redirect: this.redirect,
      meta: this.meta
    }
  }
}

export interface RouteModelConstructor {
  parentName?: string | null
  redirect?: string | { [key: string]: any } | undefined
  meta?: { [key: string]: any } | undefined
}

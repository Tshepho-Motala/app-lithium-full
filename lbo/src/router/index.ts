import Vue from 'vue'
import VueRouter, { RouteConfig } from 'vue-router'

// Views are used to template the overall look of the pages it contains
import AnonymousView from '@/views/AnonymousView.vue'
import PrivateView from '@/views/PrivateView.vue'
import AdminView from '@/views/AdminView.vue'

// Pages contain the bulk of User Interaction and Functionality
import LoginPage from '@/views/AuthPages/LoginPage.vue'
import HomePage from '@/views/UserPages/UserHomePage.vue'
import UserDashboardPage from '@/views/UserPages/UserDashboardPage.vue'
import AdminDashboardPage from '@/views/AdminPages/AdminDashboardPage.vue'

import { Role } from '@/common/enums'
import { StoreProxy } from '@/store'

Vue.use(VueRouter)

const routes: Array<RouteConfig> = [
  {
    path: '/auth',
    name: 'auth',
    component: AnonymousView,
    children: [
      {
        path: 'login',
        name: 'login',
        component: LoginPage
      }
    ]
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminView,
    redirect: {
      name: 'adminDashboard'
    },
    meta: {
      auth: true,
      role: Role.Administrator
    },
    children: [
      {
        path: 'dashboard',
        name: 'adminDashboard',
        component: AdminDashboardPage
      }
    ]
  },
  {
    path: '/',
    name: 'private',
    component: PrivateView,
    redirect: { name: 'userDashboard' },
    meta: {
      auth: true,
      role: Role.User
    },
    children: [
      {
        path: 'home',
        name: 'home',
        component: HomePage
      },
      {
        path: 'dashboard',
        name: 'userDashboard',
        component: UserDashboardPage
      }
    ]
  },
  { path: '*', redirect: { name: 'private' } }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach(async (to, from, next) => {
  const pageRequiresAuthentication = to.matched.some((x) => x.meta.auth)

  // If the page does not require authentication, continue
  if (!pageRequiresAuthentication) {
    next()
    return
  }

  // If the page requires auth, and we are already authenticated
  if (pageRequiresAuthentication && StoreProxy.AuthStore.authenticated) {
    const routeRecord = to.matched.find((x) => x.meta.role)

    // If there is no route within {to} with any role, continue
    if (!routeRecord) {
      next()
      return
    }

    // If there the current user is Anonymous (somehow de-authed), go to login
    if (StoreProxy.AuthStore.role === Role.Anonymous) {
      next({
        name: 'login',
        query: {
          ...from.query,
          redirect: to.path
        }
      })
      return
    }

    // Otherwise just continue
    next()
    return
  } else {
    // If the page requires auth, and we are not logged in
    if (to.name !== 'login') {
      next({
        name: 'login',
        query: {
          ...from.query,
          redirect: to.path
        }
      })
    } else {
      next()
    }
    return
  }
})

export default router

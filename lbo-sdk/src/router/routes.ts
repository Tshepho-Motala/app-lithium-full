// This will get large for now, we'll split it out to separate route files as we
// discuss virtual domains
export default [
  {
    name: 'client-auth',
    path: '/client-auth',
    component: () => import('@/plugin/authentication/ClientAuth.vue'),
    meta: {
      title: 'Plugin: Client Auth',
      angularPath: '/login',
      mount: '#vueMountLogin'
      // key: 'plugin-ClientAuth' -- If needed
    }
  },
  {
    name: 'casino-cms',
    path: '/casino-cms',
    component: () => import('@/plugin/cms/CMS.vue'),
    meta: {
      title: 'Plugin: Casino CMS',
      angularPath: '/dashboard/cmsbuilderdemo',
      mount: '#vueMountCmsBuilder'
    }
  },
  {
    name: 'cashier-config',
    path: '/dashboard/cashier/config',
    component: () => import('@/plugin/cashier/config/CashierConfigPage.vue'),
    meta: {
      title: 'Plugin: Cashier Config',
      angularPath: '/dashboard/cashier/config',
      mount: '#vueMountCashierConfig'
    }
  },
  {
    name: 'Promotions',
    path: '/promotions-mock/:tab?',
    component: () => import('@/plugin/promotions/Promotions.vue'),
    meta: {
      title: 'Plugin: Promotion Schedule',
      mount: '#vuePromotions',
      angularPath: '/dashboard/promotions/beta'
    }
  },
  // {
  //   name: 'Player Reward History',
  //   path: '/player-reward-history',
  //   component: () => import('@/plugin/promotions/reward/RewardPlayerHistory.vue'),
  //   meta: {
  //     title: 'Player Reward History'
  //     // angularPath: '/promotions-mock',
  //     // mount: '#vuePromotions'
  //   }
  // },
  {
    name: 'casino-games',
    path: '/dashboard/casino/games',
    component: () => import('@/plugin/games/GameSearchTopBar.vue'),
    meta: {
      title: 'Plugin: Games Search',
      angularPath: '/dashboard/casino/games',
      mount: '#vueMountGameSearchTopBar'
    }
  },
  {
    name: 'banner-management',
    path: '/dashboard/casino/banners',
    component: () => import('@/plugin/cms/banners/BannerList.vue'),
    meta: {
      title: 'Plugin: Banner Management',
      angularPath: '/dashboard/banner-management',
      mount: '#vueMountBannerManagement'
    }
  }
]

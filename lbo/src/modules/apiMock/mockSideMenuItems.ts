import SideMenuItemModel from '@/common/models/MenuModel'

class MockSideMenuItems {
  get admin(): SideMenuItemModel[] {
    return [
      new SideMenuItemModel({
        uid: 'a',
        title: 'Admin Dashboard',
        to: 'adminDashboard',
        icon: 'mdi-view-dashboard'
      }),
      new SideMenuItemModel({
        uid: 'b',
        title: 'User Management',
        disabled: true,
        icon: 'mdi-account-edit'
      }),
      new SideMenuItemModel({
        uid: 'c',
        title: 'Reports',
        disabled: true,
        icon: 'mdi-file-chart'
      })
    ]
  }

  get user(): SideMenuItemModel[] {
    return [
      new SideMenuItemModel({
        uid: 'a',
        title: 'User Dashboard',
        to: 'userDashboard',
        icon: 'mdi-view-dashboard'
      }),
      new SideMenuItemModel({
        uid: 'b',
        title: 'My Documents',
        disabled: true,
        icon: 'mdi-folder'
      }),
      new SideMenuItemModel({
        uid: 'c',
        title: 'Search',
        disabled: true,
        icon: 'mdi-magnify'
      })
    ]
  }
}

export default new MockSideMenuItems()

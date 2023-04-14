import { Role } from '@/common/enums'
import RouteModel from '@/common/models/RouteModel'

class MockRoutes {
  get admin(): RouteModel[] {
    return [
      new RouteModel('admin', '/admin', {
        redirect: { name: 'adminDashboard' },
        meta: { auth: true, role: Role.Administrator }
      }),
      new RouteModel('adminDashboard', 'dashboard', { parentName: 'admin' }),
      ...this.user
    ]
  }

  get user(): RouteModel[] {
    return [
      new RouteModel('private', '/', {
        redirect: { name: 'PrivateView' },
        meta: { auth: true, role: Role.User }
      }),
      new RouteModel('home', 'home', {
        parentName: 'private'
      }),
      new RouteModel('userDashboard', 'dashboard', { parentName: 'private' })
    ]
  }
}

export default new MockRoutes()

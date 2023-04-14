import { createModule } from 'vuex-class-component'

const ProfileModule = createModule({
  namespaced: 'ProfileModule',
  strict: false,
  target: 'nuxt'
})

export class ProfileStore extends ProfileModule {}

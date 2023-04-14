<template>
  <div v-role="wantedRoles">
    <slot> </slot>
  </div>
</template>

<script lang='ts'>
import { Component, Inject, Prop, Vue } from 'vue-property-decorator'

import '@/core/directive/role-check/RoleCheckDirective'
import UserServiceInterface from '@/core/interface/service/UserServiceInterface'

@Component
export default class PermissionValidator extends Vue {
  @Inject('userService') readonly userService!: UserServiceInterface

  @Prop({ default: () => [] }) roleList!: string[]
  @Prop({ default: '' }) roles!: string
  @Prop({ default: null }) domain!: string | null

  get wantedRoles(): string[] {
    const r: string[] = this.roleList

    if (this.roles.length > 0) {
      const rSplit = this.roles.split(',')
      for (const rs of rSplit) {
        r.push(rs.trim())
      }
    }

    return r
  }
}
</script>
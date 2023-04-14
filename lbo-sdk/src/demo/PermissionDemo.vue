<template>
  <v-row>
    <v-col cols="12">
      <span class="text-h4">DEMO - Permissions / Roles</span>
    </v-col>

    <v-col cols="12">
      <v-divider></v-divider>
    </v-col>

    <v-col cols="12">
      <div class="text-h5">Role Check through Component</div>

      <RoleCheck roles="UNKNOWN"> Invalid Role: None </RoleCheck>

      <RoleCheck> No Role: None </RoleCheck>

      <RoleCheck roles="PLAYER_VIEW"> Valid Role: Visible </RoleCheck>

      <!-- EXAMPLE --
        <RoleCheck roles="ROLE_1,ROLE_2,ROLE_3" :roleList="['ROLE_1','ROLE_2']" domain="DOMAIN_NAME">
          You can use either "roles" with a comma separated string
          You can use "roleList" as an array of roles (Useful if you want to bind to a local array in code)
          You can use both

          You can add a domain name to check a role for that domain
        </RoleCheck> 
      -->
    </v-col>

    <v-col cols="12">
      <v-divider></v-divider>
    </v-col>

    <v-col cols="12">
      <div class="text-h5">Role Check through Directive</div>

      <div v-role="'UNKNOWN'">Invalid Role: None</div>

      <div v-role="">No Role: None</div>

      <div v-role="'PLAYER_VIEW'">Valid Role: Visible</div>
    </v-col>

    <v-col cols="12">
      <v-divider></v-divider>
    </v-col>

    <v-col cols="12">
      <div class="text-h5">Interactive Role Check</div>

      <v-text-field label="Type Roles" v-model="selectedRoles" :hint="currentRoles" persistent-hint></v-text-field>

      <div v-role="selectedRoles" class="pa-4 ma-2 interactive">
        This control will change state based on the role selected above.
      </div>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import { Component, Vue, Inject } from 'vue-property-decorator'
import RoleCheck from '@/plugin/components/PermissionValidatior.vue'
import UserServiceInterface from '@/core/interface/service/UserServiceInterface'

// !!IMPORTANT!! Reference needed when wanting to use directives
import '@/core/directive/role-check/RoleCheckDirective'

@Component({
  components: {
    RoleCheck
  }
})
export default class PermissionDemo extends Vue {
  // !!IMPORTANT!! UserService is required for permissions and role checks
  @Inject('userService') readonly userService!: UserServiceInterface

  selectedRoles = "PLAYER_VIEW,ROLE_USER"
  get currentRoles() {
    return "Current Roles: " + this.userService.roles().join(',')
  }
}
</script>

<style scoped>
.interactive {
  border: 1px dashed #CCC;
}
</style>
<template>
  <div class="mobile-nav__wrap flex justify-end">
    <v-btn   @click="menuOpen = !menuOpen" class="text-none" v-bind="attrs" v-on="on">
      <span v-text="'Quick Actions'"></span>
      <v-icon right>
        {{ menuOpen ? 'mdi-chevron-up' : 'mdi-chevron-down' }}
      </v-icon>
    </v-btn>

    <nav class="mobile-nav mt-2" v-if="menuOpen">
      <transition-group :name="transitionName" tag="div" class="mobile-nav__levels" ref="levels">
        <div v-if="currentLevel"
             class="mobile-nav__level"
             role="group"
             :aria-labelledby="`mobile-nav-level-label-${getPathName(currentLevel.path)}`"
             tabindex="-1"
             :key="getPathName(currentLevel.path)"
             :ref="`level-${getPathName(currentLevel.path)}`"
        >
          <v-btn color="primary"  v-if="currentLevelDepth > 0" @click="navigate(currentLevel.path.slice(0, currentLevelDepth - 1))" ref="back" block elevation="0" class="text-none justify-start mt-0 mb-0" >
            <v-icon left> mdi-chevron-left</v-icon> Back to {{ levels[currentLevelDepth].label }}
          </v-btn>
          <div class="mt-0 mb-0 pt-0 pl-0 pr-0 pb-0 " v-for="item in currentLevel.items" :key="item.path.join()">
            <v-btn block v-if="item.hasItems" @click="navigate(item.path)"  class="text-none align-content-start justify-space-between mt-0 mb-0"  elevation="0"  ref="link" >
                 <span>{{ item.label }}</span> <v-icon right> mdi-chevron-right </v-icon>
                </v-btn>
                <v-btn
                    v-else-if="item.visible && hasRoleForDomain(item.permission, item.domainName)"
                    @click="linkClickMethod(item)"
                    block
                    ref="link"  class="text-none align-content-start justify-start mt-0 mb-0" elevation="0"

                >
                 {{ item.label }}
            </v-btn>
            </div>
        </div>
      </transition-group>
    </nav>
  </div>
</template>

<script lang="ts">
import { Component, Inject, Vue } from 'vue-property-decorator'
import { RootScopeInterface } from '@/core/interface/ScopeInterface'
import UserServiceInterface from '@/core/interface/service/UserServiceInterface'

@Component
export default class ClientAuth extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('userService') readonly userService!: UserServiceInterface

  menuItems = this.rootScope.provide.quickActionProvider.menuItems
  menuOpen:boolean = false;
  currentPosition = [];
  levels = [];
  currentLevel = null;
  prevLevelDepth:number = 0;
  currentLevelDepth:number = 0;

  get transitionName() {
    return 'slide-' + (this.currentLevelDepth > this.prevLevelDepth ? 'left' : 'right');
  }

  navigate (path:any) {
    this.currentPosition = path;
    this.recalculateItems();
  }

  recalculateItems () {
    let items:any = this.menuItems;
    let levels:any = [];

    levels.push(this.getLevel([], null, items));

    this.currentPosition.forEach((index, depth) => {
      const path: any = [...this.currentPosition].slice(0, depth + 1);
      const label: any = items[index] && items[index].label || null;
      items = items[index] && items[index].items || [];
      levels.push(this.getLevel(path, label, items));
    });

    this.levels = levels;
    this.currentLevel = levels[levels.length - 1];
    this.prevLevelDepth = this.currentLevelDepth;
    this.currentLevelDepth = levels.length - 1;
  }

  getLevel (path: Array<any>, label:String | null, items:Array<any>) {
    return {
      path: path,
      label: label,
      items: items.map((item:any, index:any) => {
        return {
          path: [...path, index],
          hasItems: item.items && item.items.length > 0,
          ...item
        }
      })
    };
  }

  getPathName(path:String) {
    if (!path || path.length < 0) {
      return 0;
    }

    return [0, ...path].join('-');
  }

  handleArrowNavigation(direction:any) {
    if (!this.$refs.link) {
      return;
    }

    let  links:Array<any> = [];
    if (this.$refs.back) {
      links.push(this.$refs.back);
    }

    if (links.includes(document.activeElement)) {
      let nextIndex = links.indexOf(document.activeElement) + direction;
      if (nextIndex < 0) {
        nextIndex = links.length - 1;
      } else if (nextIndex > links.length - 1) {
        nextIndex = 0;
      }
      links[nextIndex].focus();
    } else {
      if (direction > 0) {
      } else {
        links[links.length - 1].focus();
      }
    }
  }

  linkClickMethod(item:any) {
    if(item?.method) {
      item.method()
    }
    this.clearMenu()
  }

  clearMenu(){
    this.menuOpen = false;
    this.currentPosition = [];
    this.levels = [];
    this.currentLevel = null;
    this.prevLevelDepth = 0;
    this.currentLevelDepth = 0;
    this.recalculateItems();
  }
  hasRoleForDomain (role:any, domain:any) {
    if(Array.isArray(role)) {
      const results:any = []
      role.forEach((el:any) => {
        let isHas =  this.userService.hasRoleForDomain(domain, el)
        results.push(isHas)
      })
      const isNotHasRole:any = results.find((elem:any) => elem === false)
      return isNotHasRole === false ? false : true
    }
    return  this.userService.hasRoleForDomain(domain, role)
  }
  mounted () {
    this.recalculateItems();
  }
}
</script>
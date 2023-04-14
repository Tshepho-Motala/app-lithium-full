<template>
  <v-menu
      v-model="menuIsVisible"
      :close-on-content-click="false"
      nudge-width="350"
      max-width="350"
      max-height="600"
      hide-details
      nudge-top="-40"
      right
  >
    <template v-slot:activator="{ on, attrs }">
      <v-btn
          v-bind="attrs"
          v-on="on"
          color="primary"
          style="text-transform: none;"
          dark
      >
        {{tagsAreSelectedText }}
        <v-icon
            dense
            right
            color="white"
        >
          mdi-chevron-down
        </v-icon>
      </v-btn>
    </template>
    <v-card class="px-4 py-4">
      <template>
        <template>
          <v-text-field
              class="mb-0  mt-0"
              v-model="search"
              label="Search"
          ></v-text-field>
        </template>
        <div v-if="items.length">
          <v-list class="overflow-y-auto mt-0 pt-0" max-height="400">
            <v-list-item style="min-height: 40px;" class="pl-2 pr-0 pt-0 pb-0 ma-0 " v-for="item in items"
                         :key="item.id">
              <v-checkbox
                  v-model="item.selected"
                  color="info"
                  :label="item.value"
                  hide-details
                  @change="selectTag(item)"
                  class="mt-0 pt-0 pb-0"
              >
              </v-checkbox>
            </v-list-item>
            <v-skeleton-loader v-if="moreDataToAvailable" v-intersect="loadNextPage" type="list-item@5"/>
          </v-list>

          <div class=" mt-4 text-right">
            <v-btn
                color="primary"
                dark
                @click="menuIsVisible = false"
            >
              Close
            </v-btn>
            <v-btn
                color="error"
                dark
                @click="clearList"
            >
              Clear
            </v-btn>
          </div>

        </div>

        <v-alert
            v-else
            outlined
            type="warning"
            class="mb-0"
        >
          List is empty
        </v-alert>

      </template>
    </v-card>
  </v-menu>
</template>

<script lang="ts">
import {Component, Mixins, Watch} from "vue-property-decorator";
import {AffiliateItemInterface, AffiliateParamsInterface, TagDropDown} from '@/core/interface/DropDownMenuInterface'
import UserApiInterface from "@/core/interface/axios-api/UserApiInterface";
import UserApi from "@/core/axios/axios-api/UserApi";
import cashierMixins from "@/plugin/cashier/mixins/cashierMixins";

@Component
export default class AffiliateDropDown extends Mixins(cashierMixins) {
  UserApiService: UserApiInterface = new UserApi(this.userService)
  menuIsVisible: boolean = false
  search: string = ''
  pageLoaded: number = 0
  totalCount: number = 0
  items: AffiliateItemInterface[] = []
  pageSize:number = 30
  clearSearch:boolean = false
  selectedList: AffiliateItemInterface[] = []


  @Watch('search')
  async onSearchAffiliates(value) {
      this.clearSearch = true
      this.pageLoaded = 0
      await this.loadUserAffiliates()
  }

  async mounted() {
    await this.loadUserAffiliates()
  }

  get moreDataToAvailable(): boolean {
    return Math.ceil(this.totalCount / this.pageSize) - 1 > this.pageLoaded
  }

  async loadNextPage(entries: IntersectionObserverEntry[]) {
    if (entries[0].isIntersecting && this.moreDataToAvailable) {
      this.pageLoaded = this.pageLoaded + 1
      this.clearSearch = false
      await this.loadUserAffiliates()
    }
  }

  async loadUserAffiliates() {
    const params: AffiliateParamsInterface = {
      size: this.pageSize,
      page: this.pageLoaded,
      name: this.search
    }
    try {
      const result = await this.UserApiService.userAffiliates(params)
      if (result?.data) {
        if (this.clearSearch) {
          const list = result.data.content
          list.forEach((el: AffiliateItemInterface) => {
            const hasElement = this.selectedList.some((i: AffiliateItemInterface) => i.id === el.id)
            if (hasElement) el.selected = true
          })
          this.items = list
        } else {
          const list = [...this.items, ...result.data.content]
          list.forEach((el: AffiliateItemInterface) => {
            const hasElement = this.selectedList.some((i: AffiliateItemInterface) => i.id === el.id)
            if (hasElement) el.selected = true
          })
          this.items = list
        }
        this.totalCount = result.data.totalElements
      }

    } catch (err) {
      this.logService.error(err)
    }
  }

  get someTagsAreSelected(): boolean {
    return this.items.some((i: AffiliateItemInterface) => i.selected === true) && this.items.some((i: AffiliateItemInterface) => i.selected === false)
  }

  get tagsAreSelectedText(): string {
    const list = this.selectedList
    if (list.length === 0 || !this.items.length) {
      return 'No affiliates selected'
    } else if (list.length > 1) {
      return list.length + ' ' + 'affiliates selected'
    }
    return list.length + ' ' + 'affiliate selected'
  }

  selectTag(item) {
    const hasElement = this.selectedList.some((i: AffiliateItemInterface) => i.id === item.id)
    if (this.selectedList.length && hasElement) {
      this.selectedList = this.selectedList.filter((i: AffiliateItemInterface) => i.id !== item.id)
    } else {
      this.selectedList.push(item)
    }
    this.$emit('changeAffiliate', this.selectedList)
  }

  async clearList() {
    this.search = ''
    this.selectedList = []
    this.clearSearch = true
    this.pageLoaded = 0
    this.items.forEach((el: AffiliateItemInterface) => el.selected = false)
    this.$emit('changeAffiliate', this.selectedList)
    await this.loadUserAffiliates()
    this.menuIsVisible = false
  }

  // Translation Methods
  translate(text: string): string {
    return this.translateService.instant("UI_NETWORK_ADMIN.DROP_DOWN_MENU." + text);
  }
}
</script>
<style scoped>
</style>
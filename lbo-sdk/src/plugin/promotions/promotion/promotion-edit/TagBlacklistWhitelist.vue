<template>
  <v-card flat outlined id="TagBlacklistWhitelist">
    <v-toolbar flat>
      <v-toolbar-title>
        <span>Tag Restrictions</span>
      </v-toolbar-title>
      <v-spacer></v-spacer>

      <v-switch class="mt-6" v-model="showOptions" @change="onShowOptionsChange"></v-switch>
    </v-toolbar>

    <template v-if="showOptions">
      <div class="pl-4 pr-6">
        <v-text-field append-icon="mdi-magnify" label="search" @input="onSearch" @click:clear="onClearSearch" clearable dense single-line hide-details/>
      </div>
      <v-divider></v-divider>

      <div class="d-flex">
        <!-- BLACKLIST -->
        <div style="width: 33.3%" class="d-flex flex-column">
          <div class="pa-2">
            <span class="text-subtitle-2">Blacklist</span>
          </div>
          <TagDraggable :items="blacklistTags" @change="onTagsUpdated" @start="dragStart(0)" @stop="dragStop(0)" />
          <v-spacer></v-spacer>
          <v-divider></v-divider>
          <div class="pa-2 text-caption font-weight-medium">{{ blacklistTags.length }} items</div>
        </div>

        <!-- DIVIDER -->
        <div style="width: auto" class="">
          <v-divider vertical></v-divider>
        </div>

        <!-- CURRENT -->
        <div style="width: 33.3%" class="d-flex flex-column">
          <div class="pa-2">
            <span class="text-subtitle-2">Available</span>
          </div>
          <TagDraggable :items="selectableTags" @change="onTagsUpdated" @start="dragStart(1)" @stop="dragStop(1)" />
          <v-spacer></v-spacer>
          <v-divider></v-divider>
          <div class="pa-2 text-caption font-weight-medium">{{ selectableTags.length }} items</div>
        </div>

        <!-- DIVIDER -->
        <div style="width: auto" class="">
          <v-divider vertical></v-divider>
        </div>

        <!-- WHITELIST -->
        <div style="width: 33.3%" class="d-flex flex-column">
          <div class="pa-2">
            <span class="text-subtitle-2">Whitelist</span>
          </div>
          <TagDraggable :items="whitelistTags" @change="onTagsUpdated" @start="dragStart(2)" @stop="dragStop(2)" />
          <v-spacer></v-spacer>
          <v-divider></v-divider>
          <div class="pa-2 text-caption font-weight-medium">{{ whitelistTags.length }} items</div>
        </div>
      </div>
    </template>
  </v-card>
</template>

<script lang='ts'>
import { Vue, Component, Prop, Inject, Watch, VModel } from 'vue-property-decorator'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import UserTag from '@/plugin/components/UserTag'
import TagDraggable from './TagDraggable.vue'
import { Promotion } from '../../Promotion'

@Component({
  components: {
    TagDraggable
  }
})
export default class TagBlacklistWhitelist extends Vue {
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @VModel({ required: true, type: Promotion }) readonly promotion!: Promotion

  @Prop({ required: true }) readonly domain!: DomainItemInterface

  _referenceTags: UserTag[] = [] // Allows us to reset
  selectableTags: UserTag[] = []

  blacklistTags: UserTag[] = []
  whitelistTags: UserTag[] = []

  showOptions = false

  mounted() {
    this.seed()
  }

  async seed() {
    await this.getTags()

    for (const { userCategoryId, type } of this.promotion.tagList) {
      // Find if we have it
      const index = this.selectableTags.findIndex((x) => x.id === userCategoryId.toString())
      if (index > -1) {
        const item = this.selectableTags.splice(index, 1)
        // Whitelist or blacklist
        if (type === 'blacklist') {
          this.blacklistTags.push(...item)
        }
        if (type === 'whitelist') {
          this.whitelistTags.push(...item)
        }
      }
    }

    this.showOptions = this.whitelistTags.length > 0 || this.blacklistTags.length > 0
  }

  @Watch('domain')
  async getTags() {
    const tags = await this.apiClients.serviceUser.getTagsForDomain(this.domain)
    if (tags !== null) {
      this.selectableTags = tags.map((contract) => UserTag.fromContract(contract, this.domain))
      this._referenceTags = [...this.selectableTags]
    }
  }

  onSearch(searchValue) {
    if (searchValue != null) {
      this.selectableTags = this._referenceTags.filter(t =>
          t.name.toLowerCase().match(searchValue.toLowerCase())
          && !this.whitelistTags.includes(t)
          && !this.blacklistTags.includes(t))
    }
  }

  onClearSearch() {
    this.selectableTags = this._referenceTags.filter(t =>
        !this.whitelistTags.includes(t)
        && !this.blacklistTags.includes(t))
  }

  onTagsUpdated() {
    this.promotion.tagList = []

    for (const tag of this.blacklistTags) {
      this.promotion.tagList.push({
        type: 'blacklist',
        userCategoryId: parseInt(tag.id)
      })
    }

    for (const tag of this.whitelistTags) {
      this.promotion.tagList.push({
        type: 'whitelist',
        userCategoryId: parseInt(tag.id)
      })
    }
  }

  dragStart(index: number) {}

  dragStop(index: number) {}

  onShowOptionsChange(val: boolean) {
    if (!val) {
      this.promotion.tagList = []
      this.blacklistTags = []
      this.whitelistTags = []
      this.selectableTags = [...this._referenceTags]
    }
  }
}
</script>

<style scoped>
</style>
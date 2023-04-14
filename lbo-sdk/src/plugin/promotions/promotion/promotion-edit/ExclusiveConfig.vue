<template>
  <v-card flat outlined id="ExclusiveConfig">
    <v-toolbar flat>
      <v-toolbar-title>
        <span>Mark Promotion as Exclusive</span>
      </v-toolbar-title>
      <v-spacer></v-spacer>

      <v-switch class="mt-6" v-model="promotion.exclusive"></v-switch>
    </v-toolbar>

    <template v-if="promotion.exclusive">
      <v-divider></v-divider>

      <div class="pa-4">
        <FileReaderText @data="onFileRead" :disabled="!promotion.exclusive" />
        <v-text-field v-model="manualAddItem" outlined label="Add User ID" prepend-icon="mdi-account-plus-outline" placeholder="User ID">
          <template #append-outer>
            <v-btn @click="onManualAdd" :disabled="!manualAddItem">Add</v-btn>
          </template>
        </v-text-field>
        <v-text-field
          v-if="!noResults"
          append-outer-icon="mdi-filter-variant"
          hide-details
          label="Filter"
          v-model="filterValue"
          @input="onFilterChange"
          :hint="filterResults"
        ></v-text-field>
      </div>

      <template v-if="!noResults">
        <v-divider></v-divider>
        <v-virtual-scroll :items="filteredPlayerDataList" item-height="49" :height="virtualScrollHeight">
          <template #default="{ index, item }">
            <v-list-item :key="index" @click="updateSelection(item)">
              <v-list-item-content>
                <v-list-item-title>
                  <span v-text="item.playerGuid"> </span>
                </v-list-item-title>
              </v-list-item-content>
              <v-list-item-action>
                <v-checkbox v-model="filteredPlayerDataList[index].selected"></v-checkbox>
              </v-list-item-action>
            </v-list-item>

            <v-divider v-if="index < total - 1"></v-divider>
          </template>
        </v-virtual-scroll>
        <v-divider></v-divider>
        <div class="pa-2 d-flex">
          <div>
            <span class="text-caption font-weight-medium" v-text="totalResults"></span>
          </div>
          <v-spacer></v-spacer>
          <div>
            <span class="text-caption font-weight-medium" v-text="totalSelected"></span>
          </div>
        </div>
      </template>
    </template>
  </v-card>
</template>

<script lang='ts'>
import { Vue, Component, VModel } from 'vue-property-decorator'
import { Promotion } from '../../Promotion'
import FileReaderText from '@/plugin/components/file-readers/FileReaderText.vue'

/**
 * For the player data:
 * This needs to run from an internal data model and only update the promotion once
 * a selection has changed.
 * We must not bind directly to the promotion model
 */
@Component({
  components: {
    FileReaderText
  }
})
export default class ExclusiveConfig extends Vue {
  @VModel({ required: true, type: Promotion }) readonly promotion!: Promotion

  playerDataList: PlayerData[] = []

  filterValue: string = ''
  filteredPlayerDataList: PlayerData[] = []

  manualAddItem = ''

  get selectedPlayerDataList() {
    return this.playerDataList.filter((x) => x.selected)
  }

  get noResults() {
    return this.playerDataList.length === 0
  }

  get filterResults(): string {
    if (!this.filterValue) {
      return ''
    }
    return 'Filtered results: ' + this.filteredPlayerDataList.length
  }

  get total(): number {
    return this.playerDataList.length
  }

  get totalResults(): string {
    return 'Total Results: ' + this.total
  }

  get totalSelected(): string {
    return 'Total Selected: ' + this.selectedPlayerDataList.length
  }

  get virtualScrollHeight(): number {
    const desired = this.filteredPlayerDataList.length * 49
    if (desired > 300) {
      return 300
    }
    return desired
  }

  mounted() {
    let originalIndex = 0
    for (const player of this.promotion.exclusivePlayers) {
      // Ensure we strip out the GUID
      const guidParts = player.guid.split('/')
      let playerGuid = player.guid
      if (guidParts.length === 2) {
        playerGuid = guidParts[1]
      }
      this.playerDataList.push({
        originalIndex,
        playerGuid,
        selected: true
      })
      originalIndex++
    }

    if (this.playerDataList.length > 0) {
      this.onFilterChange()
      this.updatePromotionsModel()
    }
  }

  updatePromotionsModel() {
    if (!this.promotion.domain) {
      return
    }

    this.promotion.exclusivePlayers = []
    for (const item of this.selectedPlayerDataList) {
      // Ensure we append the domain name
      const guid = this.promotion.domain.name + '/' + item.playerGuid
      this.promotion.exclusivePlayers.push({
        guid
      })
    }
  }

  onManualAdd() {
    this.playerDataList.push({
      playerGuid: this.manualAddItem,
      originalIndex: this.playerDataList.length - 1,
      selected: true
    })

    this.onFilterChange()
    this.updatePromotionsModel()

    this.manualAddItem = ''
  }

  onFileRead(data: string) {
    const newlineRegex = new RegExp(/\r?\n/, 'g')
    let originalIndex = 0
    const items = data.split(newlineRegex).map((playerGuid) => {
      const o = {
        selected: true,
        playerGuid,
        originalIndex
      }
      originalIndex++
      return o
    })

    this.playerDataList.push(...items)

    this.onFilterChange()
    this.updatePromotionsModel()
  }

  updateSelection(item: PlayerData) {
    // this.playerDataList[item.originalIndex].selected = !this.playerDataList[item.originalIndex].selected
    this.updatePromotionsModel()
  }

  onFilterChange() {
    if (!this.filterValue) {
      this.filteredPlayerDataList = this.playerDataList
    }
    this.filteredPlayerDataList = this.playerDataList.filter((x) => x.playerGuid.toLowerCase().indexOf(this.filterValue) > -1)
  }
}

interface PlayerData {
  selected: boolean
  playerGuid: string
  originalIndex: number
}
</script>

<style scoped>
</style>
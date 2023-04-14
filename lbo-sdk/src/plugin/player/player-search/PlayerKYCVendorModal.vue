<template>
  <div>
    <v-dialog
        data-test-id="cnt-cashier-transaction-cancel-modal"
        v-model="dialog"
        max-width="600px"
    >
      <v-card>
        <v-toolbar
            color="primary"
            dark
            style="font-size: 22px"
        >
          <div class="d-flex justify-space-between align-content-center align-center" style="width: 100%">
            <span> Vendor data information</span>
            <v-chip @click="close" small
                    text-color="white"
                    color="error" label>

              <v-icon>
                mdi-close
              </v-icon>

            </v-chip>
          </div>

        </v-toolbar>
        <v-card-text  class="mt-4">
          <v-tabs
              v-if="uniqVendor.length"
              background-color="primary"
              v-model="tab"
              fixed-tabs
              slider-color="white"
              dark
          >
            <v-tabs-slider color="yellow"></v-tabs-slider>

            <v-tab

                v-for="item in uniqVendor"
                :key="item"
            >
              {{ item }}
            </v-tab>
          </v-tabs>

          <v-tabs-items v-model="tab">
            <v-tab-item
                v-for="item in uniqVendor"
                :key="item"
            >
              <v-card flat>

                <v-simple-table  height="500px" class="mt-3 table-striped" data-test-id="tbl-vendor-data">

                    <tbody>
                    <tr v-for="data in vendorDataFilter(item)" :key="data.id">
                      <th style="width: 40%; height: auto;   min-height: 40px;  padding: 5px; font-size: 14px;">
                        {{data.name}}
                      </th>
                      <td style="height: auto;   min-height: 40px;  padding: 5px;font-size: 14px;"> {{ data.value }}</td>
                    </tr>

                    </tbody>

                </v-simple-table>
              </v-card>
            </v-tab-item>
          </v-tabs-items>

          <v-alert
              type="info"
              v-if="!vendorData.length"

          >
            Vendor data information is empty
          </v-alert>
        </v-card-text>

      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import {Component, Inject,  Vue} from "vue-property-decorator";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";

@Component
export default class PlayerKYCVendorModal  extends Vue {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface
  dialog: Boolean = true
  vendorData: any[] = []
  uniqVendor:any[] = []
  tab =  null

  close() {
    this.rootScope.provide.playerKYCProvider.closeModal()
    this.dialog = false
  }
  mounted(){
    this.vendorData = this.rootScope.provide.playerKYCProvider.vendorData
    if(this.vendorData.length){
      this.makeUniqVendor()
    }
  }

  makeUniqVendor(){
    const array = [...this.vendorData]
    const allVendorArray = array.map((el:any) => {
      return el.vendor
    })

    const uniqSet = new Set(allVendorArray)
    this.uniqVendor  =   [...uniqSet];

  }
  vendorDataFilter(item){
    const arr:any = this.vendorData.filter((el:any) => {
      return el.vendor === item
    })
    return arr.sort((a:any, b:any) => a.name.localeCompare(b.name))
  }

}
</script>
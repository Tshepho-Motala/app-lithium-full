<template>
  <div>
    <v-tooltip bottom :color="tooltipColour">
      <template v-slot:activator="{ on, attrs }">
        <div v-bind="attrs" v-on="on">

          <!--          Start Generation-->
          <template v-if="canBeProcessed">
            <div>
              <v-btn  @click="onGenerateClick"
                     style="text-transform: capitalize;">
                <v-icon left>mdi-file-export-outline</v-icon>
                <Translator translationKey="UI_NETWORK_ADMIN.CSVEXPORT.BUTTON.EXPORT"/>
              </v-btn>
            </div>
          </template>

          <!--          Cancel Generation-->
          <v-btn v-else-if="generating" @click="onCancelClick" style="text-transform: capitalize;">
            <v-icon left>mdi-cancel</v-icon>
            <template v-if="generating">
              <div style="width: 90%">
                <span>{{ status }}</span>
                <v-progress-linear indeterminate></v-progress-linear>
              </div>
            </template>
          </v-btn>

          <!--          Download completed file -->
          <v-btn @click="onDownloadClick" v-else-if="completed"
                 style="text-transform: capitalize;">
            <v-icon left>mdi-cloud-download</v-icon>
            <Translator translationKey="UI_NETWORK_ADMIN.CSVEXPORT.BUTTON.DOWNLOAD"/>
          </v-btn>

          <!--          Reset Failed generation data -->
          <v-btn @click="resetState" v-else-if="failed" style="text-transform: capitalize;">
            <span>{{ status }}</span>
          </v-btn>

        </div>
      </template>
      <span>{{ toolTipValue }}</span>
    </v-tooltip>
    <v-snackbar
        v-model="snackbar.show"
        :color="snackbar.color"
        :timeout="15000"
    >
      {{ snackbar.text }}
      <template v-slot:action="{ attrs }">
        <v-btn
            color="black"
            text
            v-bind="attrs"
            @click="snackbar.show = false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script lang="ts">
import CsvGeneratorProvider, {
  ExportConfig,
  ExportProgress,
  ExportStatus
} from '@/core/interface/provider/CsvGeneratorProvider'
import {RootScopeInterface} from '@/core/interface/ScopeInterface'
import LogServiceInterface from '@/core/interface/service/LogServiceInterface'
import UserServiceInterface from '@/core/interface/service/UserServiceInterface'
import {isNull} from '@/core/utils/objectUtils'
import Translator from '@/plugin/components/Translator.vue'

import {Component, Inject, Vue, Watch} from 'vue-property-decorator'

import {isEmpty} from '../../core/utils/objects'
import {SnackbarInterface} from "@/core/interface/AutoWithdrawalInterface";
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";

@Component({
  components: {Translator}
})
export default class extends Vue {
  @Inject() rootScope!: RootScopeInterface
  @Inject() logService!: LogServiceInterface
  @Inject() userService!: UserServiceInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface

  snackbar: SnackbarInterface = {
    show: false,
    text: '',
    color: 'success',
  }

  generating = false
  completed = false
  disableButton = false
  failed = false
  errorComment = "not specified"


  config!: ExportConfig

  progressInternal: number = 0

  progress: ExportProgress = {
    status: ExportStatus.IDLE,
    comment: ''
  }

  mounted() {
    this.config = this.csv.getConfig()

    const hasPermission = true
        //this.hasPermission()
        //!hasPermission

    if (hasPermission) {
      this.getProgress()
    }
  }

  async getProgress() {
    try {

      if (isNull(this.config.reference)) {
        return;
      }

      this.progress = await this.csv.progress(this.config)

      if (this.progress.status === ExportStatus.CANCELED || this.progress.status === ExportStatus.DOWNLOADED) {
        return
      }

      if (this.progress.status === ExportStatus.CREATED || this.progress.status === ExportStatus.BUSY) {
        this.generating = true
      }
      if (this.progress.status === ExportStatus.FAILED && this.generating) {
        this.stopError(null)
      }
    } catch (error) {
      this.logService.error(error)
      this.progress.status = ExportStatus.FAILED
      this.stopError(error)
    }
  }

  async onDownloadClick() {
    try {
      await this.csv.download(this.config.reference)
      this.resetState()
    } catch (error) {
      this.logService.error(error)
      this.stopError(error)
    }
  }

  async onCancelClick() {
    try {
      await this.csv.cancelGeneration(this.config.reference)
      this.resetState()
    } catch (error) {
      this.logService.error(error)
      this.stopError(error)
    }
  }

  resetState() {
    this.snackbar.show = false
    this.snackbar.text = ''
    this.snackbar.color = 'success'
    this.generating = false
    this.failed = false
    this.errorComment = "not specified"
    this.progress.status = ExportStatus.IDLE
    this.progress.comment = ''
    this.completed = false
  }

  async onGenerateClick() {
    try {
      this.generating = true
      this.config = this.csv.getConfig()
      const genResponse = await this.csv.generate(this.config)

      this.progress = {
        status: genResponse.status,
        comment: genResponse.comment
      }

      this.config.reference = genResponse.reference

      if (genResponse.status === ExportStatus.FAILED) {
        this.stopError(null)
      }
    } catch (error) {
      this.logService.log(error)
      this.stopError(error)
    }
  }

  stopError(error) {
    this.failed = true
    if (error != null) {
      this.errorComment = error.data.error + " Response status:" + error.data.status
    } else if (this.progress.comment != undefined && this.progress.comment != "") {
      this.errorComment = this.progress.comment
    }
    this.progress.status = ExportStatus.FAILED
    this.generating = false
    this.setSnackBarError()
  }

  hasPermission(): boolean {
    //if (!this.config.role) {
      return true
    //}
   // return this.userService.hasRoleForDomain(this.config.domain, this.config.role)
  }

  setSnackBarError() {
    this.snackbar = {
      show: true,
      text: this.translate('UI_NETWORK_ADMIN.CSV.DOWNLOAD.FAILED') + this.errorComment,
      color: 'error'
    }
  }


  @Watch('progress.status')
  onComplete(status: ExportStatus, oldStatus: ExportStatus) {
    if (![ExportStatus.BUSY, ExportStatus.CREATED].includes(status)) {
      this.generating = false
    }
    if (ExportStatus.COMPLETE === status) {
      this.completed = true
    }
  }

  @Watch('generating')
  onGenerationStatusChange(newValue: boolean, oldVal: boolean) {
    clearInterval(this.progressInternal)
    if (newValue) {
      this.progressInternal = setInterval(this.getProgress, 1000)
    }
  }

  get toolTipValue(): string {
    if (this.completed) {
      return this.translate('UI_NETWORK_ADMIN.CSV.TOOLTIP.COMPLETED')
    } else if (this.generating) {
      return this.translate('UI_NETWORK_ADMIN.CSV.TOOLTIP.CANCEL')
    } else if (this.canBeProcessed && !this.generating) {
      return this.translate('UI_NETWORK_ADMIN.CSV.TOOLTIP.START')
    } else if (this.failed && !this.generating) {
      return this.translate('UI_NETWORK_ADMIN.CSV.TOOLTIP.FAILED') + this.errorComment
    } else return ""
  }

  translate(key: string): string {
    return this.translateService.instant(key)
  }

  get tooltipColour() {
    if (this.completed) {
      return "success"
    } else if (this.failed && !this.generating) {
      return "warning"
    } else return "default"
  }

  get canBeProcessed(): boolean {
    return !this.failed && !isEmpty(this.progress) && this.progress.status != ExportStatus.COMPLETE && this.progress.status != ExportStatus.BUSY
  }

  get status() {
    const busy = [ExportStatus.BUSY, ExportStatus.CREATED]

    if (busy.includes(this.progress.status as ExportStatus)) {
      return ExportStatus.BUSY
    }

    return this.progress.status
  }

  get csv(): CsvGeneratorProvider {
    return this.rootScope.provide.csvGeneratorProvider
  }
}
</script>

<style scoped>
</style>

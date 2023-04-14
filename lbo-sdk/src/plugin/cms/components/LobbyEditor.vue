<template>
  <v-row data-test-id="cnt-lobby-editor">
    <v-col cols="12">
      <v-card>
        <v-card-title> {{ this.dialogTitle }}  </v-card-title>
        <v-card-text>
          <v-text-field type="text" data-test-id="txt-lobby-name" label="Lobby Name" v-model="lobby.name"></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-btn data-test-id="btn-cancel" text @click="onCancelClick">
            {{ translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.CANCEL') }}
          </v-btn>
          <v-spacer></v-spacer>
          <v-btn data-test-id="btn-save" color="primary" @click="onSaveClick" :disabled="!this.lobby.name">
            {{translate('UI_NETWORK_ADMIN.CMS.GLOBAL.BUTTONS.SAVE')}}
          </v-btn>
        </v-card-actions>
      </v-card>

    </v-col>
  </v-row>
</template>

<script lang='ts'>
import {Component, Inject, Prop, Vue} from 'vue-property-decorator'
import Lobby from '../models/Lobby'
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";

@Component
export default class LobbyEditor extends Vue {
  @Inject('translateService') readonly translateService!: TranslateServiceInterface;

  @Prop() lobby!: Lobby;
  @Prop() dialogTitle!: string;

  onSaveClick() {
    this.$emit('onSave', this.lobby)
  }

  onCancelClick() {
    this.$emit('onCancel')
  }

  translate(text: string) {
    return this.translateService.instant(text);
  }
}
</script>
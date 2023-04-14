<template>
  <div data-test-id="cnt-lobby-selector">
    <v-data-table :loading="loading"
                  :headers="headers"
                  :items="filteredLobbies"
                  sort-by="version"
                  class="elevation-1"
                  :items-per-page="20"
                  :footer-props="{ 'items-per-page-options': [5, 10, 15, 20, 100] }"
                  @click:row="onItemClick">
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Lobbies</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn :disabled="!domain || !channel || lobbies.length === 0" class="mr-1" @click="onPublishClick">Publish</v-btn>
          <v-btn :disabled="!domain || !channel" data-test-id="btn-new-lobby" @click="onNewLobbyClick" color="primary">
            <v-icon left>mdi-plus</v-icon>
            New Lobby
          </v-btn>
        </v-toolbar>
      </template>
      <template v-slot:item.modifiedDate="{ item }">
        <span>{{ item.modifiedDate ? new Date(item.modifiedDate).toLocaleString() : ''}}</span>
      </template>
      <template v-slot:item.active="{ item }">
        <span>{{ item.active ? 'Active' : 'Inactive'}}</span>
      </template>
      <template v-slot:item.name="{ item }">
        <span>{{ buildNameOutput(item) }}</span>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-icon data-test-id="btn-set-active" small class="mr-2" @click="setActive(item)"> mdi-cloud-upload </v-icon>
        <v-icon data-test-id="btn-delete-lobby" small class="mr-2" color="red" @click="deleteLobby(item)"> mdi-delete </v-icon>
        <v-icon data-test-id="btn-edit-lobby" small class="mr-2" @click="modifyLobbyName(item)"> mdi-pencil-box-outline </v-icon>
      </template>

      <template v-slot:no-data>
        <span>Please add a new lobby to continue.</span>
      </template>
    </v-data-table>

    <v-dialog v-if="selectedLobby" persistent :title="translate('UI_NETWORK_ADMIN.CASINO.MODIFY_LOBBY.SELECTOR.TITLE')" max-width="500" v-model="showModifyLobbyDialog">
      <LobbyEditor :lobby="selectedLobby" :dialogTitle=dialogTitle @onSave="onSaveClick" @onCancel="onCancelClick"/>
    </v-dialog>

    <div class="text-center">
      <v-snackbar v-model="snackbar" :color=snackbarColour :timeout=2000 :right = true>{{snackbarTitle}}<template v-slot:action="{ attrs }">
        <v-btn color="black" text v-bind="attrs" @click="snackbar = false">
          <v-icon dark>mdi-close</v-icon>
        </v-btn>
      </template>
      </v-snackbar>
    </div>
  </div>
</template>

<script lang='ts'>
import {Component, Inject, Prop, Vue} from 'vue-property-decorator'
import Lobby from '../models/Lobby'
import LobbyEditor from '../components/LobbyEditor.vue'
import ListenerServiceInterface from "@/core/interface/service/ListenerServiceInterface";
import TranslateServiceInterface from "@/core/interface/service/TranslateServiceInterface";

@Component({
  components: {
    LobbyEditor
  }
})
export default class LobbySelector extends Vue {
  @Inject('listenerService') readonly listenerService!: ListenerServiceInterface
  @Inject('translateService') readonly translateService!: TranslateServiceInterface;

  @Prop() lobbies!: Lobby[];
  @Prop() channel!: string
  @Prop() domain!: string
  @Prop({ default: false }) loading!: boolean

  snackbar: boolean = false;
  snackbarColour: string = '';
  snackbarTitle: string = '';
  isModify: boolean = false;
  dialogTitle: string = '';

  headers = [
    { text: 'Version', value: 'version' },
    { text: 'Name', value: 'name' },
    { text: 'Last Published', value: 'modifiedDate' },
    { text: 'User', value: 'modifiedBy.fullName' },
    { text: 'Status', value: 'active' },
    { text: 'Actions', value: 'actions', sortable: false }
  ]

  showModifyLobbyDialog = false
  selectedLobby: Lobby | null = null;
  selectedLobbyCopy: Lobby | null = null;
  lobbyItemSelected = false;

  get filteredLobbies() {
    if (!this.channel) return this.lobbies;
    return this.lobbies.filter(lobby => lobby.channel === this.channel);
  }

  onNewLobbyClick() {
    this.isModify = false;
    this.dialogTitle = this.translate("UI_NETWORK_ADMIN.CASINO.LOBBY.DIALOG.CREATE");
    this.selectedLobby = new Lobby('', this.channel);
    this.showModifyLobbyDialog = true
  }

  modifyLobbyName(lobby: Lobby) {
    this.isModify = true;
    this.dialogTitle = this.translate("UI_NETWORK_ADMIN.CASINO.LOBBY.DIALOG.MODIFY");
    this.selectedLobby = lobby;
    this.selectedLobbyCopy = this.lobbyCopy(lobby);
    this.showModifyLobbyDialog = true
  }

  onItemClick(lobby: Lobby) {
    this.selectedLobby = lobby;
    this.setActive(lobby);
    this.$emit('onSelect', lobby)
  }

  setActive(lobby: Lobby) {
    this.lobbies.forEach((lobby) => (lobby.active = false))
    lobby.active = true
    this.selectedLobby = lobby;
  }

  onCancelClick() {
    this.showModifyLobbyDialog = false
    if(this.selectedLobby && this.selectedLobbyCopy) {
      this.selectedLobby.name = this.selectedLobbyCopy.name;
      this.selectedLobbyCopy = null;
    }
    this.selectedLobby = null;
  }

  onSaveClick(editedLobby: Lobby) {
    // TODO: Save lobby
    if (this.selectedLobby && editedLobby) {
      if (this.isDuplicateLobbyName(editedLobby.name)
      ) {
        this.buildSnackBarProperties('duplicate')
        return;
      }
      this.selectedLobby.name = editedLobby.name.trim();
      if (this.selectedLobby.lobbyItems) {
        this.selectedLobby.lobbyItems.forEach(lobbyItem => lobbyItem.name = editedLobby.name.trim())
      }
      if (!this.isModify) {
        this.selectedLobby.version = 1
        this.$emit('onSave', this.selectedLobby)
        this.buildSnackBarProperties('success')
      }
      this.clearText();
      this.showModifyLobbyDialog = false
    } else {

    }
  }

  private isDuplicateLobbyName(lobbyName: string) {
    return !!this.lobbies.find((lobby: Lobby) => {
      if (!lobby.name || lobby === this.selectedLobby || lobby.channel !== this.selectedLobby?.channel) {
        return false;
      }
      return lobby.name.trim().toLowerCase() === lobbyName.trim().toLowerCase();
    });
  }

  onPublishClick() {
    this.$emit("onPublish");
  }

  deleteLobby(lobby: Lobby) {
    this.listenerService.call('dialog-confirm', {
      title: this.translate("UI_NETWORK_ADMIN.CMS.GLOBAL.DIALOG.CONFIRM_DELETE.TITLE"),
      text: this.translate("UI_NETWORK_ADMIN.CMS.GLOBAL.DIALOG.CONFIRM_DELETE.TEXT") + " " + lobby.name + " " +
          this.translate("UI_NETWORK_ADMIN.CMS.GLOBAL.DIALOG.CONFIRM_DELETE.LOBBY_VERSION") + " " + lobby.version + "?",
      btnPositive: {
        text: "Delete",
        color: "error",
        onClick: () => {
          let index = this.lobbies.findIndex((lobby: Lobby) => lobby.active === this.selectedLobby?.active)
          if (this.lobbies[index] === lobby) {
            this.lobbies.splice(index, 1);
          }
        }
      }
    });
  }

  buildNameOutput(lobby: Lobby): string {
    if (!lobby.name) {
      return lobby.nav[0].primary_nav_code + '@' + lobby.channel;
    }
    return lobby.name + '@' + lobby.channel;
  }

  private clearText() {
    this.selectedLobby = null;
  }

  translate(text: string) {
    return this.translateService.instant(text);
  }

  buildSnackBarProperties(type: string){
    switch (type) {
      case "duplicate":
        this.snackbarColour = "error";
        this.snackbarTitle = this.translate("UI_NETWORK_ADMIN.CMS.LOBBY.ADD.ERROR.DUPLICATE_ENTRY");
        this.snackbar = true;
        break;
      case "success":
        this.snackbarColour = "success";
        this.snackbarTitle = this.translate("UI_NETWORK_ADMIN.CMS.LOBBY.ADD.SUCCESS");
        this.snackbar = true;
        break;
    }
  }

  lobbyCopy(lobby: Lobby): Lobby {
    return JSON.parse(JSON.stringify(lobby))
  }


}
</script>
<template>
    <div data-test-id="cnt-remove-lobby" class="mt-10">
        <!-- <v-row>Select Lobby to Remove  </v-row>       -->
        <v-form v-model="form_valid" ref="form">
               <v-row >
            <v-col cols="6">
               <v-select 
               label="Remove Lobby"
               :items="lobbyOptions" 
               :rules="rules_lobbyname_delete"
               v-model="selectedLobby" 
               @change="onChange" 
               item-text="title" 
               item-value="id" 
               return-object>Select Lobby to Configure</v-select>
            </v-col>
             <v-btn
                  elevation="2"
                  @click="onDeleteLobby"
                  class="mx-2"
                  fab
                  > 
                  <v-icon >
                     mdi-minus
                  </v-icon>
            </v-btn>
          </v-row>
        </v-form>
                  
    </div>
</template>

<script lang='ts'>

import {Vue,Component, Prop} from 'vue-property-decorator'
import Lobby from '../models/Lobby'

@Component
export default class RemoveLobby extends Vue{

   @Prop() lobbyOptions!: Lobby[];
   selectedLobby:any=null;
   rules_lobbyname_delete = [(v: string) => !!v || 'Please select lobby '];
   form_valid=false;


   onChange(lobby: Lobby) {
    this.$emit('onSelect', lobby)
  }
  onDeleteLobby(){
    (this.$refs.form as any).validate()

    if(!this.form_valid) {
      return
    }
     this.$emit('onDelete', this.selectedLobby)
  }

}
</script>
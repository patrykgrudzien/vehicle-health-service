<template>
  <v-dialog v-model="visibility"
            persistent
            max-width="450">

    <v-card class="text-xs-center">
      <v-card-title id="dialog-title"
                    class="headline">
        {{ $t(`${dialogTitle}`) }}
      </v-card-title>
      <v-card-text :class="cardTextClassesWhenTextFieldExists"
                   v-if="dialogText && dialogText !== ''">
        {{ $t(`${dialogText}`) }}
      </v-card-text>

      <v-container fluid
                   :class="containerClassesWhenTextFieldExists">
        <v-layout row
                  align-center
                  justify-center>
          <v-flex xs8>
            <v-text-field
              v-if="includeTextField && includeTextField === true"
              v-model="dialogTextFieldData"
              class="ma-0 pa-0"
              type="text"
              :hint="$t(`${hint}`)"
              :persistent-hint="true"
              required
              :rules="notEmpty"
              mask="### ### ###"/>
          </v-flex>
        </v-layout>
      </v-container>

      <v-card-actions class="pt-0" id="button-container">
        <v-btn v-if="agreeButtonText && agreeButtonText !== ''"
               color="primary"
               flat
               @click.native="agreeButtonFunction"
               :disabled="disabledIfInputEmpty">
          {{ $t(`${agreeButtonText}`) }}
        </v-btn>
        <v-btn v-if="disagreeButtonText && disagreeButtonText !== ''"
               color="primary"
               flat
               @click.native="disagreeButtonFunction">
          {{ $t(`${disagreeButtonText}`) }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
  import {getMessageFromLocale} from "../main";
  import {mapGetters}           from 'vuex';

  export default {
    props: [
      'includeTextField',
      'visibility',
      'dialogTitle',
      'dialogText',
      'agreeButtonText',
      'disagreeButtonText',
      'agreeButtonFunction',
      'disagreeButtonFunction',
      'hint'
    ],
    data() {
      return {
        notEmpty: [
          v => !!v || `${getMessageFromLocale('value-cannot-be-empty')}`
        ]
      }
    },
    computed: {
      ...mapGetters([
        'getDialogTextFieldData'
      ]),
      dialogTextFieldData: {
        get() {return this.$store.getters.getDialogTextFieldData;},
        set(value) {this.$store.commit('setDialogTextFieldData', value);}
      },
      containerClassesWhenTextFieldExists() {
        if (this.includeTextField && this.includeTextField === true) {
          return {
            'pa-0': true,
            'ml-0': true,
            'mr-0': true,
            'mb-2': true,
            'mt-0': true
          }
        } else {
          return {
            'pa-0': true,
            'ma-0': true
          }
        }
      },
      cardTextClassesWhenTextFieldExists() {
        if (this.includeTextField && this.includeTextField === true) {
          return {
            'pt-2': true,
            'pb-0': true
          }
        } else {
          return {
            'pt-1': true,
            'pb-2': true
          }
        }
      },
      disabledIfInputEmpty() {
        return !this.getDialogTextFieldData || this.getDialogTextFieldData === null || this.getDialogTextFieldData === '';
      }
    }
  }
</script>

<style scoped>
  button {
    border: none;
  }

  #dialog-title {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  #button-container {
    display: flex;
    align-items: center;
    justify-content: center;
  }
</style>

<template>
  <v-dialog v-model="visibility"
            persistent
            max-width="450">

    <v-card class="text-xs-center notSelectable">
      <v-card-title id="dialog-title"
                    class="headline notSelectable">
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
                  justify-center
                  class="notSelectable">
          <v-flex xs8 class="notSelectable">
            <v-text-field
              v-if="includeTextField && includeTextField === true"
              :value="value"
              @change="onChangeEvent"
              @input="onInputEvent"
              @keyup.enter.native="onEnterClicked"
              @keyup.esc.native="onEscClicked"
              class="ma-0 pa-0 notSelectable"
              type="text"
              :hint="$t(`${hint}`)"
              :persistent-hint="true"
              required
              mask="### ### ###"
              :autofocus="inputFieldAutofocus"
              ref="inputFieldReference"/>
          </v-flex>
        </v-layout>
      </v-container>

      <v-card-actions class="pt-0" id="button-container">
        <v-btn v-if="agreeButtonText && agreeButtonText !== ''"
               color="primary"
               flat
               @click.native="agreeButtonFunction"
               :disabled="agreeButtonDisabledCondition">
          {{ $t(`${agreeButtonText}`) }}
        </v-btn>
        <v-btn v-if="disagreeButtonText && disagreeButtonText !== ''"
               color="primary"
               flat
               @click.native="disagreeButtonFunction"
               :disabled="disagreeButtonDisabledCondition">
          {{ $t(`${disagreeButtonText}`) }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  props: {
    includeTextField: {
      default: false,
      type: Boolean
    },
    visibility: {
      default: false,
      type: Boolean
    },
    dialogTitle: {
      default: '',
      type: String
    },
    dialogText: {
      default: '',
      type: String
    },
    agreeButtonText: {
      default: '',
      type: String
    },
    disagreeButtonText: {
      default: '',
      type: String
    },
    agreeButtonFunction: {
      type: Function
    },
    agreeButtonDisabledCondition: {
      default: false,
      type: Boolean
    },
    disagreeButtonFunction: {
      type: Function
    },
    disagreeButtonDisabledCondition: {
      default: false,
      type: Boolean
    },
    hint: {
      default: '',
      type: String
    },
    value: {},
    inputFieldAutofocus: {
      default: false,
      type: Boolean
    }
  },
  methods: {
    onChangeEvent (payload) {
      this.$emit('onChangeEvent', payload)
    },
    onInputEvent (payload) {
      this.$emit('onInputEvent', payload)
    },
    onEnterClicked (payload) {
      if (payload.target.value !== '') {
        this.$emit('onEnterClicked', payload)
      }
    },
    onEscClicked (payload) {
      this.$emit('onEscClicked', payload)
    }
  },
  computed: {
    containerClassesWhenTextFieldExists () {
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
    cardTextClassesWhenTextFieldExists () {
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
    }
  },
  watch: {
    inputFieldAutofocus (value) {
      if (value) {
        this.$nextTick(this.$refs.inputFieldReference.focus)
      }
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

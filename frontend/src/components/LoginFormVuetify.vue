<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex xs12 sm8 md6>
        <!-- ERROR ALERT -->
        <my-alert @dismissed="clearServerError"
                  type="error"
                  v-if="getServerError"
                  :errorMessage="getServerError"/>
        <!-- ERROR ALERT -->

        <!-- SUCCESS ALERT -->
        <my-alert @dismissed="clearServerResponse"
                  type="success"
                  v-if="getServerResponse"
                  :errorMessage="getServerResponse"/>
        <!-- SUCCESS ALERT -->

        <!-- FORM -->
        <v-card class="elevation-12">
          <v-card-text>
            <v-form v-model="valid" lazy-validation v-if="show" ref="myLoginForm">

              <!-- EMAIL -->
              <v-text-field
                prepend-icon="email"
                name="email"
                :label="$t('email-label')"
                type="email"
                v-model="email"
                :hint="$t('email-input-hint')"
                required
                :rules="emailRules"
                :counter="50"/>
              <!-- EMAIL -->

              <!-- PASSWORD -->
              <v-text-field
                prepend-icon="lock"
                name="password"
                :label="$t('password-label')"
                id="password"
                type="password"
                v-model="password"
                :hint="$t('password-input-hint')"
                required
                :rules="passwordRules"
                :counter="50"
                :append-icon="hidePasswords ? 'visibility' : 'visibility_off'"
                :append-icon-cb="() => (hidePasswords = !hidePasswords)"
                :type="hidePasswords ? 'password' : 'text'"/>
              <!-- PASSWORD -->

            </v-form>
            <!-- FORM -->

          </v-card-text>
          <v-card-actions class="pl-3">
            <v-btn color="primary" @click="submit" :disabled="loginButtonDisabled">{{ $t('login-button') }}</v-btn>
            <v-btn color="error" @click="clearFormFields" :disabled="clearButtonDisabled" left>
              {{ $t('clear-button') }}
            </v-btn>
            <v-spacer/>
          </v-card-actions>
          <v-card-text class="pl-3 ml-1">
            {{ $t('new-user') }}
            <router-link to="/registration-form" exact>{{ $t('register-here') }}</router-link>
          </v-card-text>
        </v-card>
        <!-- FORM -->

        <!-- DIALOG WINDOW -->
        <v-dialog v-model="dialogWindowActive" persistent max-width="450">
          <v-card class="text-xs-center">
            <v-card-title id="dialog-title" class="headline">{{ $t('dialog-title') }}</v-card-title>
            <v-card-text class="pt-0 pb-0">{{ $t('dialog-text') }}</v-card-text>
            <v-card-actions id="button-container">
              <v-btn color="primary" flat @click.native="changeLanguageAndHideDialog">{{ $t('agree-button') }}</v-btn>
              <v-btn color="primary" flat @click.native="hideDialogWindow">{{ $t('disagree-button') }}</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <!-- DIALOG WINDOW -->

      </v-flex>

    </v-layout>
  </v-container>
</template>

<script>
  import CircleSpinner          from 'vue-loading-spinner/src/components/Circle8';
  import {getMessageFromLocale} from "../main";
  import {mapGetters}           from 'vuex';
  import {mapActions}           from 'vuex';

  export default {
    components: {
      'circle-spinner': CircleSpinner
    },
    data() {
      return {
        tempLanguage: null,
        dialogWindowActive: false,
        hidePasswords: true,
        passwordRules: [
          v => !!v || `${getMessageFromLocale('password-required')}`,
          v => (v && v.length >= 4) || `${getMessageFromLocale('min-chars-length')}`,
          v => (v && v.length <= 50) || `${getMessageFromLocale('max-chars-length')}`,
        ],
        emailRules: [
          v => !!v || `${getMessageFromLocale('email-required')}`,
          v => (v && v.length >= 4) || `${getMessageFromLocale('min-chars-length')}`,
          v => (v && v.length <= 50) || `${getMessageFromLocale('max-chars-length')}`,
          v => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || `${getMessageFromLocale('email-must-be-valid')}`
        ]
      }
    },
    methods: {
      ...mapActions([
        'clearServerError',
        'clearServerResponse',
        ''
      ]),
      submit() {
        if (this.$refs.myLoginForm.validate()) {
          this.$store.dispatch('login', this.getLoginForm);
        } else {
          this.$store.commit('setServerError', 'Form filled incorrectly!');
        }
      },
      clearFormFields() {
        this.$store.commit('setLoginFormEmail', '');
        this.$store.commit('setLoginFormPassword', '');
        this.$store.commit('setLoginFormValid', true);
        this.$store.commit('setLoginFormShow', false);
        this.$nextTick(() => {
          this.$store.commit('setLoginFormShow', true);
        });
      },
      hideDialogWindow() {
        this.dialogWindowActive = false;
      },
      changeLanguageAndHideDialog() {
        this.dialogWindowActive = false;
        setTimeout(() => {
          this.$store.dispatch('setLang', this.tempLanguage)
            .then(() => {
              this.clearFormFields();
              this.$store.commit('setSideNavigation', false);
              this.$store.commit('clearServerError');
              this.$store.commit('clearServerResponse');
            });
        }, 250);
      }
    },
    created() {
      this.$router.app.$on('open-dialog-and-send-lang', (payload) => {
        this.dialogWindowActive = payload.showDialog;
        this.tempLanguage = payload.lang;
      })
    },
    computed: {
      ...mapGetters([
        'getLoginForm',
        'getServerError',
        'getServerResponse'
      ]),
      email: {
        get() {return this.$store.getters.getLoginForm.email;},
        set(value) {this.$store.commit('setLoginFormEmail', value);}
      },
      password: {
        get() {return this.$store.getters.getLoginForm.password;},
        set(value) {this.$store.commit('setLoginFormPassword', value);}
      },
      valid: {
        get() {return this.$store.getters.getLoginForm.valid;},
        set(value) {this.$store.commit('setLoginFormValid', value);}
      },
      show: {
        get() {return this.$store.getters.getLoginForm.show;},
        set(value) {this.$store.commit('setLoginFormShow', value);}
      },
      loginButtonDisabled() {
        return this.email === '' || this.password === '' || this.valid === false;
      },
      clearButtonDisabled() {
        return this.email === '' && this.password === '' && this.valid === true;
      },
    }
  };
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

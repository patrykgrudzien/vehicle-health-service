<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex xs12 sm8 md6>
        <!-- CONFIRMATION REGISTRATION ALERT -->
        <my-alert :dismissible="true"
                  @dismissed="dismissDialog()"
                  type="success"
                  v-if="showDialog"
                  :message="confirmationMessage()"/>
        <!-- CONFIRMATION REGISTRATION ALERT -->

        <!-- SERVER NOT RUNNING -->
        <my-alert :dismissible="true"
                  @dismissed="setServerRunning"
                  type="error"
                  v-if="!isServerRunning"
                  :message="$t('server-status-message')"/>
        <!-- SERVER NOT RUNNING -->

        <!-- ERROR ALERT -->
        <my-alert :dismissible="true"
                  @dismissed="clearServerExceptionResponse"
                  type="error"
                  v-if="getServerExceptionResponse"
                  :message="getServerExceptionResponseMessage"
                  :errors="getServerExceptionResponseErrors"/>
        <!-- ERROR ALERT -->

        <!-- SUCCESS ALERT -->
        <my-alert :dismissible="true"
                  @dismissed="clearServerSuccessResponse"
                  type="success"
                  v-if="getServerSuccessResponse"
                  :message="getServerSuccessResponseMessage"/>
        <!-- SUCCESS ALERT -->

        <!-- LOGOUT SUCCESSFUL ALERT -->
        <my-alert :dismissible="false"
                  type="success"
                  v-if="urlContainsLogoutSuccessfulTrue"
                  :message="$t('logout-successful-message')"/>
        <!-- LOGOUT SUCCESSFUL ALERT -->

        <!-- FORM -->
        <v-card class="elevation-12">
          <v-card-text>
            <v-form v-model="valid"
                    ref="myLoginForm"
                    @keyup.native.enter="valid && submit($event)">
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
                :counter="50" />
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
                :append-icon="hidePasswords ? 'visibility_off' : 'visibility'"
                :append-icon-cb="() => (hidePasswords = !hidePasswords)"
                :type="hidePasswords ? 'password' : 'text'"/>
              <!-- PASSWORD -->

            </v-form>
            <!-- FORM -->

          </v-card-text>
          <v-card-actions class="pl-3">
            <v-btn color="primary" @click="submit" :disabled="loginButtonDisabled" :loading="isLoading">
              {{ $t('login-button') }}
            </v-btn>
            <v-btn color="error" @click="clearFormFields" :disabled="clearButtonDisabled" left>
              {{ $t('clear-button') }}
            </v-btn>
            <v-spacer/>
          </v-card-actions>
          <v-card-text class="pl-3 ml-1 notSelectable">
            {{ $t('new-user') }}
            <router-link to="/registration-form" exact>{{ $t('register-here') }}</router-link>
          </v-card-text>
        </v-card>
        <!-- FORM -->

        <!-- MY DIALOG WINDOW -->
        <my-dialog :visibility="tempDialogWindowActive"
                   dialog-title="change-language-dialog-title"
                   dialog-text="change-language-dialog-text"
                   agree-button-text="agree-button"
                   disagree-button-text="disagree-button"
                   :agree-button-function="changeLanguageAndHideDialog"
                   :disagree-button-function="hideDialogWindow"/>
        <!-- MY DIALOG WINDOW -->

      </v-flex>

    </v-layout>
  </v-container>
</template>

<script>
  import {getMessageFromLocale} from "../main";
  import {mapGetters}           from 'vuex';
  import {mapActions}           from 'vuex';
  import componentsPaths from '../componentsPaths';

  export default {
    props: ['confirmationMessage', 'dismissDialog', 'showDialog', 'type'],
    data() {
      return {
        tempLanguage: null,
        tempDialogWindowActive: false,
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
        'clearServerExceptionResponse',
        'clearServerSuccessResponse'
      ]),
      submit() {
        if (this.$refs.myLoginForm.validate()) {
          this.$store.dispatch('login', this.getLoginForm);
        } else {
          this.$store.commit('setServerExceptionResponse', 'Form filled incorrectly!');
        }
      },
      clearFormFields() {
        this.$refs.myLoginForm.reset();
        this.$store.commit('setLoginFormValid', true);
        // without that code below it does not work
        this.$nextTick(() => {
          this.$store.commit('setLoginFormValid', true);
        });
      },
      hideDialogWindow() {
        this.tempDialogWindowActive = false;
      },
      changeLanguageAndHideDialog() {
        this.tempDialogWindowActive = false;
        setTimeout(() => {
          this.$store.dispatch('setLang', this.tempLanguage)
            .then(() => {
              this.clearFormFields();
              this.$store.commit('setServerRunning', true);
              this.$store.commit('setSideNavigation', false);
              this.$store.commit('clearServerExceptionResponse');
              this.$store.commit('clearServerSuccessResponse');
            });
        }, 200);
      },
      setServerRunning() {
        this.$store.commit('setServerRunning', true);
      }
    },
    created() {
      this.$router.app.$on('open-dialog-and-send-lang', (payload) => {
        this.tempDialogWindowActive = payload.showDialog;
        this.tempLanguage = payload.lang;
      })
    },
    computed: {
      ...mapGetters([
        'getLoginForm',
        'isServerRunning',
        'getServerExceptionResponse',
        'getServerSuccessResponse',
        'isLoading'
      ]),
      getServerExceptionResponseMessage() {
        return this.getServerExceptionResponse.message;
      },
      getServerExceptionResponseErrors() {
        return this.getServerExceptionResponse.errors;
      },
      getServerSuccessResponseMessage() {
        return this.getServerSuccessResponse.message;
      },
      urlContainsLogoutSuccessfulTrue() {
        return this.$route.fullPath.includes(componentsPaths.logoutSuccessful);
      },
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
      loginButtonDisabled() {
        return this.email === '' || !this.email || this.password === '' || !this.password
          || this.valid === false || this.isLoading === true;
      },
      clearButtonDisabled() {
        return (this.email === '' || !this.email) && (this.password === '' || !this.password) &&
          (this.valid === true || !this.valid) && (this.isLoading === true || !this.isLoading);
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

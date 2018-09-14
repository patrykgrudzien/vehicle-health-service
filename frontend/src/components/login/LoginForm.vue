<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex xs12 sm8 md6>
        <!-- USER ALREADY ENABLED ALERT -->
        <my-alert id="user-already-enabled-alert"
                  dismissible
                  @dismissed="dismissDialog()"
                  type="info"
                  v-if="showDialog && this.$route.fullPath.includes('userAlreadyEnabled')"
                  :message="confirmationMessage()"/>
        <!-- USER ALREADY ENABLED ALERT -->

        <!-- CONFIRMATION REGISTRATION ALERT -->
        <my-alert id="confirmation-registration-alert"
                  dismissible
                  @dismissed="dismissDialog()"
                  type="success"
                  v-if="showDialog && !this.$route.fullPath.includes('userAlreadyEnabled')"
                  :message="confirmationMessage()"/>
        <!-- CONFIRMATION REGISTRATION ALERT -->

        <!-- SERVER NOT RUNNING -->
        <my-alert id="server-not-running-alert"
                  dismissible
                  @dismissed="setServerRunning"
                  type="error"
                  v-if="!isServerRunning"
                  :message="$t('server-status-message')"/>
        <!-- SERVER NOT RUNNING -->

        <!-- ERROR ALERT (only message from server) -->
        <my-alert id="message-from-server-alert"
                  dismissible
                  @dismissed="clearServerExceptionResponse"
                  type="error"
                  v-if="getServerExceptionResponse && !getServerExceptionResponseErrors"
                  :message="getServerExceptionResponseMessage"
                  :errors="getServerExceptionResponseErrors"/>
        <!-- ERROR ALERT (only message from server) -->

        <!-- ERROR ALERT -->
        <my-alert id="validation-errors-alert"
                  dismissible
                  @dismissed="clearServerExceptionResponse"
                  type="error"
                  v-if="getServerExceptionResponse && getServerExceptionResponseErrors"
                  :message="getServerExceptionResponseMessage"
                  :errors="getServerExceptionResponseErrors"/>
        <!-- ERROR ALERT -->

        <!-- SUCCESS ALERT -->
        <my-alert id="success-alert"
                  dismissible
                  @dismissed="clearServerSuccessResponse"
                  type="success"
                  v-if="getServerSuccessResponse"
                  :message="getServerSuccessResponseMessage"/>
        <!-- SUCCESS ALERT -->

        <!-- LOGOUT SUCCESSFUL ALERT -->
        <my-alert id="logout-successful-alert"
                  :dismissible="false"
                  type="success"
                  v-if="urlContainsLogoutSuccessfulTrue"
                  :message="$t('logout-successful-message')"/>
        <!-- LOGOUT SUCCESSFUL ALERT -->

        <!-- AUTHENTICATION REQUIRED ALERT (SECURED RESOURCE -> "/main-board" without token") -->
        <my-alert id="authentication-required-alert"
                  :dismissible="false"
                  type="error"
                  v-if="urlContainsAuthenticationRequired"
                  :message="$t('authentication-required-message')"/>
        <!-- AUTHENTICATION REQUIRED ALERT (SECURED RESOURCE -> "/main-board" without token") -->

        <!-- FORM FILLED INCORRECTLY ALERT -->
        <my-alert id="form-filled-incorrectly-alert"
                  dismissible
                  @dismissed="clearServerExceptionResponse"
                  type="error"
                  v-if="formFilledIncorrectlyMessage !== null && formFilledIncorrectlyMessage !== '' &&
                        getServerSuccessResponse !== null"
                  :message="formFilledIncorrectlyMessage"/>
        <!-- FORM FILLED INCORRECTLY ALERT -->

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
                :counter="50"
                class="notSelectable"/>
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
                class="notSelectable"
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
            <!-- Google -->
            <v-btn icon
                   large
                   ripple
                   @click="googleButtonClicked">
              <v-icon>fab fa-google</v-icon>
            </v-btn>
            <!-- Google -->

            <!-- Facebook -->
            <v-btn icon
                   large
                   ripple
                   @click="facebookButtonClicked">
              <v-icon>fab fa-facebook-f</v-icon>
            </v-btn>
            <!-- Facebook -->
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

        <!-- LOADING DIALOG WINDOW -->
        <loading-dialog :visibility="isLoading" />
        <!-- LOADING DIALOG WINDOW -->

      </v-flex>

    </v-layout>
  </v-container>
</template>

<script>
  import {getMessageFromLocale}       from "../../main";
  import {mapGetters, mapMutations}   from 'vuex';
  import componentsDetails            from '../../componentsDetails';
  import {MUTATIONS, ACTIONS, EVENTS} from '../../Constants';

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
      ...mapMutations({
        clearServerExceptionResponse: MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE,
        clearServerSuccessResponse: MUTATIONS.CLEAR_SERVER_SUCCESS_RESPONSE
      }),
      submit() {
        if (this.$refs.myLoginForm.validate()) {
          this.$store.dispatch(ACTIONS.LOGIN, this.getLoginForm);
        } else {
          this.$store.commit(MUTATIONS.SET_SERVER_EXCEPTION_RESPONSE, 'Form filled incorrectly!');
          window.scrollTo(0, 0);
        }
      },
      clearFormFields() {
        this.$refs.myLoginForm.reset();
        this.$store.commit(MUTATIONS.SET_LOGIN_FORM_VALID, true);
        // without that code below it does not work
        this.$nextTick(() => {
          this.$store.commit(MUTATIONS.SET_LOGIN_FORM_VALID, true);
        });
      },
      hideDialogWindow() {
        this.tempDialogWindowActive = false;
      },
      changeLanguageAndHideDialog() {
        this.tempDialogWindowActive = false;
        setTimeout(() => {
          this.$store.dispatch(ACTIONS.SET_LANG, this.tempLanguage)
            .then(() => {
              this.clearFormFields();
              this.$store.commit(MUTATIONS.SET_SERVER_RUNNING, true);
              this.$store.commit(MUTATIONS.SET_SIDE_NAVIGATION, false);
              this.$store.commit('clearServerExceptionResponse');
              this.$store.commit('clearServerSuccessResponse');
            });
        }, 200);
      },
      setServerRunning() {
        this.$store.commit(MUTATIONS.SET_SERVER_RUNNING, true);
      },
      googleButtonClicked() {
        console.log('googleButtonClicked');
        window.location.href = 'http://localhost:8088/oauth2/authorization/google';
      },
      facebookButtonClicked() {
        console.log('facebookButtonClicked');
      }
    },
    created() {
      this.$router.app.$on(EVENTS.OPEN_DIALOG_AND_SEND_LANG_EVENT, (payload) => {
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
      formFilledIncorrectlyMessage() {
        return this.getServerExceptionResponse;
      },
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
        return this.$route.fullPath.includes(componentsDetails.logoutSuccessful.path.path);
      },
      urlContainsAuthenticationRequired() {
        return this.$route.fullPath.includes(componentsDetails.authenticationRequired.path);
      },
      email: {
        get() {return this.$store.getters.getLoginForm.email;},
        set(value) {this.$store.commit(MUTATIONS.SET_LOGIN_FORM_EMAIL, value);}
      },
      password: {
        get() {return this.$store.getters.getLoginForm.password;},
        set(value) {this.$store.commit(MUTATIONS.SET_LOGIN_FORM_PASSWORD, value);}
      },
      valid: {
        get() {return this.$store.getters.getLoginForm.valid;},
        set(value) {this.$store.commit(MUTATIONS.SET_LOGIN_FORM_VALID, value);}
      },
      loginButtonDisabled() {
        return this.email === '' || !this.email || this.password === '' || !this.password
          || this.valid === false || this.isLoading === true;
      },
      clearButtonDisabled() {
        return (
                  (this.email === '' || !this.email) && (this.password === '' || !this.password) &&
                  (this.valid === true || !this.valid) && (this.isLoading === true || !this.isLoading)
               )
                  ||
               (
                  (this.email === '' || this.email) && (this.password === '' || this.password) &&
                  (this.isLoading === true || this.isLoading)
               );
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

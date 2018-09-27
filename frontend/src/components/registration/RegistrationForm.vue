<template>
  <v-layout row align-center justify-center class="pa-3">
    <v-flex xs12 sm8 md8>
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

      <!-- FORM FILLED INCORRECTLY ALERT -->
      <my-alert id="form-filled-incorrectly-alert"
                dismissible
                @dismissed="clearServerExceptionResponse"
                type="error"
                v-if="formFilledIncorrectlyMessage !== null && formFilledIncorrectlyMessage !== '' &&
                        getServerSuccessResponse !== null"
                :message="formFilledIncorrectlyMessage"/>
      <!-- FORM FILLED INCORRECTLY ALERT -->

      <v-layout v-bind="rowColumnDeterminer">
        <!-- Google Button -->
        <v-flex xs6
                :class="googleFlexClasses"
                @click="googleButtonClicked">
          <v-btn flat
                 large
                 ripple
                 disabled
                 class="white--text"
                 style="color: white !important;">
            <v-icon left
                    style="color: white !important;">fab fa-google</v-icon>
            Register with Google
          </v-btn>
        </v-flex><!-- Google Button -->

        <!-- Facebook Button -->
        <v-flex xs6
                :class="facebookFlexClasses"
                @click="facebookButtonClicked">
          <v-btn flat
                 large
                 ripple
                 disabled
                 class="white--text"
                 style="color: white !important;">
            <v-icon left
                    style="color: white !important;">fab fa-facebook</v-icon>
            Register with Facebook
          </v-btn>
        </v-flex><!-- Facebook Button -->
      </v-layout>

      <!-- FORM -->
      <v-card class="elevation-12">
        <v-card-text>
          <v-form v-model="valid"
                  ref="myRegistrationForm"
                  @keyup.native.enter="valid && submit($event)">

            <v-container grid-list-xs>
              <v-layout v-bind="rowColumnDeterminer">
                <!-- YOUR NAME -->
                <v-flex xs6 :class="{'mr-2': this.$vuetify.breakpoint.lgAndUp}">
                  <v-text-field
                    prepend-icon="person"
                    name="yourName"
                    :label="$t('first-name-label')"
                    type="text"
                    v-model="firstName"
                    :hint="$t('first-name-input-hint')"
                    required
                    :rules="firstNameRules"
                    :counter="50"
                    class="notSelectable"/>
                </v-flex>
                <!-- YOUR NAME -->

                <!-- YOUR LAST NAME -->
                <v-flex xs6 :class="{'ml-2': this.$vuetify.breakpoint.lgAndUp}">
                  <v-text-field
                    prepend-icon="person"
                    name="yourLastName"
                    :label="$t('last-name-label')"
                    type="text"
                    v-model="lastName"
                    :hint="$t('last-name-input-hint')"
                    required
                    :rules="lastNameRules"
                    :counter="50"
                    class="notSelectable"/>
                </v-flex>
                <!-- YOUR LAST NAME -->
              </v-layout>
            </v-container>

            <v-container grid-list-xs>
              <v-layout v-bind="rowColumnDeterminer">
                <!-- YOUR EMAIL -->
                <v-flex xs6 :class="{'mr-2': this.$vuetify.breakpoint.lgAndUp}">
                  <v-text-field
                    prepend-icon="email"
                    name="yourEmail"
                    :label="$t('email-label')"
                    type="email"
                    v-model="email"
                    :hint="emailsMatcherErrorMessage"
                    :persistent-hint="emailsDoNotMatch"
                    required
                    :rules="emailRules"
                    :error="emailsDoNotMatch"
                    :counter="50"
                    class="notSelectable"/>
                </v-flex>
                <!-- YOUR EMAIL -->

                <!-- CONFIRM EMAIL -->
                <v-flex xs6 :class="{'ml-2': this.$vuetify.breakpoint.lgAndUp}">
                  <v-text-field
                    prepend-icon="email"
                    name="confirmEmail"
                    :label="$t('confirm-email-label')"
                    type="email"
                    v-model="confirmedEmail"
                    :hint="emailsMatcherErrorMessage"
                    :persistent-hint="emailsDoNotMatch"
                    required
                    :rules="confirmEmailRules"
                    :error="emailsDoNotMatch"
                    :counter="50"
                    class="notSelectable"/>
                </v-flex>
                <!-- CONFIRM EMAIL -->
              </v-layout>
            </v-container>

            <v-container grid-list-xs>
              <v-layout v-bind="rowColumnDeterminer">
                <!-- PASSWORD -->
                <v-flex xs6 :class="{'mr-2': this.$vuetify.breakpoint.lgAndUp}">
                  <v-text-field
                    prepend-icon="lock"
                    name="password"
                    :label="$t('password-label')"
                    id="password"
                    type="password"
                    v-model="password"
                    :hint="passwordsMatcherErrorMessage"
                    :persistent-hint="passwordsDoNotMatch"
                    required
                    :rules="passwordRules"
                    :error="passwordsDoNotMatch"
                    :counter="50"
                    class="notSelectable"
                    :append-icon="hidePasswords ? 'visibility_off' : 'visibility'"
                    :append-icon-cb="() => (hidePasswords = !hidePasswords)"
                    :type="hidePasswords ? 'password' : 'text'"/>
                </v-flex>
                <!-- PASSWORD -->

                <!-- CONFIRM PASSWORD -->
                <v-flex xs6 :class="{'ml-2': this.$vuetify.breakpoint.lgAndUp}">
                  <v-text-field
                    prepend-icon="lock"
                    name="confirmPassword"
                    :label="$t('confirm-password-label')"
                    id="confirmPassword"
                    type="password"
                    v-model="confirmedPassword"
                    :hint="passwordsMatcherErrorMessage"
                    :persistent-hint="passwordsDoNotMatch"
                    required
                    :rules="confirmPasswordRules"
                    :error="passwordsDoNotMatch"
                    :counter="50"
                    class="notSelectable"
                    :append-icon="hidePasswords ? 'visibility_off' : 'visibility'"
                    :append-icon-cb="() => (hidePasswords = !hidePasswords)"
                    :type="hidePasswords ? 'password' : 'text'"/>
                </v-flex>
                <!-- CONFIRM PASSWORD -->
              </v-layout>
            </v-container>
          </v-form>
        </v-card-text>
        <v-card-actions class="pl-3">
          <v-btn color="primary" @click="submit" :disabled="registerButtonDisabled" :loading="isLoading">
            {{ $t('register-button') }}
          </v-btn>
          <v-btn color="error" @click="clearFormFields" :disabled="clearButtonDisabled" left>
            {{ $t('clear-button') }}
          </v-btn>
          <v-spacer/>
        </v-card-actions>
        <v-card-text class="pl-3 ml-1 notSelectable">
          {{ $t('already-registered') }}
          <router-link to="/login" exact>{{ $t('login-here') }}</router-link>
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
      <loading-dialog :visibility="isLoading"/>
      <!-- LOADING DIALOG WINDOW -->

    </v-flex>
  </v-layout>
</template>

<script>
  import {getMessageFromLocale}       from '../../main';
  import {mapGetters, mapMutations}   from 'vuex';
  import {MUTATIONS, ACTIONS, EVENTS} from '../../Constants';

  export default {
    data() {
      return {
        tempLanguage: null,
        tempDialogWindowActive: false,
        hidePasswords: true,
        firstNameRules: [
          v => !!v || `${getMessageFromLocale('first-name-required')}`,
          v => (v && v.length >= 4) || `${getMessageFromLocale('min-chars-length')}`,
          v => (v && v.length <= 50) || `${getMessageFromLocale('max-chars-length')}`
        ],
        lastNameRules: [
          v => !!v || `${getMessageFromLocale('last-name-required')}`,
          v => (v && v.length >= 4) || `${getMessageFromLocale('min-chars-length')}`,
          v => (v && v.length <= 50) || `${getMessageFromLocale('max-chars-length')}`
        ],
        emailRules: [
          v => !!v || `${getMessageFromLocale('email-required')}`,
          v => (v && v.length >= 4) || `${getMessageFromLocale('min-chars-length')}`,
          v => (v && v.length <= 50) || `${getMessageFromLocale('max-chars-length')}`,
          v => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || `${getMessageFromLocale('email-must-be-valid')}`
        ],
        confirmEmailRules: [
          v => !!v || `${getMessageFromLocale('confirm-email-required')}`,
          v => (v && v.length >= 4) || `${getMessageFromLocale('min-chars-length')}`,
          v => (v && v.length <= 50) || `${getMessageFromLocale('max-chars-length')}`,
          v => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || `${getMessageFromLocale('email-must-be-valid')}`
        ],
        passwordRules: [
          v => !!v || `${getMessageFromLocale('password-required')}`,
          v => (v && v.length >= 4) || `${getMessageFromLocale('min-chars-length')}`,
          v => (v && v.length <= 50) || `${getMessageFromLocale('max-chars-length')}`,
        ],
        confirmPasswordRules: [
          v => !!v || `${getMessageFromLocale('confirm-password-required')}`,
          v => (v && v.length >= 4) || `${getMessageFromLocale('min-chars-length')}`,
          v => (v && v.length <= 50) || `${getMessageFromLocale('max-chars-length')}`
        ]
      }
    },
    methods: {
      ...mapMutations({
        clearServerExceptionResponse: MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE,
        clearServerSuccessResponse: MUTATIONS.CLEAR_SERVER_SUCCESS_RESPONSE
      }),
      submit() {
        if (this.$refs.myRegistrationForm.validate()) {
          this.$store.dispatch(ACTIONS.REGISTER_USER_ACCOUNT, this.getRegistrationForm);
        } else {
          this.$store.commit(MUTATIONS.SET_SERVER_EXCEPTION_RESPONSE, 'Form filled incorrectly!');
          window.scrollTo(0, 0);
        }
      },
      clearFormFields() {
        this.$refs.myRegistrationForm.reset();
        this.$store.commit(MUTATIONS.SET_REGISTRATION_FORM_VALID, true);
        // without that code below it does not work
        this.$nextTick(() => {
          this.$store.commit(MUTATIONS.SET_REGISTRATION_FORM_VALID, true);
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
        this.$store.commit(MUTATIONS.SET_LOADING, true);
        window.location.href = 'http://localhost:8088/oauth2/authorization/google';
      },
      facebookButtonClicked() {
        console.log('facebookButtonClicked');
        window.location.href = 'http://localhost:8088/oauth2/authorization/facebook';
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
        'getRegistrationForm',
        'isLoading',
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
      rowColumnDeterminer() {
        const binding = {};
        if (this.$vuetify.breakpoint.mdAndDown) {
          binding.column = true;
        } else {
          binding.row = true;
        }
        return binding;
      },
      firstName: {
        get() {return this.$store.getters.getRegistrationForm.firstName;},
        set(value) {this.$store.commit(MUTATIONS.SET_REGISTRATION_FORM_FIRST_NAME, value);}
      },
      lastName: {
        get() {return this.$store.getters.getRegistrationForm.lastName;},
        set(value) {this.$store.commit(MUTATIONS.SET_REGISTRATION_FORM_LAST_NAME, value);}
      },
      email: {
        get() {return this.$store.getters.getRegistrationForm.email;},
        set(value) {this.$store.commit(MUTATIONS.SET_REGISTRATION_FORM_EMAIL, value);}
      },
      confirmedEmail: {
        get() {return this.$store.getters.getRegistrationForm.confirmedEmail;},
        set(value) {this.$store.commit(MUTATIONS.SET_REGISTRATION_FORM_CONFIRMED_EMAIL, value);}
      },
      password: {
        get() {return this.$store.getters.getRegistrationForm.password;},
        set(value) {this.$store.commit(MUTATIONS.SET_REGISTRATION_FORM_PASSWORD, value);}
      },
      confirmedPassword: {
        get() {return this.$store.getters.getRegistrationForm.confirmedPassword;},
        set(value) {this.$store.commit(MUTATIONS.SET_REGISTRATION_FORM_CONFIRMED_PASSWORD, value);}
      },
      valid: {
        get() {return this.$store.getters.getRegistrationForm.valid;},
        set(value) {this.$store.commit(MUTATIONS.SET_REGISTRATION_FORM_VALID, value);}
      },
      registerButtonDisabled() {
        return this.firstName === '' || !this.firstName || this.lastName === '' || !this.lastName ||
          this.email === '' || !this.email || this.confirmedEmail === '' || !this.confirmedEmail ||
          this.password === '' || !this.password || this.confirmedPassword === '' || !this.confirmedPassword ||
          this.valid === false || this.emailsDoNotMatch === true || this.passwordsDoNotMatch === true ||
          this.isLoading === true;
      },
      clearButtonDisabled() {
        return (
                  (this.firstName === '' || !this.firstName) && (this.lastName === '' || !this.lastName) &&
                  (this.email === '' || !this.email) && (this.confirmedEmail === '' || !this.confirmedEmail) &&
                  (this.password === '' || !this.password) && (this.confirmedPassword === '' || !this.confirmedPassword) &&
                  (this.valid === true || !this.valid) && (this.isLoading === true || !this.isLoading)
               )
                  ||
               (
                  (this.firstName === '' || this.firstName) && (this.lastName === '' || this.lastName) &&
                  (this.email === '' || this.email) && (this.confirmedEmail === '' || this.confirmedEmail) &&
                  (this.password === '' || this.password) && (this.confirmedPassword === '' || this.confirmedPassword) &&
                  (this.isLoading === true || this.isLoading)
               );
      },
      emailsDoNotMatch() {
        if (this.email !== '' && this.confirmedEmail !== '' && this.email !== this.confirmedEmail) {
          return true;
        }
      },
      passwordsDoNotMatch() {
        if (this.password !== '' && this.confirmedPassword !== '' && this.password !== this.confirmedPassword) {
          return true;
        }
      },
      emailsMatcherErrorMessage() {
        if (this.email !== '' && this.confirmedEmail !== '' && this.email !== this.confirmedEmail) {
          return `${getMessageFromLocale('emails-do-not-match')}`;
        } else {
          return `${getMessageFromLocale('email-input-hint')}`;
        }
      },
      passwordsMatcherErrorMessage() {
        if (this.password !== '' && this.confirmedPassword !== '' && this.password !== this.confirmedPassword) {
          return `${getMessageFromLocale('passwords-do-not-match')}`;
        } else {
          return `${getMessageFromLocale('password-input-hint')}`;
        }
      },
      formFilledIncorrectlyMessage() {
        return this.getServerExceptionResponse;
      },
      googleFlexClasses() {
        if (this.$vuetify.breakpoint.mdAndUp) {
          return {
            'text-xs-center': true,
            'googleButtonColor': true,
            'mb-2': true,
            'mr-1': true,
            'elevation-12': true,
            'google-button-style': true
          }
        } else {
          return {
            'text-xs-center': true,
            'googleButtonColor': true,
            'mb-2': true,
            'elevation-12': true,
            'google-button-style': true
          }
        }
      },
      facebookFlexClasses() {
        if (this.$vuetify.breakpoint.mdAndUp) {
          return {
            'text-xs-center': true,
            'facebookButtonColor': true,
            'mb-2': true,
            'ml-1': true,
            'elevation-12': true,
            'facebook-button-style': true
          }
        } else {
          return {
            'text-xs-center': true,
            'facebookButtonColor': true,
            'mb-2': true,
            'elevation-12': true,
            'facebook-button-style': true
          }
        }
      }
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

  .facebook-button-style, .google-button-style {
    cursor: pointer;
  }

  .facebook-button-style:hover {
    background-color: #1565C0 !important;
  }

  .google-button-style:hover {
    background-color: #D32F2F !important;
  }
</style>

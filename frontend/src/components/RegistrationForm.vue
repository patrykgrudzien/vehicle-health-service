<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex xs12 sm8 md8>
        <!-- SERVER NOT RUNNING -->
        <my-alert @dismissed="setServerRunning"
                  type="error"
                  v-if="!isServerRunning"
                  :message="$t('server-status-message')"/>
        <!-- SERVER NOT RUNNING -->

        <!-- ERROR ALERT -->
        <my-alert @dismissed="clearServerExceptionResponse"
                  type="error"
                  v-if="getServerExceptionResponse"
                  :message="getServerExceptionResponseMessage"
                  :errors="getServerExceptionResponseErrors"/>
        <!-- ERROR ALERT -->

        <!-- SUCCESS ALERT -->
        <my-alert @dismissed="clearServerSuccessResponse"
                  type="success"
                  v-if="getServerSuccessResponse"
                  :message="getServerSuccessResponseMessage"/>
        <!-- SUCCESS ALERT -->

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
                      :counter="50" />
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
                      :counter="50"/>
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
                      :counter="50"/>
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
                      :counter="50"/>
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
          <v-card-text class="pl-3 ml-1">
            {{ $t('already-registered') }}
            <router-link to="/login" exact>{{ $t('login-here') }}</router-link>
          </v-card-text>
        </v-card>
        <!-- FORM -->

        <!-- DIALOG WINDOW -->
        <v-dialog v-model="tempDialogWindowActive" persistent max-width="450">
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
  import {getMessageFromLocale} from '../main';
  import {mapGetters}           from 'vuex';
  import {mapActions}           from 'vuex';

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
      ...mapActions([
        'clearServerExceptionResponse',
        'clearServerSuccessResponse',
        ''
      ]),
      submit() {
        if (this.$refs.myRegistrationForm.validate()) {
          this.$store.dispatch('registerUserAccount', this.getRegistrationForm);
        } else {
          this.$store.commit('setServerExceptionResponse', 'Form filled incorrectly!');
        }
      },
      clearFormFields() {
        this.$refs.myRegistrationForm.reset();
        this.$store.commit('setRegistrationFormValid', true);
        // without that code below it does not work
        this.$nextTick(() => {
          this.$store.commit('setRegistrationFormValid', true);
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
        set(value) {this.$store.commit('setRegistrationFormFirstName', value);}
      },
      lastName: {
        get() {return this.$store.getters.getRegistrationForm.lastName;},
        set(value) {this.$store.commit('setRegistrationFormLastName', value);}
      },
      email: {
        get() {return this.$store.getters.getRegistrationForm.email;},
        set(value) {this.$store.commit('setRegistrationFormEmail', value);}
      },
      confirmedEmail: {
        get() {return this.$store.getters.getRegistrationForm.confirmedEmail;},
        set(value) {this.$store.commit('setRegistrationFormConfirmedEmail', value);}
      },
      password: {
        get() {return this.$store.getters.getRegistrationForm.password;},
        set(value) {this.$store.commit('setRegistrationFormPassword', value);}
      },
      confirmedPassword: {
        get() {return this.$store.getters.getRegistrationForm.confirmedPassword;},
        set(value) {this.$store.commit('setRegistrationFormConfirmedPassword', value);}
      },
      valid: {
        get() {return this.$store.getters.getRegistrationForm.valid;},
        set(value) {this.$store.commit('setRegistrationFormValid', value);}
      },
      registerButtonDisabled() {
        return this.firstName === '' || !this.firstName || this.lastName === '' || !this.lastName ||
          this.email === '' || !this.email || this.confirmedEmail === '' || !this.confirmedEmail ||
          this.password === '' || !this.password || this.confirmedPassword === '' || !this.confirmedPassword ||
          this.valid === false || this.emailsDoNotMatch === true || this.passwordsDoNotMatch === true ||
          this.isLoading;
      },
      clearButtonDisabled() {
        return (this.firstName === '' || !this.firstName) && (this.lastName === '' || !this.lastName) &&
          (this.email === '' || !this.email) && (this.confirmedEmail === '' || !this.confirmedEmail) &&
          (this.password === '' || !this.password) && (this.confirmedPassword === '' || !this.confirmedPassword) &&
          this.valid === true || this.isLoading;
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
</style>

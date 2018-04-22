<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex xs12 sm8 md8>
        <!-- ERROR ALERT -->
        <my-alert @dismissed="closeErrorAlert"
                  type="error"
                  v-if="serverError"
                  :errorMessage="serverError.errorMessage"/>
        <!-- ERROR ALERT -->

        <!-- SUCCESS ALERT -->
        <my-alert @dismissed="closeSuccessAlert"
                  type="success"
                  v-if="serverResponse"
                  :errorMessage="serverResponse"/>
        <!-- SUCCESS ALERT -->

        <!-- FORM -->
        <v-card class="elevation-12">
          <v-card-text>
            <v-form v-model="form.valid" lazy-validation ref="myRegistrationForm">
              <v-container grid-list-xs>
                <v-layout v-bind="rowColumnDeterminer">
                  <!-- YOUR NAME -->
                  <v-flex xs6 :class="{'mr-2': this.$vuetify.breakpoint.lgAndUp}">
                    <v-text-field
                      prepend-icon="person"
                      name="yourName"
                      :label="$t('first-name-label')"
                      type="text"
                      v-model="form.firstName"
                      :hint="$t('first-name-input-hint')"
                      required
                      :rules="firstNameRules"
                      :counter="50"/>
                  </v-flex>
                  <!-- YOUR NAME -->

                  <!-- YOUR LAST NAME -->
                  <v-flex xs6 :class="{'ml-2': this.$vuetify.breakpoint.lgAndUp}">
                    <v-text-field
                      prepend-icon="person"
                      name="yourLastName"
                      :label="$t('last-name-label')"
                      type="text"
                      v-model="form.lastName"
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
                      v-model="form.email"
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
                      v-model="form.confirmedEmail"
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
                      v-model="form.password"
                      :hint="passwordsMatcherErrorMessage"
                      :persistent-hint="passwordsDoNotMatch"
                      required
                      :rules="passwordRules"
                      :error="passwordsDoNotMatch"
                      :counter="50"
                      :append-icon="hidePasswords ? 'visibility' : 'visibility_off'"
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
                      v-model="form.confirmedPassword"
                      :hint="passwordsMatcherErrorMessage"
                      :persistent-hint="passwordsDoNotMatch"
                      required
                      :rules="confirmPasswordRules"
                      :error="passwordsDoNotMatch"
                      :counter="50"
                      :append-icon="hidePasswords ? 'visibility' : 'visibility_off'"
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
              <span slot="loader" class="custom-loader">
                <v-icon light>cached</v-icon>
              </span>
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

      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import {getMessageFromLocale} from '../main';
  import {mapGetters}           from 'vuex';

  export default {
    data() {
      return {
        dialogWindowActive: true,
        form: {
          firstName: '',
          lastName: '',
          email: '',
          confirmedEmail: '',
          password: '',
          confirmedPassword: '',
          valid: true,
        },
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
      submit() {
        if (this.$refs.myRegistrationForm.validate()) {
          this.$store.dispatch('registerUserAccount', this.form);
        } else {
          this.$store.commit('setServerError', 'Form filled incorrectly!');
        }
      },
      clearFormFields() {
        this.form.firstName = '';
        this.form.lastName = '';
        this.form.email = '';
        this.form.confirmedEmail = '';
        this.form.password = '';
        this.form.confirmedPassword = '';
        this.form.valid = true;
      },
      closeErrorAlert() {
        this.$store.dispatch('clearServerError');
      },
      closeSuccessAlert() {
        this.$store.dispatch('clearServerResponse');
      }
    },
    computed: {
      ...mapGetters([
        'isLoading'
      ]),
      rowColumnDeterminer() {
        const binding = {};
        if (this.$vuetify.breakpoint.mdAndDown) {
          binding.column = true;
        } else {
          binding.row = true;
        }
        return binding;
      },
      registerButtonDisabled() {
        return this.form.firstName === '' || this.form.lastName === '' || this.form.email === '' ||
          this.form.confirmedEmail === '' || this.form.password === '' || this.form.confirmedPassword === '' ||
          this.form.valid === false || this.emailsDoNotMatch === true || this.passwordsDoNotMatch === true ||
          this.isLoading;
      },
      clearButtonDisabled() {
        return this.form.firstName === '' && this.form.lastName === '' && this.form.email === '' &&
          this.form.confirmedEmail === '' && this.form.password === '' && this.form.confirmedPassword === '' &&
          this.form.valid === true;
      },
      emailsDoNotMatch() {
        if (this.form.email !== '' && this.form.confirmedEmail !== '' && this.form.email !== this.form.confirmedEmail) {
          return true;
        }
      },
      passwordsDoNotMatch() {
        if (this.form.password !== '' && this.form.confirmedPassword !== '' && this.form.password !== this.form.confirmedPassword) {
          return true;
        }
      },
      emailsMatcherErrorMessage() {
        if (this.form.email !== '' && this.form.confirmedEmail !== '' && this.form.email !== this.form.confirmedEmail) {
          return `${getMessageFromLocale('emails-do-not-match')}`;
        } else {
          return `${getMessageFromLocale('email-input-hint')}`;
        }
      },
      passwordsMatcherErrorMessage() {
        if (this.form.password !== '' && this.form.confirmedPassword !== '' && this.form.password !== this.form.confirmedPassword) {
          return `${getMessageFromLocale('passwords-do-not-match')}`;
        } else {
          return `${getMessageFromLocale('password-input-hint')}`;
        }
      },
      serverError() {
        return this.$store.getters.getServerError;
      },
      serverResponse() {
        return this.$store.getters.getServerResponse;
      }
    }
  };
</script>

<style scoped>
  button {
    border: none;
  }

  /* For register button spinning */
  .custom-loader {
    animation: loader 1s infinite;
    display: flex;
  }
  @-moz-keyframes loader {
    from {
      transform: rotate(0);
    }
    to {
      transform: rotate(360deg);
    }
  }
  @-webkit-keyframes loader {
    from {
      transform: rotate(0);
    }
    to {
      transform: rotate(360deg);
    }
  }
  @-o-keyframes loader {
    from {
      transform: rotate(0);
    }
    to {
      transform: rotate(360deg);
    }
  }
  @keyframes loader {
    from {
      transform: rotate(0);
    }
    to {
      transform: rotate(360deg);
    }
  }
  /* For register button spinning */
</style>

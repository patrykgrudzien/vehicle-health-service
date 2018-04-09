<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex xs12 sm8 md8>
        <!-- ALERT -->
        <my-alert @dismissed="onDismissed" v-if="serverError" :errorMessage="serverError"/>
        <!-- ALERT -->

        <!-- FORM -->
        <v-card class="elevation-12">
          <v-card-text>
            <v-form v-model="form.valid" lazy-validation v-if="form.show" ref="myRegistrationForm">
              <v-container grid-list-xs>
                <v-layout v-bind="rowColumnDeterminer">
                  <!-- YOUR NAME -->
                  <v-flex xs6 :class="{'mr-2': this.$vuetify.breakpoint.lgAndUp}">
                    <v-text-field
                      prepend-icon="person"
                      name="yourName"
                      label="Your First Name"
                      type="text"
                      v-model="form.firstName"
                      hint="At least 4 characters"
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
                      label="Your Last Name"
                      type="text"
                      v-model="form.lastName"
                      hint="At least 4 characters"
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
                      label="Your Email"
                      type="email"
                      v-model="form.email"
                      :hint="emailsMatcherErrorMessage"
                      :persistent-hint="emailsMatcher"
                      required
                      :rules="emailRules"
                      :error="emailsMatcher"
                      :counter="50"/>
                  </v-flex>
                  <!-- YOUR EMAIL -->

                  <!-- CONFIRM EMAIL -->
                  <v-flex xs6 :class="{'ml-2': this.$vuetify.breakpoint.lgAndUp}">
                    <v-text-field
                      prepend-icon="email"
                      name="confirmEmail"
                      label="Confirm Email"
                      type="email"
                      v-model="form.confirmedEmail"
                      :hint="emailsMatcherErrorMessage"
                      :persistent-hint="emailsMatcher"
                      required
                      :rules="confirmEmailRules"
                      :error="emailsMatcher"
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
                      label="Password"
                      id="password"
                      type="password"
                      v-model="form.password"
                      :hint="passwordsMatcherErrorMessage"
                      :persistent-hint="passwordsMatcher"
                      required
                      :rules="passwordRules"
                      :error="passwordsMatcher"
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
                      label="Confirm Password"
                      id="confirmPassword"
                      type="password"
                      v-model="form.confirmedPassword"
                      :hint="passwordsMatcherErrorMessage"
                      :persistent-hint="passwordsMatcher"
                      required
                      :rules="confirmPasswordRules"
                      :error="passwordsMatcher"
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
            <v-btn color="primary" @click="submit" :disabled="registerButtonDisabled">Register</v-btn>
            <v-btn color="error" @click="clearFormFields" :disabled="resetButtonDisabled" left>Reset</v-btn>
            <v-spacer/>
          </v-card-actions>
          <v-card-text class="pl-3 ml-1">
            Already registered?
            <router-link to="/login" exact>Login here</router-link>
          </v-card-text>
        </v-card>
        <!-- FORM -->

      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import CircleSpinner from 'vue-loading-spinner/src/components/Circle8';

  export default {
    components: {
      'circle-spinner': CircleSpinner
    },
    data() {
      return {
        form: {
          firstName: '',
          lastName: '',
          email: '',
          confirmedEmail: '',
          password: '',
          confirmedPassword: '',
          valid: true,
          show: true
        },
        hidePasswords: true,
        firstNameRules: [
          v => !!v || 'First Name is required',
          v => (v && v.length >= 4) || 'Min 4 characters',
          v => (v && v.length <= 50) || 'Max 50 characters'
        ],
        lastNameRules: [
          v => !!v || 'Last Name is required',
          v => (v && v.length >= 4) || 'Min 4 characters',
          v => (v && v.length <= 50) || 'Max 50 characters'
        ],
        emailRules: [
          v => !!v || 'Email address is required',
          v => (v && v.length >= 4) || 'Min 4 characters',
          v => (v && v.length <= 50) || 'Max 50 characters',
          v => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || 'E-mail must be valid'
        ],
        confirmEmailRules: [
          v => !!v || 'Confirm Email is required',
          v => (v && v.length >= 4) || 'Min 4 characters',
          v => (v && v.length <= 50) || 'Max 50 characters',
          v => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || 'E-mail must be valid'
        ],
        passwordRules: [
          v => !!v || 'Password is required',
          v => (v && v.length >= 4) || 'Min 4 characters',
          v => (v && v.length <= 50) || 'Max 50 characters',
        ],
        confirmPasswordRules: [
          v => !!v || 'Confirm password is required',
          v => (v && v.length >= 4) || 'Min 4 characters',
          v => (v && v.length <= 50) || 'Max 50 characters'
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
        this.form.show = false;
        this.$nextTick(() => {
          this.form.show = true
        });
      },
      onDismissed() {
        this.$store.dispatch('clearServerError');
      }
    },
    computed: {
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
          this.form.valid === false;
      },
      resetButtonDisabled() {
        return this.form.firstName === '' && this.form.lastName === '' && this.form.email === '' &&
          this.form.confirmedEmail === '' && this.form.password === '' && this.form.confirmedPassword === '' &&
          this.form.valid === true;
      },
      emailsMatcher() {
        if (this.form.email !== '' && this.form.confirmedEmail !== '' && this.form.email !== this.form.confirmedEmail) {
          return true;
        }
      },
      passwordsMatcher() {
        if (this.form.password !== '' && this.form.confirmedPassword !== '' && this.form.password !== this.form.confirmedPassword) {
          return true;
        }
      },
      emailsMatcherErrorMessage() {
        if (this.form.email !== '' && this.form.confirmedEmail !== '' && this.form.email !== this.form.confirmedEmail) {
          return 'Emails do not match!';
        } else {
          return 'At least 4 characters';
        }
      },
      passwordsMatcherErrorMessage() {
        if (this.form.password !== '' && this.form.confirmedPassword !== '' && this.form.password !== this.form.confirmedPassword) {
          return 'Passwords do not match!';
        } else {
          return 'At least 4 characters';
        }
      },
      serverError() {
        return this.$store.getters.getServerError;
      }
    }
  };
</script>

<style scoped>
  button {
    border: none;
  }
</style>

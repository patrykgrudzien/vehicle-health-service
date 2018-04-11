<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex xs12 sm8 md6>
        <!-- ALERT -->
        <my-alert @dismissed="onDismissed" v-if="serverError" :errorMessage="serverError"/>
        <!-- ALERT -->

        <!-- FORM -->
        <v-card class="elevation-12">
          <v-card-text>
            <v-form v-model="form.valid" lazy-validation v-if="form.show" ref="myLoginForm">

              <!-- EMAIL -->
              <v-text-field
                prepend-icon="email"
                name="email"
                :label="$t('email-label')"
                type="email"
                v-model="form.email"
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
                v-model="form.password"
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

      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  import CircleSpinner          from 'vue-loading-spinner/src/components/Circle8';
  import {getMessageFromLocale} from "../main";

  export default {
    components: {
      'circle-spinner': CircleSpinner
    },
    data() {
      return {
        form: {
          email: '',
          password: '',
          valid: true,
          show: true
        },
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
      submit() {
        if (this.$refs.myLoginForm.validate()) {
          this.$store.dispatch('login', this.form);
        } else {
          this.$store.commit('setServerError', 'Form filled incorrectly!');
        }
      },
      clearFormFields() {
        this.form.email = '';
        this.form.password = '';
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
      loginButtonDisabled() {
        return this.form.email === '' || this.form.password === '' || this.form.valid === false;
      },
      clearButtonDisabled() {
        return this.form.email === '' && this.form.password === '' && this.form.valid === true;
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

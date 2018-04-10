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
                label="Email"
                type="email"
                v-model="form.email"
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
                hint="At least 4 characters"
                required
                :rules="passwordRules"
                :counter="50"
                :append-icon="hidePasswords ? 'visibility' : 'visibility_off'"
                :append-icon-cb="() => (hidePasswords = !hidePasswords)"
                :type="hidePasswords ? 'password' : 'text'"/>
              <!-- PASSWORD -->
            </v-form>
          </v-card-text>
          <v-card-actions class="pl-3">
            <v-btn color="primary" @click="submit" :disabled="loginButtonDisabled">Login</v-btn>
            <v-btn color="error" @click="clearFormFields" :disabled="resetButtonDisabled" left>Reset</v-btn>
            <v-spacer/>
          </v-card-actions>
          <v-card-text class="pl-3 ml-1">
            New user?
            <router-link to="/registration-form" exact>Register here</router-link>
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
          email: '',
          password: '',
          valid: true,
          show: true
        },
        hidePasswords: true,
        passwordRules: [
          v => !!v || 'Password is required',
          v => (v && v.length >= 4) || 'Min 4 characters',
          v => (v && v.length <= 50) || 'Max 50 characters',
        ],
        emailRules: [
          v => !!v || 'Email address is required',
          v => (v && v.length >= 4) || 'Min 4 characters',
          v => (v && v.length <= 50) || 'Max 50 characters',
          v => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || 'E-mail must be valid'
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
      resetButtonDisabled() {
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

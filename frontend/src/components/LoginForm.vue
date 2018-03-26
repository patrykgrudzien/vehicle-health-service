<template>
  <b-containter>
    <!-- novalidate (disables the browser default feedback tooltips) -->
    <b-form @submit.prevent="validateForm" @reset.prevent="clearFormFields" v-if="form.show" novalidate>
      <b-form-row>
        <b-col cols="4"></b-col>
        <b-col cols="4">
          <b-alert :show="showSuccessAlert" variant="success">Successfully logged in.</b-alert>
          <b-alert :show="showDangerAlert" variant="danger">Bad credentials.</b-alert>
        </b-col>
        <b-col cols="2"></b-col>
      </b-form-row>

      <!-- Email address -->
      <b-form-row>
        <b-col cols="4"></b-col>
        <b-col cols="4">
          <b-form-group id="group-3"
                        label="Email address:"
                        label-for="group-3">
            <b-form-input id="group-3"
                          type="email"
                          v-model.trim="credentials.email"
                          placeholder="Enter email"
                          :class="{'is-invalid': missingEmail && form.attemptSubmit}">
            </b-form-input>
            <!-- Email field validation -->
            <div class="invalid-feedback">
              Please provide your email
            </div>
          </b-form-group>
        </b-col>
        <b-col cols="2"></b-col>
      </b-form-row>
      <!-- Email address -->

      <!-- Your Password -->
      <b-form-row>
        <b-col cols="4"></b-col>
        <b-col cols="4">
          <b-form-group id="group-5"
                        label="Your Password:"
                        label-for="group-5">
            <b-form-input id="group-5"
                          type="password"
                          v-model.trim="credentials.password"
                          placeholder="Enter your password"
                          :class="{'is-invalid': missingPassword && form.attemptSubmit}">
            </b-form-input>
            <!-- Password field validation -->
            <div class="invalid-feedback">
              Please provide your password
            </div>
          </b-form-group>
        </b-col>
        <b-col cols="2"></b-col>
      </b-form-row>
      <!-- Your Password -->

      <!-- Buttons -->
      <b-form-row>
        <b-col cols="4"></b-col>
        <b-button id="login-button" type="submit" variant="secondary">Login</b-button>
        <b-button :disabled="missingInputFields('&&')" type="reset" variant="danger">Reset</b-button>
      </b-form-row>
      <!-- Buttons -->

      <!-- Register here -->
      <b-form-row>
        <b-col cols="4"></b-col>
        <b-col cols="4" style="margin-top: 10px">
          <p>
            New user?
            <router-link to="/registration-form" exact>Register here</router-link>
          </p>
        </b-col>
      </b-form-row>
    </b-form>
    <circle-spinner v-if="!form.show" style="margin: 0 auto"/>
  </b-containter>
</template>

<script>
  import bContainer    from 'bootstrap-vue/es/components/layout/container';
  import CircleSpinner from 'vue-loading-spinner/src/components/Circle8';

  export default {
    components: {
      'b-containter': bContainer,
      'circle-spinner': CircleSpinner
    },
    data() {
      return {
        form: {
          attemptSubmit: false,
          show: true
        },
        credentials: {
          email: '',
          password: ''
        },
        serverResponse: null,
        showSuccessAlert: false,
        showDangerAlert: false
      }
    },
    methods: {
      validateForm() {
        this.form.attemptSubmit = true;
        if (this.missingInputFields('||')) {
          console.log('Form filled incorrectly.')
        } else {
          this.submitForm();
        }
      },
      missingInputFields(operator) {
        if (operator === '||') {
          return this.missingEmail || this.missingPassword;
        } else {
          if (operator === '&&') {
            return this.missingEmail && this.missingPassword;
          }
        }
      },
      submitForm() {
        this.axios.post(`/auth`, this.credentials)
          .then(response => {
            localStorage.setItem('token', response.data.token);
            this.$router.push('/server-health');
          })
          .catch(error => {
            console.log(error);
          })
      },
      clearFormFields() {
        // Reset our form values
        this.credentials.email = '';
        this.credentials.password = '';
        this.form.attemptSubmit = false;
        this.showSuccessAlert = false;
        this.showDangerAlert = false;
        // Trick to reset/clear native browser form validation state
        this.form.show = false;
        this.$nextTick(() => {
          this.form.show = true
        });
      }
    },
    computed: {
      missingEmail() {
        return this.credentials.email === '';
      },
      missingPassword() {
        return this.credentials.password === '';
      }
    }
  };
</script>

<style scoped>
  .invalid-feedback {
    margin-top: 0;
  }

  #login-button {
    margin-right: 10px;
    margin-left: 5px
  }
</style>

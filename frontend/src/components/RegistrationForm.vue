<template>
  <b-containter>
    <!-- novalidate (disables the browser default feedback tooltips) -->
    <b-form @submit.prevent="validateForm" @reset.prevent="clearFormFields" v-if="showForm" novalidate>
      <b-form-row>
        <b-col cols="2"></b-col>
        <b-col cols="8">
          <b-alert :show="showSuccessAlert" variant="success">
            You have been successfully registered. You can login now using <b>{{ successfulResponse }}</b> email address.
          </b-alert>
          <b-alert :show="showDangerAlert && errorMessage === null" variant="danger">Cannot connect to the server to finish registration.</b-alert>
          <b-alert :show="showDangerAlert && errorMessage !== null && errors === null" variant="danger">{{ errorMessage }}</b-alert>
          <b-alert :show="showDangerAlert && errors !== null" variant="danger">
            <p class="error" v-for="error in errors">{{ error }}</p>
          </b-alert>
        </b-col>
        <b-col cols="2"></b-col>
      </b-form-row>
      <b-form-row>
        <b-col cols="2"></b-col>
        <!-- Your Name -->
        <b-col cols="4">
          <b-form-group id="group-1"
                        label="Your Name:"
                        label-for="group-1">
            <b-form-input id="group-1"
                          type="text"
                          v-model.trim="form.firstName"
                          placeholder="Enter first name"
                          :class="{'is-invalid': missingName && form.attemptSubmit}">
            </b-form-input>
            <!-- Name field validation -->
            <div class="invalid-feedback">
              Please provide your first name
            </div>
          </b-form-group>
        </b-col>
        <!-- Your Name -->

        <!-- Your Last Name -->
        <b-col cols="4">
          <b-form-group id="group-2"
                        label="Your Last Name:"
                        label-for="group-2">
            <b-form-input id="group-2"
                          type="text"
                          v-model.trim="form.lastName"
                          placeholder="Enter last name"
                          :class="{'is-invalid': missingLastName && form.attemptSubmit}">
            </b-form-input>
            <!-- Last name field validation -->
            <div class="invalid-feedback">
              Please provide your last name
            </div>
          </b-form-group>
        </b-col>
        <!-- Your Last Name -->
        <b-col cols="2"></b-col>
      </b-form-row>

      <!-- Email address -->
      <b-form-row>
        <b-col cols="2"></b-col>
        <b-col cols="4">
          <b-form-group id="group-3"
                        label="Email address:"
                        label-for="group-3">
            <b-form-input id="group-3"
                          type="email"
                          v-model.trim="form.email"
                          placeholder="Enter email"
                          :class="{'is-invalid': missingEmail && form.attemptSubmit}">
            </b-form-input>
            <!-- Email field validation -->
            <div class="invalid-feedback">
              Please provide your email
            </div>
          </b-form-group>
        </b-col>
        <!-- Email address -->

        <!-- Confirmed Email address -->
        <b-col cols="4">
          <b-form-group id="group-4"
                        label="Confirm Email address:"
                        label-for="group-4">
            <b-form-input id="group-4"
                          type="email"
                          v-model.trim="form.confirmedEmail"
                          placeholder="Confirm your email"
                          :class="{'is-invalid': missingConfirmedEmail && form.attemptSubmit}">
            </b-form-input>
            <!-- Email field validation -->
            <div class="invalid-feedback">
              Please confirm your email
            </div>
          </b-form-group>
        </b-col>
        <!-- Confirmed Email address -->
        <b-col cols="2"></b-col>
      </b-form-row>
      <!-- Email address -->

      <!-- Your Password -->
      <b-form-row>
        <b-col cols="2"></b-col>
        <b-col cols="4">
          <b-form-group id="group-5"
                        label="Your Password:"
                        label-for="group-5">
            <b-form-input id="group-5"
                          type="password"
                          v-model.trim="form.password"
                          placeholder="Enter your password"
                          :class="{'is-invalid': missingPassword && form.attemptSubmit}">
            </b-form-input>
            <!-- Password field validation -->
            <div class="invalid-feedback">
              Please create a password
            </div>
          </b-form-group>
        </b-col>
        <!-- Your Password -->

        <!-- Confirm Password -->
        <b-col cols="4">
          <b-form-group id="group-6"
                        label="Confirm your password:"
                        label-for="group-6">
            <b-form-input id="group-6"
                          type="password"
                          v-model.trim="form.confirmedPassword"
                          placeholder="Confirm your password"
                          :class="{'is-invalid': missingConfirmedPassword && form.attemptSubmit}">
            </b-form-input>
            <!-- Confirm password field validation -->
            <div class="invalid-feedback">
              Please confirm your password
            </div>
          </b-form-group>
        </b-col>
        <b-col cols="2"></b-col>
      </b-form-row>
      <!-- Confirm Password -->

      <!-- Buttons -->
      <b-form-row>
        <b-col cols="2"></b-col>
        <b-col cols="2">
          <b-button type="submit" variant="primary" v-b-modal.modal-1>Register</b-button>
          <b-button :disabled="missingInputFields('&&')" type="reset" variant="danger">Reset</b-button>
        </b-col>
      </b-form-row>
      <!-- Buttons -->

      <!-- Login here -->
      <b-form-row>
        <b-col cols="2"></b-col>
        <b-col cols="4" style="margin-top: 10px">
          <p>Already registered?
            <router-link to="/login" exact>Login here</router-link>
          </p>
        </b-col>
      </b-form-row>
    </b-form>
    <circle-spinner v-if="!showForm" style="margin: 0 auto"/>
  </b-containter>
</template>

<script>
  import bContainer from 'bootstrap-vue/es/components/layout/container';
  import CircleSpinner from 'vue-loading-spinner/src/components/Circle8';

  export default {
    components: {
      'b-containter': bContainer,
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
          attemptSubmit: false
        },
        showForm: true,
        successfulResponse: null,
        errorMessage: '',
        errors: [],
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
          return this.missingName || this.missingLastName || this.missingEmail || this.missingConfirmedEmail ||
            this.missingPassword || this.missingConfirmedPassword;
        } else if (operator === '&&') {
          return this.missingName && this.missingLastName && this.missingEmail && this.missingConfirmedEmail &&
            this.missingPassword && this.missingConfirmedPassword;
        }
      },
      submitForm() {
        this.showForm = false;
        this.axios.post(`/registration/register-user-account`, this.form)
          .then(response => {
            this.showForm = true;
            this.showSuccessAlert = true;
            this.successfulResponse = response.data;
          })
          .catch(error => {
            this.showForm = true;
            this.showDangerAlert = true;
            if (!error.response) {
              this.errorMessage = null;
            } else {
              this.errorMessage = error.response.data.errorMessage;
              this.errors = error.response.data.errors;
            }
          })
      },
      clearFormFields() {
        // Reset our form values
        this.form.firstName = '';
        this.form.lastName = '';
        this.form.email = '';
        this.form.confirmedEmail = '';
        this.form.password = '';
        this.form.confirmedPassword = '';
        this.form.attemptSubmit = false;
        this.showSuccessAlert = false;
        this.showDangerAlert = false;
        // Trick to reset/clear native browser form validation state
        this.showForm = false;
        this.$nextTick(() => {
          this.showForm = true
        });
      }
    },
    computed: {
      missingName() {
        return this.form.firstName === '';
      },
      missingLastName() {
        return this.form.lastName === '';
      },
      missingEmail() {
        return this.form.email === '';
      },
      missingConfirmedEmail() {
        return this.form.confirmedEmail === '';
      },
      missingPassword() {
        return this.form.password === '';
      },
      missingConfirmedPassword() {
        return this.form.confirmedPassword === '';
      }
    }
  };
</script>

<style scoped>
  .error {
    margin: 4px auto;
  }
</style>

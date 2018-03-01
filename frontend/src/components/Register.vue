<template>
  <b-containter>
    <!-- novalidate (disables the browser default feedback tooltips) -->
    <b-form @submit.prevent="validateForm" @reset.prevent="clearFormFields" v-if="showForm" novalidate>
      <b-form-row>
        <b-col cols="2"></b-col>
        <!-- Your Name -->
        <b-col cols="4">
          <b-form-group id="group-1"
                        label="Your Name:"
                        label-for="group-1">
            <b-form-input id="group-1"
                          type="text"
                          v-model="form.name"
                          placeholder="Enter name"
                          :class="{'is-invalid': missingName && form.attemptSubmit}">
            </b-form-input>
            <!-- Name field validation -->
            <div class="invalid-feedback">
              Please provide your name
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
                          v-model="form.lastName"
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
        <b-col cols="8">
          <b-form-group id="group-3"
                        label="Email address:"
                        label-for="group-3">
            <b-form-input id="group-3"
                          type="email"
                          v-model="form.email"
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
        <b-col cols="2"></b-col>
        <b-col cols="8">
          <b-form-group id="group-4"
                        label="Your Password:"
                        label-for="group-4">
            <b-form-input id="group-4"
                          type="password"
                          v-model="form.password"
                          placeholder="Enter your password"
                          :class="{'is-invalid': missingPassword && form.attemptSubmit}">
            </b-form-input>
            <!-- Password field validation -->
            <div class="invalid-feedback">
              Please create a password
            </div>
          </b-form-group>
        </b-col>
        <b-col cols="2"></b-col>
      </b-form-row>
      <!-- Your Password -->

      <!-- Confirm Password -->
      <b-form-row>
        <b-col cols="2"></b-col>
        <b-col cols="8">
          <b-form-group id="group-5"
                        label="Confirm your password:"
                        label-for="group-5">
            <b-form-input id="group-5"
                          type="password"
                          v-model="form.confirmedPassword"
                          placeholder="Confirm your password"
                          :class="{'is-invalid': missingConfirmPassword && form.attemptSubmit}">
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
        <b-col cols="8">
          <b-button type="submit" variant="primary" v-b-modal.modal-1>Submit</b-button>
          <b-button :disabled="missingInputFields('&&')" type="reset" variant="danger">Reset</b-button>
        </b-col>
        <b-col cols="2"></b-col>
      </b-form-row>
      <!-- Buttons -->
    </b-form>

  </b-containter>
</template>

<script>
  import bContainer from 'bootstrap-vue/es/components/layout/container';

  export default {
    components: {
      'b-containter': bContainer
    },
    data() {
      return {
        form: {
          name: '',
          lastName: '',
          email: '',
          password: '',
          confirmedPassword: '',
          attemptSubmit: false
        },
        showForm: true,
        serverResponse: null
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
          return this.missingName || this.missingLastName || this.missingEmail || this.missingPassword || this.missingConfirmPassword;
        } else if (operator === '&&') {
          return this.missingName && this.missingLastName && this.missingEmail && this.missingPassword && this.missingConfirmPassword;
        }
      },
      submitForm() {
        {
          this.axios.post(`register/check`, this.form)
            .then(response => {
              this.serverResponse = response.data;
            })
            .catch(error => {
              if (!error.response) {
                this.serverResponse = 'NETWORK ERROR !!!';
              }
            })
        }
      },
      clearFormFields() {
        /* Reset our form values */
        this.form.name = '';
        this.form.lastName = '';
        this.form.email = '';
        this.form.password = '';
        this.form.confirmedPassword = '';
        this.form.validationError = '';
        /* Trick to reset/clear native browser form validation state */
        this.showForm = false;
        this.$nextTick(() => {
          this.showForm = true
        });
      }
    },
    computed: {
      missingName() {
        return this.form.name === '';
      },
      missingLastName() {
        return this.form.lastName === '';
      },
      missingEmail() {
        return this.form.email === '';
      },
      missingPassword() {
        return this.form.password === '';
      },
      missingConfirmPassword() {
        return this.form.confirmedPassword === '';
      }
    }
  };
</script>

<style scoped>
</style>

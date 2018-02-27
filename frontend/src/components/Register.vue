<template>
  <b-containter>
    <b-form @submit.prevent="onSubmit" @reset.prevent="onReset" v-if="show">
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
                          placeholder="Enter name">
            </b-form-input>
          </b-form-group>
        </b-col>

        <!-- Your Last Name -->
        <b-col cols="4">
          <b-form-group id="group-2"
                        label="Your Last Name:"
                        label-for="group-2">
            <b-form-input id="group-2"
                          type="text"
                          v-model="form.lastName"
                          placeholder="Enter last name">
            </b-form-input>
          </b-form-group>
        </b-col>
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
                          placeholder="Enter email">
            </b-form-input>
          </b-form-group>
        </b-col>
        <b-col cols="2"></b-col>
      </b-form-row>

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
                          placeholder="Enter your password">
            </b-form-input>
          </b-form-group>
        </b-col>
        <b-col cols="2"></b-col>
      </b-form-row>

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
                          placeholder="Confirm your password">
            </b-form-input>
          </b-form-group>
        </b-col>
        <b-col cols="2"></b-col>
      </b-form-row>

      <!-- Buttons -->
      <b-form-row>
        <b-col cols="2"></b-col>
        <b-col cols="8">
          <b-button type="submit" variant="primary">Submit</b-button>
          <b-button type="reset" variant="danger">Reset</b-button>
        </b-col>
        <b-col cols="2"></b-col>
      </b-form-row>

    </b-form>

    <b-form-row>
      <b-col cols="2"></b-col>
      <b-col cols="8">
        <br>
        <p v-if="form.valid" style="font-weight: bold">{{ response }}</p>
        <p v-if="form.validationError" style="color: red; font-weight: bold">{{ form.validationError }}</p>
      </b-col>
    </b-form-row>
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
          validationError: '',
          valid: false
        },
        show: true,
        response: ''
      }
    },
    methods: {
      onSubmit() {
        {
          this.axios.post(`register/check`, this.form)
            .then(response => {
              this.response = response.data;
              this.form.valid = true;
            })
            .catch(error => {
              this.form.validationError = error.response.data;
            })
        }
      },
      onReset() {
        /* Reset our form values */
        this.form.name = '';
        this.form.lastName = '';
        this.form.email = '';
        this.form.password = '';
        this.form.confirmedPassword = '';
        this.form.validationError = '';
        /* Trick to reset/clear native browser form validation state */
        this.show = false;
        this.$nextTick(() => {
          this.show = true
        });
      }
    }
  };
</script>

<style scoped>
</style>

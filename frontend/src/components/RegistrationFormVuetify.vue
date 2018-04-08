<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex xs12 sm8 md8>
        <v-card class="elevation-12">
          <v-card-text>
            <v-form>
              <v-container grid-list-xs>
                <v-layout v-bind="binding">
                  <!-- YOUR NAME -->
                  <v-flex xs6 :class="{'mr-2': this.$vuetify.breakpoint.lgAndUp}">
                    <v-text-field
                      prepend-icon="person"
                      name="yourName"
                      label="Your Name"
                      type="text"
                      v-model="form.firstName"
                      :rules="[(v) => v.length <= 50 || 'Max 50 characters']"
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
                      :rules="[(v) => v.length <= 50 || 'Max 50 characters']"
                      :counter="50"/>
                  </v-flex>
                  <!-- YOUR LAST NAME -->
                </v-layout>
              </v-container>

              <v-container grid-list-xs>
                <v-layout v-bind="binding">
                  <!-- YOUR EMAIL -->
                  <v-flex xs6 :class="{'mr-2': this.$vuetify.breakpoint.lgAndUp}">
                    <v-text-field
                      prepend-icon="email"
                      name="yourEmail"
                      label="Your Email"
                      type="email"
                      v-model="form.email"
                      :rules="[(v) => v.length <= 50 || 'Max 50 characters']"
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
                      :rules="[(v) => v.length <= 50 || 'Max 50 characters']"
                      :counter="50"/>
                  </v-flex>
                  <!-- CONFIRM EMAIL -->
                </v-layout>
              </v-container>

              <v-container grid-list-xs>
                <v-layout v-bind="binding">
                  <!-- PASSWORD -->
                  <v-flex xs6 :class="{'mr-2': this.$vuetify.breakpoint.lgAndUp}">
                    <v-text-field
                      prepend-icon="lock"
                      name="password"
                      label="Password"
                      id="password"
                      type="password"
                      v-model="form.password"
                      :rules="[(v) => v.length <= 50 || 'Max 50 characters']"
                      :counter="50"
                      :append-icon="showPassword ? 'visibility' : 'visibility_off'"
                      :append-icon-cb="() => (showPassword = !showPassword)"
                      :type="showPassword ? 'password' : 'text'"/>
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
                      :rules="[(v) => v.length <= 50 || 'Max 50 characters']"
                      :counter="50"
                      :append-icon="showPassword ? 'visibility' : 'visibility_off'"
                      :append-icon-cb="() => (showPassword = !showPassword)"
                      :type="showPassword ? 'password' : 'text'"/>
                  </v-flex>
                  <!-- CONFIRM PASSWORD -->
                </v-layout>
              </v-container>
            </v-form>
          </v-card-text>
          <v-card-actions class="pl-3">
            <v-btn color="primary" @click="validateForm">Register</v-btn>
            <v-btn color="error" @click="clearFormFields" left>Reset</v-btn>
            <v-spacer/>
          </v-card-actions>
          <v-card-text class="pl-3 ml-1">
            Already registered?
            <router-link to="/login" exact>Login here</router-link>
          </v-card-text>
        </v-card>
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
          confirmedPassword: ''
        },
        showPassword: true
      }
    },
    methods: {
      validateForm() {
        alert('Trying to validate form.');
      },
      clearFormFields() {
        this.form.firstName = '';
        this.form.lastName = '';
        this.form.email = '';
        this.form.confirmedEmail = '';
        this.form.password = '';
        this.form.confirmedPassword = '';
      }
    },
    computed: {
      binding() {
        const binding = {};
        if (this.$vuetify.breakpoint.mdAndDown) {
          binding.column = true;
        } else {
          binding.row = true;
        }
        return binding;
      }
    }
  };
</script>

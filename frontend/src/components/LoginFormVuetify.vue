<template>
  <v-layout align-center justify-center>
    <v-flex xs12 sm8 md4>
      <v-card class="elevation-12">
        <v-card-text>
          <v-form>
            <!-- EMAIL -->
            <v-text-field
              prepend-icon="email"
              name="email"
              label="Email"
              type="email"
              v-model="email"
              :rules="[(v) => v.length <= 50 || 'Max 50 characters']"
              :counter="50"/>
            <!-- EMAIL -->

            <!-- PASSWORD -->
            <v-text-field
              prepend-icon="lock"
              name="password"
              label="Password"
              id="password"
              type="password"
              v-model="password"
              :rules="[(v) => v.length <= 100 || 'Max 100 characters']"
              :counter="100"
              :append-icon="showPassword ? 'visibility' : 'visibility_off'"
              :append-icon-cb="() => (showPassword = !showPassword)"
              :type="showPassword ? 'password' : 'text'"/>
            <!-- PASSWORD -->
          </v-form>
        </v-card-text>
        <v-card-actions class="pl-3">
          <v-btn color="primary">Login</v-btn>
          <v-btn color="error" left>Reset</v-btn>
          <v-spacer/>
        </v-card-actions>
        <v-card-text class="pl-3 ml-1">
          New user? <router-link to="/registration-form" exact>Register here</router-link>
        </v-card-text>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
  import CircleSpinner from 'vue-loading-spinner/src/components/Circle8';

  export default {
    components: {
      'circle-spinner': CircleSpinner
    },
    data() {
      return {
        email: '',
        password: '',
        showPassword: true
      }
    },
    methods: {},
    computed: {}
  };
</script>

<style scoped>
</style>

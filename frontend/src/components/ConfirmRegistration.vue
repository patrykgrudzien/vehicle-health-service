<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex>
        <b-row class="text-center">
          <b-col cols="3"></b-col>
          <b-col>
            <b-alert :show="confirmationFailed" variant="danger">{{ this.confirmationError.message }}</b-alert>
          </b-col>
          <b-col cols="3"></b-col>
        </b-row>
        <b-row v-if="!confirmationFailed">
          <b-col>
            <h2 class="text-center" style="margin-top: 50px;">{{ confirmationMessage }}</h2>
            <div class="text-center">
              <b-link to="/login" router-tag="b-button">Login there!</b-link>
            </div>
          </b-col>
        </b-row>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  export default {
    data() {
      return {
        confirmationMessage: 'Your account has been confirmed and created!',
        confirmationError: {
          code: null,
          display: false,
          message: ''
        },
        hideMainPanel: true
      }
    },
    created() {
      this.confirmationError.code = this.$route.query.error;
    },
    computed: {
      confirmationFailed() {
        if (this.confirmationError.code === 'tokenNotFound') {
          this.confirmationError.display = true;
          this.confirmationError.message = 'Verification token not found for your account. Register again.';
          return true;
        } else {
          return false;
        }
      }
    }
  }
</script>

<style scoped>

</style>

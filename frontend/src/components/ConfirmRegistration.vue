<template>
  <!-- TODO: alerts TYPES should be fixed cause for now there is only SUCCESS type -->
  <login-form :confirmationMessage="() => confirmationFailed"
              :dismissDialog="() => {this.showDialog = false;}"
              :showDialog="showDialog"/>
</template>

<script>
  import LoginForm from './LoginForm';

  export default {
    components: {
      LoginForm
    },
    data() {
      return {
        showDialog: true,
        confirmation: {
          code: null,
          message: ''
        }
      }
    },
    created() {
      this.confirmation.code = this.$route.query.error;
    },
    computed: {
      confirmationFailed() {
        if (this.confirmation.code === 'tokenNotFound') {
          return 'Verification token not found for your account. Register again.';
        } else if (this.confirmation.code === 'tokenExpired') {
          return 'Verification token expired. Would you like to resend it again?';
        } else {
          return 'Your account has been confirmed and created!';
        }
      }
    }
  }
</script>

<style scoped>

</style>

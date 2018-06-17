<template>
  <!-- TODO: alerts TYPES should be fixed cause for now there is only SUCCESS type -->
  <login-form :confirmationMessage="() => confirmationFailed"
              :dismissDialog="() => {this.showDialog = false;}"
              :showDialog="showDialog"/>
</template>

<script>
  import LoginForm              from '../login/LoginForm';
  import {getMessageFromLocale} from "../../main";

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
          return `${getMessageFromLocale('verification-token-not-found')}`;
        } else if (this.confirmation.code === 'tokenExpired') {
          return `${getMessageFromLocale('verification-token-expired')}`;
        } else {
          return `${getMessageFromLocale('account-confirmed-and-activated')}`;
        }
      }
    }
  }
</script>

<style scoped>

</style>

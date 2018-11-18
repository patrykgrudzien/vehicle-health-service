<template>
  <!-- TODO: alerts TYPES should be fixed cause for now there is only SUCCESS type -->
  <login-form :confirmationMessage="() => confirmationMessage"
              :dismissDialog="() => {this.showDialog = false;}"
              :showDialog="showDialog" />
</template>

<script>
import LoginForm from '../login/LoginForm'
import { getMessageFromLocale } from '../../main'

export default {
  components: {
    LoginForm
  },
  data () {
    return {
      showDialog: true,
      confirmation: {
        codeType: {
          error: null,
          info: null
        }
      }
    }
  },
  created () {
    this.confirmation.codeType.error = this.$route.query.error
    this.confirmation.codeType.info = this.$route.query.info
  },
  computed: {
    confirmationMessage () {
      if (this.confirmation.codeType.error !== null && this.confirmation.codeType.error === 'tokenNotFound') {
        return `${getMessageFromLocale('verification-token-not-found')}`
      } else if (this.confirmation.codeType.error !== null && this.confirmation.codeType.error === 'tokenExpired') {
        return `${getMessageFromLocale('verification-token-expired')}`
      } else if (this.confirmation.codeType.info !== null && this.confirmation.codeType.info === 'userAlreadyEnabled') {
        return `${getMessageFromLocale('user-already-enabled')}`
      } else {
        return `${getMessageFromLocale('account-confirmed-and-activated')}`
      }
    }
  }
}
</script>

<style scoped>

</style>

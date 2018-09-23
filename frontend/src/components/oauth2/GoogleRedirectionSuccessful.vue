<template>
  <v-container fluid fill-height>
    <v-layout column align-center justify-center>

      <v-flex class="text-xs-center">
        <textarea rows="50" cols="250" title="label">
          {{ shortLivedAuthTokenQueryParam }}
        </textarea>
      </v-flex>

    </v-layout>
  </v-container>
</template>

<script>
  export default {
    data() {
      return {
        shortLivedAuthTokenQueryParam: ''
      }
    },
    created() {
      this.shortLivedAuthTokenQueryParam = this.$route.query.shortLivedAuthToken;

      this.axios.post('/exchange-short-lived-token', {shortLivedAuthToken: this.shortLivedAuthTokenQueryParam})
          .then(response => {
            console.log(response.data);
          })
          .catch(error => {
            console.log(error.data);
          })
    }
  }
</script>

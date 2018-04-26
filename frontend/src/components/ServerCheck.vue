<template>
  <v-container fluid fill-height>
    <v-layout column align-center justify-center>

      <v-flex id="button-container">
        <v-btn color="primary" @click="callRestService">
          {{ $t('secured-resource-button') }}
        </v-btn>
      </v-flex>

      <v-flex class="text-xs-center" id="message-container">
        <h1>{{ responseMessage }}</h1>
      </v-flex>

    </v-layout>
  </v-container>
</template>

<script>
  export default {
    data() {
      return {
        responseMessage: ''
      }
    },
    methods: {
      callRestService() {
        this.axios.get(`/server/health-check`)
          .then(response => {
            this.responseMessage = response.data.message;
          })
          .catch(error => {
            console.log(error.response.data);
          });
      }
    }
  }
</script>

<style scoped>
  #button-container {
    display: flex;
    align-items: center;
    justify-content: center;
  }
</style>

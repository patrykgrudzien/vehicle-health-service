<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex>
        <b-row class="text-center">
          <b-col cols="3"></b-col>
          <b-col>
            <b-alert :show="dangerAlert.show" variant="danger">{{ this.dangerAlert.message }}</b-alert>
          </b-col>
          <b-col cols="3"></b-col>
        </b-row>
        <div class="text-center">
          <b-button id="call-rest-service" @click="callRestService" variant="secondary">Call Spring Boot REST</b-button>
        </div>
        <b-row>
          <b-col>
            <h4 v-if="response.length > 0">{{ response }}</h4>
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
        message: 'Server Health Check',
        response: [],
        errors: [],
        dangerAlert: {
          show: false,
          message: ''
        }
      }
    },
    methods: {
      callRestService() {
        this.axios.get(`/server/health-check`)
          .then(response => {
            if (response.data.code) {
              this.dangerAlert.show = true;
              this.dangerAlert.message = response.data.message;
            } else {
              this.response = response.data;
            }
          })
          .catch(error => {
            this.errors.push(error);
          })
      }
    }
  }
</script>

<style scoped>
  #call-rest-service {
    margin-top: 50px;
    margin-bottom: 10px;
  }

  h1, h4 {
    text-align: center;
    margin-bottom: 20px;
  }
</style>

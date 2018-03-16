<template>
  <b-container>
    <div class="text-center">
      <b-button id="call-rest-service" @click="callRestService" variant="secondary">Call Spring Boot REST</b-button>
    </div>
    <b-row>
      <b-col>
        <h4 v-if="response.length > 0">{{ response }}</h4>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
  import bContainer from 'bootstrap-vue/es/components/layout/container';

  export default {
    components: {
      'b-containter': bContainer
    },
    data() {
      return {
        message: 'Server Health Check',
        response: [],
        errors: []
      }
    },
    methods: {
      callRestService() {
        this.axios.get(`/server/health-check`)
          .then(response => {
            this.response = response.data;
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

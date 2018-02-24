<template>
  <div id="main-component">
    <h1>{{ message }}</h1>
    <div id="button-wrapper" class="row align-items-center justify-content-center">
      <b-btn class="col" @click="callRestService">CALL Spring Boot REST backend service</b-btn>
    </div>
    <h4 v-if="response.length > 0">Backend response: {{ response }}</h4>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        message: 'Main App Component !',
        response: [],
        errors: []
      }
    },
    methods: {
      callRestService() {
        this.axios.get(`api/hello`)
          .then(response => {
            console.log(response);
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
  #main-component {
    width: 900px;
    height: 500px;
    margin: 50px auto auto auto;
  }

  h1, h4 {
    text-align: center;
    margin-bottom: 20px;
  }

  #button-wrapper {
    margin-bottom: 20px;
  }
</style>

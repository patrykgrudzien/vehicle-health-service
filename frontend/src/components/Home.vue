<template>
  <b-container>
    <b-row>
      <b-col>
        <h2 class="text-center" style="margin-top: 50px;">{{ homeMessage }}</h2>
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
        homeMessage: 'Welcome to the Home Page!'
      }
    }
  }
</script>

<style scoped>
  h1 {
    text-align: center;
  }
</style>

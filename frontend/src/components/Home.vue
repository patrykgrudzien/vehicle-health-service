<template>
  <div id="home-component">
    <h1>{{ homeMessage }}</h1>
  </div>
</template>

<script>
    export default {
        data() {
          return {
            homeMessage: 'Welcome to the Home Page!'
          }
        }
    }
</script>

<style scoped>
  #home-component {
    width: 900px;
    height: 500px;
    margin: 50px auto auto auto;
  }

  h1 {
    text-align: center;
    margin-bottom: 20px;
  }
</style>

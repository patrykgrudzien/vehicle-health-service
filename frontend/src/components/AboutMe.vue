<template>
  <b-containter>
    <div id="about-me">
      <p>My name is: </p><h6>{{ name }}</h6>
      <p>My last name is:</p><h6>{{ lastName}}</h6>
      <p>I'm: </p><h6>{{ age }}</h6>
      <p>My Profession is:</p><h6>{{ profession }}</h6>
    </div>
  </b-containter>
</template>

<script>
  import bContainer from 'bootstrap-vue/es/components/layout/container';

  export default {
    components: {
      'b-containter': bContainer
    },
    data() {
      return {
        name: 'Patryk',
        lastName: 'Grudzień',
        age: 27,
        profession: 'Junior Software Engineer'
      }
    }
  }
</script>

<style scoped>
  p {
    text-align: center;
    margin-bottom: 0;
  }
  h6 {
    text-align: center;
  }
</style>

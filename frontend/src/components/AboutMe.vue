<template>
  <div id="about-me">
    <p>My name is: </p><h6>{{ name }}</h6>
    <p>My last name is:</p><h6>{{ lastName}}</h6>
    <p>I'm: </p><h6>{{ age }}</h6>
    <p>My Profession is:</p><h6>{{ profession }}</h6>
  </div>
</template>

<script>
  export default {
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

<template>
  <div id="app">
    <h1>{{ msg }}</h1>
    <router-view/>
  </div>
</template>

<script>
export default {
  data() {
    return {
      msg: 'Main App Component !'
    }
  }
}
</script>

<style scoped>
</style>

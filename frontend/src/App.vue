<template>
  <div id="app">
    <app-header/>
    <router-view v-animate-css="animationInfinite"/>
  </div>
</template>

<script>
  import AppHeader from './components/AppHeader';

  export default {
    components: {
      AppHeader
    },
    data() {
      return {
        animationInfinite: {
          classes: 'fadeIn',
          duration: 1000,
        }
      }
    }
  };
</script>

<style scoped>
</style>

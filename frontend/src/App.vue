<template>
  <div id="app">
    <div v-cloak>
      <app-nav-bar/>
      <router-view v-animate-css="animationInfinite"/>
    </div>
  </div>
</template>

<script>
  import AppNavBar from './components/AppNavBar';

  export default {
    components: {
      AppNavBar
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

<style>
  [v-cloak] {
    display: none;
  }
  body {
    background-color: #303030;
    color: #ffffff;
  }
</style>

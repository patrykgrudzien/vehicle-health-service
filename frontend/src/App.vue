<template>
  <div id="app">
    <div v-cloak>
      <app-nav-bar/>
      <transition name="fade">
        <router-view/>
      </transition>
    </div>
  </div>
</template>

<script>
  import AppNavBar from './components/AppNavBar';

  export default {
    components: {
      AppNavBar
    }
  };
</script>

<style>
  .fade-enter-active, .fade-leave-active {
    transition-property: opacity;
    transition-duration: .25s;
  }

  .fade-enter-active {
    transition-delay: .25s;
  }

  .fade-enter, .fade-leave-active {
    opacity: 0
  }

  [v-cloak] {
    display: none;
  }

  body {
    background-color: #303030;
    color: #ffffff;
    overflow: hidden;
  }
</style>

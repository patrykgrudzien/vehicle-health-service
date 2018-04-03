<template>
  <v-app>
    <v-toolbar>
      <v-toolbar-title>Full-Stack Web App</v-toolbar-title>
      <v-toolbar-items>
        <v-btn flat>View Meetups</v-btn>
      </v-toolbar-items>
    </v-toolbar>
    <main></main>
    <!--<div v-cloak>-->
      <!--<app-nav-bar/>-->
      <!--<transition name="fade">-->
        <!--<router-view/>-->
      <!--</transition>-->
    <!--</div>-->
  </v-app>
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

  /*#app {*/
    /*background-color: #303030;*/
    /*color: #ffffff;*/
    /*overflow-y: visible;*/
  /*}*/
</style>

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
  import { mapGetters } from 'vuex';

  export default {
    components: {
      AppNavBar
    },
    computed: mapGetters([
      'isLogged'
    ]),
    methods: {
      logout() {
        // TODO: to be implemented
        this.$store.dispatch('logout');
      }
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
    overflow-y: visible;
  }
</style>

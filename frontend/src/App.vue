<template>
  <v-app dark>

    <!-- TOOLBAR -->
    <v-toolbar dark class="primary">
      <v-toolbar-side-icon @click="sideNav = !sideNav" class="hidden-sm-and-up"/>
      <v-toolbar-title>
        <router-link to="/" tag="span" style="cursor: pointer">Full-Stack Web App</router-link>
      </v-toolbar-title>
      <v-spacer/>
      <v-toolbar-items class="hidden-xs-only">
        <v-btn flat v-for="item in menuItems" :key="item.title" router :to="item.link">
          <v-icon left>{{ item.icon }}</v-icon>
          {{ item.title }}
        </v-btn>
      </v-toolbar-items>
    </v-toolbar>

    <!-- MAIN -->
    <main>
      <transition name="fade">
        <router-view/>
      </transition>
    </main>

    <!-- NAVIGATION DRAWER -->
    <v-navigation-drawer temporary absolute v-model="sideNav">
      <v-list>
        <v-list-tile v-for="item in menuItems" :key="item.title" router :to="item.link">
          <v-list-tile-action>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>{{ item.title }}</v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>

  </v-app>
</template>

<script>
  import AppNavBar from './components/AppNavBar';

  export default {
    components: {
      AppNavBar
    },
    data() {
      return {
        sideNav: false,
        menuItems: [
          {icon: 'person', title: 'About me', link: '/about-me'},
          {icon: 'face', title: 'Sign up', link: '/registration-form'},
          {icon: 'lock_open', title: 'Sign in', link: '/login'}
        ]
      }
    }
  };
</script>

<style>
  a:hover {
    text-decoration: none;
    color: #fff;
  }

  .btn {
    padding: 0;
  }

  .toolbar__content {
    box-shadow: 0 0 50px 0 black;
  }

  #app {
    background-color: #303030;
  }

  html {
    overflow-y: visible;
  }

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
</style>

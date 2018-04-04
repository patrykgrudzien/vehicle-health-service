<template>
  <v-app>

    <!-- TOOLBAR -->
    <v-toolbar dark class="primary">
      <v-toolbar-side-icon @click="sideNav = !sideNav" class="hidden-sm-and-up"/>
      <v-toolbar-title>
        <router-link to="/" tag="span" style="cursor: pointer">Full-Stack Web App</router-link>
      </v-toolbar-title>
      <v-spacer/>
      <v-toolbar-items class="hidden-xs-only" v-for="item in menuItems" :key="item.title">
        <v-btn flat>
          <v-icon left>{{ item.icon }}</v-icon>
          {{ item.title }}
        </v-btn>
      </v-toolbar-items>
    </v-toolbar>

    <!-- NAVIGATION DRAWER -->
    <v-navigation-drawer v-model="sideNav">
      <v-list v-for="item in menuItems" :key="item.title">
        <v-list-tile>
          <v-list-tile-action>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>{{ item.title }}</v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>

    <!-- MAIN -->
    <main>
      <router-view/>
    </main>

    <!-- OLD -->

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
  .btn {
    padding: 0;
  }

  .toolbar__content {
    box-shadow: 0 0px 50px 0px black;
  }

  #app {
    background-color: #303030;
  }
</style>

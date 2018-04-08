<template>
  <v-app id="inspire" dark>

    <!-- NAVIGATION DRAWER -->
    <v-navigation-drawer app clipped absolute temporary v-model="sideNav">
      <v-list dense>
        <v-list-tile v-for="item in menuItems" :key="item.title" router :to="item.link">
          <v-list-tile-action>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ item.title }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>

    <!-- TOOLBAR -->
    <v-toolbar app fixed clipped-left class="primary">
      <v-toolbar-side-icon @click.stop="sideNav = !sideNav" class="hidden-sm-and-up"/>
      <v-toolbar-title>
        <router-link to="/" tag="span" style="cursor: pointer;">Full-Stack Web App</router-link>
      </v-toolbar-title>
      <v-spacer/>
      <v-toolbar-items class="hidden-xs-only">
        <v-btn flat v-for="item in menuItems" :key="item.title" router :to="item.link">
          <v-icon left>
            {{ item.icon }}
          </v-icon>
          {{ item.title }}
        </v-btn>
      </v-toolbar-items>
    </v-toolbar>

    <!-- CONTENT -->
    <v-content style="position: static">
      <transition name="fade" mode="out-in">
        <router-view/>
      </transition>
    </v-content>

    <!-- FOOTER -->
    <v-footer app fixed class="pa-3">
      <v-spacer/>
      <span>&copy; Made by Patryk Grudzień 2018</span>
    </v-footer>

  </v-app>
</template>

<script>
  export default {
    data() {
      return {
        sideNav: false,
        menuItems: [
          {
            icon: 'face',
            title: 'Sign up',
            link: '/registration-form'
          },
          {
            icon: 'lock_open',
            title: 'Sign in',
            link: '/login'
          },
          {
            icon: 'person',
            title: 'About me',
            link: '/about-me'
          }
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
    box-shadow: 0 5px 50px 0 black;
  }

  footer {
    box-shadow: 0 -5px 50px 0 black;
  }

  #app {
    background-color: #303030;
  }

  html {
    overflow: visible;
  }

  .fade-enter-active, .fade-leave-active {
    transition: opacity .5s;
  }

  .fade-enter, .fade-leave-to {
    opacity: 0;
  }
</style>

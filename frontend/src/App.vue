<template>
  <v-app dark>

    <v-layout row>
      <v-flex xs12>
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

        <v-toolbar dark class="primary">
          <!-- TOOLBAR -->
          <v-toolbar-side-icon @click="sideNav = !sideNav" class="hidden-sm-and-up"/>
          <v-toolbar-title>
            <router-link to="/" tag="span" style="cursor: pointer; padding: 16px;">Full-Stack Web App</router-link>
          </v-toolbar-title>
          <v-spacer/>
          <v-toolbar-items class="hidden-xs-only">
            <v-btn flat v-for="item in menuItems" :key="item.title" router :to="item.link">
              <v-icon left>{{ item.icon }}</v-icon>
              {{ item.title }}
            </v-btn>
          </v-toolbar-items>
        </v-toolbar>
      </v-flex>
    </v-layout>

    <!-- MAIN -->
    <v-layout row>
      <v-flex xs12>
        <router-view/>
      </v-flex>
    </v-layout>

    <!-- FOOTER -->
    <v-layout row>
      <v-flex xs12>
        <v-footer dark fixed class="pa-3 footer">
          <v-spacer/>
          <div>&copy; Made by Patryk Grudzień</div>
        </v-footer>
      </v-flex>
    </v-layout>

  </v-app>
</template>

<script>
  export default {
    data() {
      return {
        sideNav: false,
        menuItems: [
          {
            icon: 'person',
            title: 'About me',
            link: '/about-me'
          },
          {
            icon: 'face',
            title: 'Sign up',
            link: '/registration-form'
          },
          {
            icon: 'lock_open',
            title: 'Sign in',
            link: '/login'
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
    overflow-y: visible;
  }
</style>

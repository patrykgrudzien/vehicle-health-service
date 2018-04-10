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
        <!-- LANGUAGES -->
        <v-list-group
          v-for="language in languagesNavDrawer"
          v-model="language.active"
          :key="language.title"
          :prepend-icon="language.icon">

          <!-- HEADER -->
          <v-list-tile slot="activator">
            <v-list-tile-content>
              <v-list-tile-title>{{ language.title }}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <!-- HEADER -->

          <!-- ITEMS -->
          <v-list-tile v-for="subItem in language.items" :key="subItem.title" @click="showAlert(`${subItem.title}`)">
            <v-list-tile-action>
              <v-icon>
                <!-- for now there is no icon here (added for alignment purpose) -->
              </v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{ subItem.title }}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <!-- ITEMS -->

        </v-list-group>
        <!-- LANGUAGES -->
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

        <!-- LANGUAGES -->
        <v-menu open-on-hover offset-y>
          <v-btn flat slot="activator">
            <v-icon left>language</v-icon>
            Language
          </v-btn>
          <v-list>
            <v-list-tile v-for="language in languages" :key="language.title" @click="showAlert(`${language.title}`)">
              <v-list-tile-title>{{ language.title }}</v-list-tile-title>
            </v-list-tile>
          </v-list>
        </v-menu>
        <!-- LANGUAGES -->

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
        languages: [
          {title: 'Polish'},
          {title: 'English'},
        ],
        languagesNavDrawer: [
          {
            icon: 'language',
            title: 'Language',
            active: false,
            items: [
              {title: 'Polish'},
              {title: 'English'},
            ]
          },
        ],
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
    },
    methods: {
      showAlert(buttonContent) {
        alert(buttonContent);
      }
    }
  };
</script>

<style>
  a {
    text-decoration: none;
  }

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

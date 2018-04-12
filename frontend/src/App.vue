<template>
  <v-app id="inspire" dark>

    <!-- NAVIGATION DRAWER -->
    <v-navigation-drawer app clipped temporary v-model="sideNav">
      <v-list dense>
        <v-list-tile v-for="item in menuItems" :key="item.title" router :to="item.link">
          <v-list-tile-action>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t(`${item.title}`) }}</v-list-tile-title>
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
              <v-list-tile-title>{{ $t(`${language.title}`) }}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <!-- HEADER -->

          <!-- ITEMS -->
          <v-list-tile v-for="subItem in language.items"
                       :key="subItem.title"
                       @click="setLang(`${subItem.langCode}`)">
            <v-list-tile-action>
              <v-icon>
                <!-- for now there is no icon here (added for alignment purpose) -->
              </v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{ $t(`${subItem.title}`) }}</v-list-tile-title>
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
        <router-link to="/" tag="span" style="cursor: pointer;">{{ $t('place-for-app-title') }}</router-link>
      </v-toolbar-title>
      <v-spacer/>
      <v-toolbar-items class="hidden-xs-only">
        <v-btn flat v-for="item in menuItems" :key="item.title" router :to="item.link">
          <v-icon left>
            {{ item.icon }}
          </v-icon>
          {{ $t(`${item.title}`) }}
        </v-btn>

        <!-- LANGUAGES -->
        <v-menu offset-y>
          <v-btn flat slot="activator">
            <v-icon left>language</v-icon>
            {{ $t('language-menu-button') }}
          </v-btn>
          <v-list>
            <v-list-tile v-for="language in languages"
                         :key="language.title"
                         @click="setLang(`${language.langCode}`)">
              <v-list-tile-title>{{ $t(`${language.title}`) }}</v-list-tile-title>
            </v-list-tile>
          </v-list>
        </v-menu>
        <!-- LANGUAGES -->

      </v-toolbar-items>
    </v-toolbar>
    <!-- TOOLBAR -->

    <!-- CONTENT -->
    <v-content style="position: static">
      <transition name="fade" mode="out-in">
        <router-view/>
      </transition>
    </v-content>

    <!-- FOOTER -->
    <v-footer app fixed class="pa-3">
      <v-spacer/>
      <span>&copy; {{ $t('footer-text') }}</span>
    </v-footer>

  </v-app>
</template>

<script>
  export default {
    data() {
      return {
        sideNav: false,
        languages: [
          {
            title: 'language-title-polish',
            langCode: 'pl'
          },
          {
            title: 'language-title-english',
            langCode: 'en'
          },
        ],
        languagesNavDrawer: [
          {
            icon: 'language',
            title: 'language-menu-button',
            active: false,
            items: [
              {
                title: 'language-title-polish',
                langCode: 'pl'
              },
              {
                title: 'language-title-english',
                langCode: 'en'
              },
            ]
          },
        ],
        menuItems: [
          {
            icon: 'face',
            title: 'sign-up-menu-button',
            link: '/registration-form'
          },
          {
            icon: 'lock_open',
            title: 'sign-in-menu-button',
            link: '/login'
          },
          {
            icon: 'person',
            title: 'about-me-menu-button',
            link: '/about-me'
          }
        ]
      }
    },
    methods: {
      // TODO: this should also clear form fields (if not old language messages will be displayed)
      setLang: function(lang){
        this.$store.dispatch('setLang', lang)
          .then(() => {
            this.sideNav = false;
            this.languagesNavDrawer.active = false;
          });
      }
    },
    created() {
      this.$i18n.locale = this.$store.getters.getLang;
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

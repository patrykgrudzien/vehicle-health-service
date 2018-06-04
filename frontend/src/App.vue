<template>
  <v-app id="inspire" dark>

    <!-- NAVIGATION DRAWER -->
    <v-navigation-drawer app clipped temporary v-model="sideNavigation">
      <v-list dense>
        <v-list-tile ripple v-for="item in menuItems" :key="item.title" router :to="item.link">
          <v-list-tile-action>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t(`${item.title}`) }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>

        <!-- PRINCIPAL USER (AVATAR) -->
        <v-list-tile ripple v-if="isLogged === 1">
          <v-list-tile-action>
            <v-icon left>account_circle</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>
              <span>{{ $t('welcome-principal-user') }}</span>
              <span></span>
              <span>{{ getPrincipalFirstName }}</span>
            </v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <!-- PRINCIPAL USER (AVATAR) -->

        <!-- LOGOUT -->
        <v-list-tile ripple v-if="isLogged === 1" @click="logout">
          <v-list-tile-action>
            <v-icon left>lock_outline</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t(`${'logout-menu-button'}`) }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <!-- LOGOUT -->

        <!-- LANGUAGES -->
        <v-list-group
          v-if="!isLogged"
          v-for="language in languagesNavDrawer"
          v-model="language.active"
          :key="language.title"
          :prepend-icon="language.icon">

          <!-- HEADER -->
          <v-list-tile ripple slot="activator">
            <v-list-tile-content>
              <v-list-tile-title>{{ $t(`${language.title}`) }}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <!-- HEADER -->

          <!-- ITEMS -->
          <v-list-tile v-for="language in languages"
                       :key="language.title"
                       @click="showModalAndSendLang(`${language.langCode}`)"
                       ripple>
            <v-list-tile-action>
              <v-icon>
                <!-- for now there is no icon here (added for alignment purpose) -->
              </v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{ $t(`${language.title}`) }}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <!-- ITEMS -->

        </v-list-group>
        <!-- LANGUAGES -->
      </v-list>
    </v-navigation-drawer>

    <!-- TOOLBAR -->
    <v-toolbar app
               fixed
               clipped-left
               class="primary"
               dense
               scroll-off-screen
               :scroll-threshold="75">
      <v-toolbar-side-icon @click.stop="switchSideNavigation(!sideNavigation)" class="hidden-md-and-up"/>
      <v-toolbar-title class="hidden-xs notSelectable">
        <router-link :to="determineHomeLink"
                     tag="span"
                     style="cursor: pointer;">
          {{ $t('place-for-app-title') }}
        </router-link>
      </v-toolbar-title>
      <v-spacer/>
      <v-toolbar-items class="hidden-sm-and-down">

        <!-- PRINCIPAL USER (AVATAR) -->
        <v-btn flat ripple v-if="isLogged === 1">
          <span>{{ $t('welcome-principal-user') }}</span>
          <span class="ml-1"></span>
          <span>{{ getPrincipalFirstName }}</span>
          <v-icon right dark>
            account_circle
          </v-icon>
        </v-btn>
        <!-- PRINCIPAL USER (AVATAR) -->

        <v-btn
          flat
          v-for="item in menuItems"
          :key="item.title"
          router
          :to="item.link"
          ripple>
          <v-icon left>
            {{ item.icon }}
          </v-icon>
          {{ $t(`${item.title}`) }}
        </v-btn>

        <!-- LOGOUT -->
        <v-btn flat v-if="isLogged === 1" @click="logout">
          <v-icon left>lock_outline</v-icon>
          {{ $t(`${'logout-menu-button'}`) }}
        </v-btn>
        <!-- LOGOUT -->

        <!-- LANGUAGES -->
        <v-menu offset-y v-if="!isLogged">
          <v-btn flat slot="activator">
            <v-icon left>language</v-icon>
            {{ $t('language-menu-button') }}
          </v-btn>
          <v-list>
            <v-list-tile v-for="language in languages"
                         :key="language.title"
                         @click="showModalAndSendLang(`${language.langCode}`)">
              <v-list-tile-title>{{ $t(`${language.title}`) }}</v-list-tile-title>
            </v-list-tile>
          </v-list>
        </v-menu>
        <!-- LANGUAGES -->

      </v-toolbar-items>
    </v-toolbar>
    <!-- TOOLBAR -->

    <!-- CONTENT (ROUTES) -->
    <v-content style="position: static">
      <transition name="fade" mode="out-in">
        <router-view/>
      </transition>
    </v-content>
    <!-- CONTENT (ROUTES) -->

    <!-- FOOTER -->
    <v-footer app
              fixed
              class="pa-3"
              v-if="isLogged === 0 || isLogged === null">
      <v-spacer/>
      <span class="notSelectable">&copy; {{ $t('footer-text') }}</span>
    </v-footer>
    <!-- FOOTER -->

  </v-app>
</template>

<script>
  import {mapGetters}    from 'vuex';
  import {mapActions}    from 'vuex';
  import componentsPaths from './componentsPaths';

  export default {
    data() {
      return {
        languagesNavDrawer: [
          {
            icon: 'language',
            title: 'language-menu-button'
          },
        ]
      }
    },
    methods: {
      ...mapActions([
        'logout'
      ]),
      showModalAndSendLang: function (lang) {

        if (this.$route.path.includes(componentsPaths.loginForm) ||
          this.$route.path.includes(componentsPaths.registrationForm) ||
          this.$route.path.includes(componentsPaths.confirmRegistration) ||
          this.$route.path.includes('/logout') ||
          this.$route.fullPath.includes(componentsPaths.logoutSuccessful)) {

          this.$router.app.$emit('open-dialog-and-send-lang',
            {
              showDialog: true,
              lang: lang
            }
          );
        } else {
          this.$store.dispatch('setLang', lang)
            .then(() => {
              this.$store.commit('setSideNavigation', false);
            });
        }
      },
      switchSideNavigation(value) {
        this.$store.commit('setSideNavigation', value);
      }
    },
    computed: {
      ...mapGetters([
        'getSideNavigation',
        'isLogged',
        'getPrincipalFirstName'
      ]),
      menuItems() {
        if (this.isLogged) {
          return [
            // FOR NOW THERE ARE ONLY "LOGOUT" AND "LANGUAGE" BUTTONS
          ]
        } else {
          return [
            {
              icon: 'face',
              title: 'sign-up-menu-button',
              link: componentsPaths.registrationForm
            },
            {
              icon: 'lock_open',
              title: 'sign-in-menu-button',
              link: componentsPaths.loginForm
            },
            {
              icon: 'person',
              title: 'about-me-menu-button',
              link: componentsPaths.aboutMe
            }
          ]
        }
      },
      sideNavigation: {
        get() {return this.$store.getters.getSideNavigation;},
        set(value) {this.$store.commit('setSideNavigation', value);}
      },
      languages() {
        if (this.$store.getters.getLang === 'pl') {
          return [
            {
              title: 'language-title-english',
              langCode: 'en'
            }
          ]
        } else {
          if (this.$store.getters.getLang === 'en') {
            return [
              {
                title: 'language-title-polish',
                langCode: 'pl'
              }
            ]
          }
        }
      },
      determineHomeLink() {
        return this.isLogged === 1 ? componentsPaths.mainBoard : componentsPaths.home;
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

  .notSelectable {
    -webkit-touch-callout: none;    /* iOS Safari */
    -webkit-user-select: none;      /* Safari */
    -khtml-user-select: none;       /* Konqueror HTML */
    -moz-user-select: none;         /* Firefox */
    -ms-user-select: none;          /* Internet Explorer/Edge */
    user-select: none;              /* Non-prefixed version, currently supported by Chrome and Opera */
  }

  .centerTextInsideDiv {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .centerSpanInsideDiv {
    display: table;
    margin: 0 auto;
  }
</style>

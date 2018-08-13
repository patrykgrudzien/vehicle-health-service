<template>
  <v-app id="inspire" dark>

    <!-- NAVIGATION DRAWER -->
    <v-navigation-drawer app
                         id="my-nav-drawer"
                         clipped
                         temporary
                         v-model="sideNavigation">
      <v-list dense>
        <v-list-tile ripple
                     v-for="item in menuItems"
                     :key="item.title"
                     router
                     :to="item.link">
          <v-list-tile-action>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t(`${item.title}`) }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>

        <!-- PRINCIPAL USER (AVATAR) -->
        <v-list-tile id="avatar"
                     ripple
                     inactive
                     v-if="isLogged === 1 && getPrincipalUserFirstName !== null">
          <v-list-tile-action>
            <v-icon left>account_circle</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>
              <span>{{ $t('welcome-principal-user') }}</span>
              <span></span>
              <span>{{ getPrincipalUserFirstName }}</span>
            </v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <!-- PRINCIPAL USER (AVATAR) -->

        <!-- LOGOUT -->
        <v-list-tile ripple
                     v-if="isLogged === 1"
                     @click="logout">
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
               id="my-toolbar"
               fixed
               clipped-left
               class="primary"
               dense
               scroll-off-screen
               :scroll-threshold="75">
      <v-toolbar-side-icon @click.stop="switchSideNavigation(!sideNavigation)"
                           class="hidden-md-and-up"/>
      <v-toolbar-title class="hidden-xs notSelectable">
        <router-link :to="determineHomeLink"
                     tag="span"
                     style="cursor: pointer;">
          {{ $t('place-for-app-title') }}
        </router-link>
      </v-toolbar-title>

      <!-- HOME ICON -->
      <v-btn flat
             v-if="showHomeVisibility"
             icon
             @click="homeIconClicked">
        <v-icon medium
                class="notSelectable">
          home
        </v-icon>
      </v-btn>
      <!-- HOME ICON -->

      <v-spacer/>
      <!-- MENU ITEMS -->
      <v-toolbar-items id="toolbar-items"
                       class="hidden-sm-and-down">
        <v-btn
          flat
          v-for="item in menuItems"
          :id="item.title"
          router
          :to="item.link"
          ripple
          :key="item.title">
          <v-icon left>
            {{ item.icon }}
          </v-icon>
          {{ $t(`${item.title}`) }}
        </v-btn>
        <!-- MENU ITEMS -->

        <!-- PRINCIPAL USER (AVATAR) -->
        <v-btn flat
               disabled
               style="color: white !important;"
               ripple
               v-if="isLogged === 1 && getPrincipalUserFirstName !== null">
          <span>{{ $t('welcome-principal-user') }}</span>
          <span class="ml-1"></span>
          <span>{{ getPrincipalUserFirstName }}</span>
          <v-icon right
                  style="color: white !important;"
                  dark>
            account_circle
          </v-icon>
        </v-btn>
        <!-- PRINCIPAL USER (AVATAR) -->

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
      <!-- MENU ITEMS -->
    </v-toolbar>
    <!-- TOOLBAR -->

    <!-- CONTENT (ROUTES) -->
    <v-content>
      <transition name="fade"
                  mode="out-in">
        <router-view :key="$route.fullPath" />
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

    <!-- JWT TOKEN EXPIRED WINDOW -->
    <my-dialog :visibility="isJwtAccessTokenExpired && isLogged === 1"
               dialog-title="token-window-title"
               dialog-text="token-window-text"
               agree-button-text="token-window-agree-button-text"
               disagree-button-text="token-window-disagree-button-text"
               :agree-button-function="stayLogInButtonClicked"
               :disagree-button-function="logoutButtonClicked" />
    <!-- JWT TOKEN EXPIRED WINDOW -->

    <!-- LOADING DIALOG WINDOW -->
    <loading-dialog :visibility="isLoading" />
    <!-- LOADING DIALOG WINDOW -->

  </v-app>
</template>

<script>
  import {mapGetters}      from 'vuex';
  import {mapActions}      from 'vuex';
  import componentsDetails from './componentsDetails';
  import {MUTATIONS}       from './Constants';

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

        if (this.$route.path.includes(componentsDetails.loginForm.path) ||
          this.$route.path.includes(componentsDetails.registrationForm.path) ||
          this.$route.path.includes(componentsDetails.confirmRegistration.path) ||
          this.$route.path.includes('/logout') ||
          this.$route.fullPath.includes(componentsDetails.logoutSuccessful.path)) {

          this.$router.app.$emit('open-dialog-and-send-lang', {
            showDialog: true,
            lang: lang
          });

        } else {
          this.$store.dispatch('setLang', lang)
            .then(() => {
              this.$store.commit('setSideNavigation', false);
            });
        }
      },
      switchSideNavigation(value) {
        this.$store.commit('setSideNavigation', value);
      },
      homeIconClicked() {
        this.$router.push(this.determineHomeLink);
      },
      stayLogInButtonClicked() {
        this.$store.dispatch('stayLogIn');
      },
      logoutButtonClicked() {
        this.logout()
            .then(() => {
              this.$store.commit(MUTATIONS.SET_LOADING, false);
              this.$store.commit('setJwtAccessTokenExpired', false);
            });
      }
    },
    computed: {
      ...mapGetters([
        'getSideNavigation',
        'isLogged',
        'getPrincipalUserFirstName',
        'isJwtAccessTokenExpired',
        'isLoading'
      ]),
      menuItems() {
        if (this.isLogged) {
          return [
            // FOR NOW THERE ARE ONLY "LOGOUT", "LANGUAGE" AND "PRINCIPAL USER (AVATAR)" BUTTONS
          ]
        } else {
          return [
            {
              icon: 'face',
              title: 'sign-up-menu-button',
              link: componentsDetails.registrationForm.path
            },
            {
              icon: 'lock_open',
              title: 'sign-in-menu-button',
              link: componentsDetails.loginForm.path
            },
            {
              icon: 'person',
              title: 'about-me-menu-button',
              link: componentsDetails.aboutMe.path
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
        return this.isLogged === 1 ? componentsDetails.mainBoard.path : componentsDetails.home.path;
      },
      showHomeVisibility() {
        return this.$route.name !== 'Home' && this.$route.name !== 'MainBoard';
      }
    },
    created() {
      this.$i18n.locale = this.$store.getters.getLang;
    }
  };
</script>

<style>
  /* STYLING SCROLLBARS */
  body::-webkit-scrollbar {
    width: 2.5px;
    height: 2.5px;
  }

  body::-webkit-scrollbar-track {
    background-color: #303030;
    -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.8);
  }

  body::-webkit-scrollbar-corner {
    background-color: #303030;
  }

  body::-webkit-scrollbar-thumb {
    background-color: #0097A7;
    border-radius: 25px;
  }
  /* STYLING SCROLLBARS */

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
    transition: opacity .35s;
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

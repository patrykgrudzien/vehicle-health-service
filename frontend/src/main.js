import Vue             from 'vue';
import VueRouter       from 'vue-router';
import allRoutes       from './router/allRoutes';
import axios           from 'axios';
import VueAxios        from 'vue-axios';
import App             from './App';
import store           from './store/store';
import Vuetify         from 'vuetify';
import 'vuetify/dist/vuetify.min.css';
import MyAlert         from './shared/MyAlert';
import MyDialog        from './shared/MyDialog';
import LoadingDialog        from './shared/LoadingDialog';
import MySnackbar        from './shared/MySnackbar';
import i18n            from './lang/i18n';
import cookieHelper    from './cookieHelper';
import NavigationGuard from './router/NavigationGuard';
import appSettings from './appSettings';

// AXIOS settings
if (appSettings && appSettings.deploymentModes[0].PROD.active) {
  console.log(appSettings.deploymentModes[0].PROD.description);
  // apply plugin
  Vue.use(VueAxios, axios);
  // apply plugin
} else if (appSettings && appSettings.deploymentModes[1].DEV.active) {
  console.log(appSettings.deploymentModes[1].DEV.description);
  // apply plugin
  Vue.use(VueAxios, axios.create({
    baseURL: `http://localhost:8088`,
    headers: {
      'Access-Control-Allow-Origin': 'http://localhost:8080'
    }
  }));
  // apply plugin
}

// Interceptor for HTTP requests (adding Authorization header with JWT accessToken to each request)
Vue.axios.interceptors.request.use(config => {
  if (localStorage.getItem('access_token')) {
    config.headers.Authorization = 'Bearer ' + localStorage.getItem("access_token");
  }
  if (cookieHelper.getCookie('lang') !== '') {
    config.headers.Language = cookieHelper.getCookie('lang');
  }
  return config;
}, function (error) {
  return Promise.reject(error);
});

// --------- ROUTER ---------
Vue.use(VueRouter);

export const myRouter = new VueRouter({
  routes: allRoutes,
  mode: 'history'
});

// --------- NAVIGATION GUARD ---------
myRouter.beforeEach(NavigationGuard);

// --------- VUETIFY ---------
import colors from 'vuetify/es5/util/colors';

Vue.use(Vuetify, {
  theme: {
    primary: colors.cyan.darken2,
    secondary: colors.deepOrange.darken4,
    footer: colors.grey.darken4,
    background: '#303030'
  }
});

// --------- REGISTER GLOBAL "MyAlert" component ---------
Vue.component('my-alert', MyAlert);
// --------- REGISTER GLOBAL "MyDialog" component ---------
Vue.component('my-dialog', MyDialog);
// --------- REGISTER GLOBAL "LoadingDialog" component ---------
Vue.component('loading-dialog', LoadingDialog);
// --------- REGISTER GLOBAL "MySnackbar" component ---------
Vue.component('my-snackbar', MySnackbar);

export function getMessageFromLocale(key) {
  return `${i18n.getLocaleMessage(i18n.locale)[key]}`
}

// --------- EVENT BUS ---------
export const eventBus = new Vue();

// --------- APP ---------
export const app = new Vue({
  el: '#app',
  router: myRouter,
  store,
  i18n,
  components: {App},
  template: '<App/>'
});

import Vue       from 'vue';
import VueRouter from 'vue-router';
import allRoutes from './router/allRoutes';
import axios     from 'axios';
import VueAxios  from 'vue-axios';
import App       from './App';
import store     from './store/store';
import Vuetify   from 'vuetify';
import 'vuetify/dist/vuetify.min.css';

// --------- AXIOS ---------
// PRODUCTION
// Vue.use(VueAxios, axios);

// DEVELOPMENT
Vue.use(VueAxios, axios.create({
  baseURL: `http://localhost:8088`,
  headers: {
    'Access-Control-Allow-Origin': 'http://localhost:8080'
  }
}));

// Interceptor for HTTP requests (adding Authorization header with JWT token to each request)
Vue.axios.interceptors.request.use(config => {
  if (localStorage.getItem('token')) {
    config.headers.Authorization = 'Bearer ' + localStorage.getItem("token");
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

// --------- APP ---------
new Vue({
  el: '#app',
  router: myRouter,
  store,
  components: {App},
  template: '<App/>'
});

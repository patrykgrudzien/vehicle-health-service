import Vue from 'vue';
import VueRouter from 'vue-router';
import allRoutes from './router/allRoutes';
import axios from 'axios';
import VueAxios from 'vue-axios';
import BootstrapVue from 'bootstrap-vue';
// Import the styles directly (they can be added via script tags alternatively)
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
import App from './App';

// noinspection JSUnresolvedFunction
Vue.use(VueRouter);
Vue.use(VueAxios, axios.create({
  baseURL: `http://localhost:8088`,
  headers: {
    'Access-Control-Allow-Origin': 'http://localhost:8080'
  }
}));
Vue.use(BootstrapVue);

const myRouter = new VueRouter({
  routes: allRoutes,
  mode: 'history'
});

new Vue({
  el: '#app',
  router: myRouter,
  components: {App},
  template: '<App/>'
});

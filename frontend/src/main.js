import Vue from 'vue';
import VueRouter from 'vue-router';
import allRoutes from './router/allRoutes'
import App from './App';

Vue.use(VueRouter);

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

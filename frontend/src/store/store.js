import Vue from 'vue';
import Vuex from 'vuex';
import getters from './getters';
import actions from './actions';
import mutations from './mutations';

import createPersistedState from 'vuex-persistedstate';
import * as Cookies from 'js-cookie';

Vue.use(Vuex);

const state = {
  logged: localStorage.getItem('token'),
  loading: false,
  serverError: null,
  lang: 'en'
};

export default new Vuex.Store({
  state,
  getters,
  actions,
  mutations,
  plugins: [
    createPersistedState({
      getState: () => {
        if (Cookies.getJSON('lang')) {
          state.lang = Cookies.getJSON('lang');
        } else {
          state.lang = 'en';
        }
      },
      setState: () => {
        Cookies.set('lang', state.lang);
      }
    })
  ]
});

import Vue from 'vue';
import Vuex from 'vuex';
Vue.use(Vuex);

import getters from './getters';
import actions from './actions';
import mutations from './mutations';

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
  mutations
})

import Vue from 'vue';
import Vuex from 'vuex';
import { myRouter } from '../main';

Vue.use(Vuex);

const types = {
  LOGIN: 'LOGIN',
  LOGOUT: 'LOGOUT'
};

const state = {
  logged: localStorage.getItem('token')
};

const getters = {
  // this function takes state as input argument and returns "logged" property of "state" object
  isLogged: state => state.logged
};

const actions = {
  login({commit}, credentials) {
    Vue.axios.post(`/auth`, credentials)
      .then(response => {
        console.log('dispatched to >>>> store >>>> login action');
        localStorage.setItem('token', response.data.token);
        commit(types.LOGIN);
        myRouter.push({path: '/server-health'});
      })
  },

  logout({commit}) {
    console.log('logout button >>>> clicked');
    localStorage.removeItem('token');
    commit(types.LOGOUT);
    myRouter.push({path: '/login'});
  }
};

const mutations = {
  [types.LOGIN](state) {
    state.logged = 1;
  },

  [types.LOGOUT](state) {
    state.logged = 0;
  }
};

export default new Vuex.Store({
  state,
  getters,
  actions,
  mutations
})

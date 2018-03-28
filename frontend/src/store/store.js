import Vue from 'vue';
import Vuex from 'vuex';

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
        localStorage.setItem('token', response.data.token);
      })
      .catch(error => {
        console.log(error);
      })
  },

  logout({commit}) {
    // TODO: implement server side /logout endpoint
    localStorage.removeItem('token');
    commit(types.LOGOUT);
  }
};

const mutations = {
  [types.LOGIN] (state) {
    state.logged = 1;
  },

  [types.LOGOUT] (state) {
    state.logged = 0;
  }
};

export default new Vuex.Store({
  state,
  getters,
  actions,
  mutations
})

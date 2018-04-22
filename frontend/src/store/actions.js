import Vue from 'vue';
import {myRouter} from '../main';
import types from './types';
import serverEndpoints from '../serverEndpoints';
import componentsPaths from '../componentsPaths';
import {app} from '../main';

export default {

  registerUserAccount({commit}, form) {
    commit('setLoading', true);
    commit('clearServerError');
    commit('clearServerResponse');
    Vue.axios.post(serverEndpoints.registration.registerUserAccount, form)
      .then(response => {
        commit('setLoading', false);
        commit('setServerResponse', response.data);
      })
      .catch(error => {
        commit('setLoading', false);
        commit('setServerError', error.response.data);
        commit('setServerResponse', null);
      })
  },

  login({commit}, credentials) {
    commit('setLoading', true);
    commit('clearServerError');
    Vue.axios.post(serverEndpoints.authentication.root, credentials)
      .then(response => {
        commit('setLoading', false);
        if (response.data.errorMessage) {
          console.log(response.data.errorMessage);
          commit('setServerError', response.data.errorMessage);
          myRouter.push({path: componentsPaths.loginFailed});
        } else {
          localStorage.setItem('token', response.data.token);
          commit(types.LOGIN);
          commit('clearServerError');
          myRouter.push({path: componentsPaths.serverCheck});
        }
      })
  },

  logout({commit}) {
    localStorage.removeItem('token');
    commit(types.LOGOUT);
    myRouter.push({path: componentsPaths.logoutSuccessful});
  },

  clearServerError({commit}) {
    commit('clearServerError');
  },

  clearServerResponse({commit}) {
    commit('clearServerResponse');
  },

  setLang({commit}, payload) {
    commit(types.SET_LANG, payload);
  }
}

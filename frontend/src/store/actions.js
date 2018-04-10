import Vue from 'vue';
import {myRouter} from '../main';
import types from './types';
import serverEndpoints from '../serverEndpoints';

export default {

  registerUserAccount({commit}, form) {
    commit('setLoading', true);
    commit('clearServerError');
    Vue.axios.post(serverEndpoints.registration.registerUserAccount, form)
      .then(response => {
        commit('setLoading', false);
        console.log(response.data);
      })
      .catch(error => {
        commit('setLoading', false);
        commit('setServerError', error.data);
        console.log(error.data);
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
          myRouter.push({path: '/login?failed=true'});
        } else {
          localStorage.setItem('token', response.data.token);
          commit(types.LOGIN);
          // reset store -> do not want to store incorrect values (user has been successfully logged in)
          commit('clearServerError');
          // /server-health comes from allRoutes.js (router)
          myRouter.push({path: '/server-health'});
        }
      })
  },

  logout({commit}) {
    localStorage.removeItem('token');
    commit(types.LOGOUT);
    myRouter.push({path: '/logout?successful=true'});
  },

  clearServerError({commit}) {
    commit('clearServerError');
  },

  setLang({commit}, payload) {
    commit(types.SET_LANG, payload);
  }
}

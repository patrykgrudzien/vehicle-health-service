import Vue from 'vue';
import {myRouter} from '../main';
import types from './types';
import serverEndpoints from '../serverEndpoints';
import componentsPaths from '../componentsPaths';

export default {

  registerUserAccount({commit}, form) {
    commit('setLoading', true);
    commit('clearServerExceptionResponse');
    commit('clearServerSuccessResponse');
    Vue.axios.post(serverEndpoints.registration.registerUserAccount, form)
      .then(response => {
        commit('setLoading', false);
        commit('setServerSuccessResponse', response.data);
      })
      .catch(error => {
        if (!error.response) {
          commit('setServerRunning', false);
          commit('setLoading', false);
        } else {
          commit('setLoading', false);
          commit('setServerExceptionResponse', error.response.data);
          commit('clearServerSuccessResponse');
        }
      });
  },

  login({commit}, credentials) {
    commit('setLoading', true);
    commit('clearServerExceptionResponse');
    Vue.axios.post(serverEndpoints.authentication.root, credentials)
      .then(response => {
        commit('setLoading', false);
        if (response.data.message) {
          commit('setServerExceptionResponse', response.data);
          myRouter.push({path: componentsPaths.loginFailed});
        } else {
          localStorage.setItem('token', response.data.token);
          commit(types.LOGIN);
          commit('clearServerExceptionResponse');
          myRouter.push({path: componentsPaths.mainBoard});
        }
      })
      .catch(error => {
        if (!error.response) {
          commit('setServerRunning', false);
          commit('setLoading', false);
        } else {
          commit('setLoading', false);
          commit('setServerExceptionResponse', error.response.data);
          commit('clearServerSuccessResponse');
        }
      });
  },

  logout({commit}) {
    localStorage.removeItem('token');
    commit(types.LOGOUT);
    myRouter.push({path: componentsPaths.logoutSuccessful});
  },

  setServerRunning({commit}, payload) {
    commit('setServerRunning', payload);
  },

  clearServerExceptionResponse({commit}) {
    commit('clearServerExceptionResponse');
  },

  clearServerSuccessResponse({commit}) {
    commit('clearServerSuccessResponse');
  },

  setLang({commit}, payload) {
    commit(types.SET_LANG, payload);
  }
}

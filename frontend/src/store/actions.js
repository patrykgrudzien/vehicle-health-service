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
        window.scrollTo(0, 0);
      })
      .catch(error => {
        if (!error.response) {
          commit('setServerRunning', false);
          commit('setLoading', false);
          window.scrollTo(0, 0);
        } else {
          commit('setLoading', false);
          commit('setServerExceptionResponse', error.response.data);
          commit('clearServerSuccessResponse');
          window.scrollTo(0, 0);
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
          window.scrollTo(0, 0);
        } else {
          localStorage.setItem('token', response.data.token);
          commit(types.LOGIN);
          commit('clearServerExceptionResponse');
          myRouter.push({path: componentsPaths.mainBoard});
          window.scrollTo(0 ,0);
        }
      })
      .catch(error => {
        if (!error.response) {
          commit('setServerRunning', false);
          commit('setLoading', false);
          window.scrollTo(0, 0);
        } else {
          commit('setLoading', false);
          commit('setServerExceptionResponse', error.response.data);
          commit('clearServerSuccessResponse');
          window.scrollTo(0, 0);
        }
      });
  },

  logout({commit}) {
    localStorage.removeItem('token');
    localStorage.removeItem('principalFirstName');
    commit(types.LOGOUT);
    commit('clearPrincipalFirstName');
    commit('clearServerExceptionResponse');
    commit('clearServerSuccessResponse');
    myRouter.push({path: componentsPaths.logoutSuccessful});
    window.scrollTo(0, 0);
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
  },

  setDialogTextFieldData({commit}, payload) {
    commit('setDialogTextFieldData', payload);
  },

  setPrincipalFirstName({commit}, payload) {
    commit('setPrincipalFirstName', payload);
  }
}

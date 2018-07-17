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
    Vue.axios.post(serverEndpoints.registration.registerUserAccount, {
      firstName: form.firstName,
      lastName: form.lastName,
      email: form.email,
      confirmedEmail: form.confirmedEmail,
      password: window.btoa(form.password),
      confirmedPassword: window.btoa(form.confirmedPassword)
    })
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
    Vue.axios.post(serverEndpoints.authentication.root, {
      email: window.btoa(credentials.email),
      password: window.btoa(credentials.password)
    })
       .then(response => {
         commit('setLoading', false);
         if (response.data.message) {
           commit('setServerExceptionResponse', response.data);
           myRouter.push({path: componentsPaths.loginFailed});
           window.scrollTo(0, 0);
         } else {
           localStorage.setItem('access_token', response.data.accessToken);
           localStorage.setItem('refresh_token', response.data.refreshToken);
           commit(types.LOGIN);
           commit('clearServerExceptionResponse');
           commit('clearLoginForm');
           myRouter.push({path: componentsPaths.mainBoard});
           window.scrollTo(0 ,0);
         }
       })
       .catch(error => {
         if (!error.response) {
           commit('setServerRunning', false);
           commit('setLoading', false);
           commit('clearPrincipalUserFirstName');
           window.scrollTo(0, 0);
         } else {
           commit('setLoading', false);
           commit('setServerExceptionResponse', error.response.data);
           commit('clearServerSuccessResponse');
           commit('clearPrincipalUserFirstName');
           window.scrollTo(0, 0);
         }
       });
    },

  logout({commit}) {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    commit(types.LOGOUT);
    commit('clearServerExceptionResponse');
    commit('clearServerSuccessResponse');
    commit('clearPrincipalUserFirstName');
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
    commit('clearServerExceptionResponse');
    commit('clearServerSuccessResponse');
  },

  stayLogIn({commit}) {
    Vue.axios.post(serverEndpoints.authentication.refreshToken, {
      refreshToken: localStorage.getItem('refresh_token')
    })
      .then((response) => {
        commit('setJwtTokenExpired', false);
        localStorage.setItem('access_token', response.data.accessToken);
        window.scrollTo(0, 0);
      })
      .catch(() => {
        commit('setJwtTokenExpired', true);
        window.scrollTo(0, 0);
      });
  },

  getPrincipalUserFirstName({commit}) {
    // returning response to component which calls this action
    return Vue.axios.get(serverEndpoints.authentication.principalUser)
              .then(response => {
                commit('setLoading', false);
                commit('setPrincipalUserFirstName', response.data.firstname);
                window.scrollTo(0, 0);
                // returning response to component which calls this action
                return response;
              });
  },

  getCurrentMileage({commit}, pathVariable) {
    // returning response to component which calls this action
    return Vue.axios.get(`${serverEndpoints.vehiclesController.getCurrentMileage}/${pathVariable}`)
              .then(response => {
                commit('setLoading', false);
                commit('setServerSuccessResponse', response.data);
                window.scrollTo(0, 0);
                // returning response to component which calls this action
                return response;
              });
  }
}

import types from './types';
import {app} from '../main';

export default {
  [types.LOGIN](state) {
    state.logged = 1;
  },

  [types.LOGOUT](state) {
    state.logged = 0;
  },

  setLoading: (state, payload) => {
    state.loading = payload;
  },

  setServerRunning: (state, payload) => {
    state.serverRunning = payload;
  },

  setServerExceptionResponse: (state, payload) => {
    state.serverExceptionResponse = payload;
  },

  setServerSuccessResponse: (state, payload) => {
    state.serverSuccessResponse = payload;
  },

  clearServerExceptionResponse: state => {
    state.serverExceptionResponse = null;
  },

  clearServerSuccessResponse: state => {
    state.serverSuccessResponse = null;
  },

  [types.SET_LANG](state, payload) {
    app.$i18n.locale = payload;
    state.lang = payload;
  },

  setLoginFormEmail(state, payload) {
    state.loginForm.email = payload;
  },

  setLoginFormPassword(state, payload) {
    state.loginForm.password = payload;
  },

  setLoginFormValid(state, payload) {
    state.loginForm.valid = payload;
  },

  setSideNavigation(state, payload) {
    state.sideNavigation = payload;
  }
}

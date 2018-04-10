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

  setServerError: (state, payload) => {
    state.serverError = payload;
  },

  clearServerError: state => {
    state.serverError = null;
  },

  [types.SET_LANG](state, payload) {
    app.$i18n.locale = payload;
    state.lang = payload;
  }
}

import types from './types';

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
  }
}

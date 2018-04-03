import types from './types';

export default {
  [types.LOGIN](state) {
    state.logged = 1;
  },

  [types.LOGOUT](state) {
    state.logged = 0;
  },

  setErrorMessageFromServer: (state, errorMessage) => {
    state.errorMessageFromServer = errorMessage;
  },

  setErrorMessageFromServerExistence: (state, isErrorMessageExist) => {
    state.errorMessageFromServerExist = isErrorMessageExist;
  }
}

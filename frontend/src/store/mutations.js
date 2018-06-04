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

  // -------------- LOGIN FORM -------------- //
  setLoginFormEmail(state, payload) {
    state.loginForm.email = payload;
  },

  setLoginFormPassword(state, payload) {
    state.loginForm.password = payload;
  },

  setLoginFormValid(state, payload) {
    state.loginForm.valid = payload;
  },
  // -------------- LOGIN FORM -------------- //

  setSideNavigation(state, payload) {
    state.sideNavigation = payload;
  },

  // -------------- REGISTRATION FORM -------------- //
  setRegistrationFormFirstName(state, payload) {
    state.registrationForm.firstName = payload;
  },
  setRegistrationFormLastName(state, payload) {
    state.registrationForm.lastName = payload;
  },
  setRegistrationFormEmail(state, payload) {
    state.registrationForm.email = payload;
  },
  setRegistrationFormConfirmedEmail(state, payload) {
    state.registrationForm.confirmedEmail = payload;
  },
  setRegistrationFormPassword(state, payload) {
    state.registrationForm.password = payload;
  },
  setRegistrationFormConfirmedPassword(state, payload) {
    state.registrationForm.confirmedPassword = payload;
  },
  setRegistrationFormValid(state, payload) {
    state.registrationForm.valid = payload;
  },
  // -------------- REGISTRATION FORM -------------- //

  setDialogTextFieldData(state, payload) {
    state.dialogTextFieldData = payload;
  },
  setPrincipalFirstName(state, payload) {
    state.principalFirstName = payload;
  },
  clearPrincipalFirstName(state) {
    state.principalFirstName = null;
  }
}

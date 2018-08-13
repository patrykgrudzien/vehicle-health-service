import {MUTATIONS} from '../Constants';
import {app}       from '../main';

export default {

  [MUTATIONS.LOGIN] (state) {
    state.logged = 1;
  },

  [MUTATIONS.LOGOUT] (state) {
    state.logged = 0;
  },

  [MUTATIONS.SET_LOADING] (state, payload) {
    state.loading = payload;
  },

  [MUTATIONS.SET_SERVER_RUNNING] (state, payload) {
    state.serverRunning = payload;
  },

  setServerExceptionResponse: (state, payload) => {
    state.serverExceptionResponse = payload;
  },

  setServerSuccessResponse: (state, payload) => {
    state.serverSuccessResponse = payload;
  },

  [MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE] (state) {
    state.serverExceptionResponse = null;
  },

  clearServerSuccessResponse: state => {
    state.serverSuccessResponse = null;
  },

  [MUTATIONS.SET_LANG](state, payload) {
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

  clearLoginForm(state) {
    state.loginForm.email = '';
    state.loginForm.password = '';
    state.loginForm.valid = true;
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

  // -------------- PRINCIPAL USER FIRST NAME -------------- //
  setPrincipalUserFirstName(state, payload) {
    state.principalUserFirstName = payload;
  },
  clearPrincipalUserFirstName(state) {
    state.principalUserFirstName = null;
  },

  setLoginUser(state, payload) {
    state.loginUser = payload;
  },
  clearLoginUser(state) {
    state.loginUser = null;
  },
  // -------------- PRINCIPAL USER FIRST NAME -------------- //

  // -------------- JWT TOKEN EXPIRED -------------- //
  setJwtAccessTokenExpired(state, payload) {
    state.jwtAccessTokenExpired = payload;
  },
  // -------------- JWT TOKEN EXPIRED -------------- //

  setLastRequestedPath(state, payload) {
    state.lastRequestedPath = payload;
  },
  clearLastRequestedPath(state) {
    state.lastRequestedPath = null;
  },
  setLastRequestMethod(state, payload) {
    state.lastRequestMethod = payload;
  },
  clearLastRequestMethod(state) {
    state.lastRequestMethod = null;
  }
}

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

  [MUTATIONS.SET_SERVER_EXCEPTION_RESPONSE] (state, payload) {
    state.serverExceptionResponse = payload;
  },

  [MUTATIONS.SET_SERVER_SUCCESS_RESPONSE] (state, payload) {
    state.serverSuccessResponse = payload;
  },

  [MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE] (state) {
    state.serverExceptionResponse = null;
  },

  [MUTATIONS.CLEAR_SERVER_SUCCESS_RESPONSE] (state) {
    state.serverSuccessResponse = null;
  },

  [MUTATIONS.SET_LANG](state, payload) {
    app.$i18n.locale = payload;
    state.lang = payload;
  },

  // -------------- LOGIN FORM -------------- //
  [MUTATIONS.SET_LOGIN_FORM_EMAIL] (state, payload) {
    state.loginForm.email = payload;
  },

  [MUTATIONS.SET_LOGIN_FORM_PASSWORD] (state, payload) {
    state.loginForm.password = payload;
  },

  [MUTATIONS.SET_LOGIN_FORM_VALID] (state, payload) {
    state.loginForm.valid = payload;
  },

  [MUTATIONS.CLEAR_LOGIN_FORM] (state) {
    state.loginForm.email = '';
    state.loginForm.password = '';
    state.loginForm.valid = true;
  },
  // -------------- LOGIN FORM -------------- //

  [MUTATIONS.SET_SIDE_NAVIGATION] (state, payload) {
    state.sideNavigation = payload;
  },

  // -------------- REGISTRATION FORM -------------- //
  [MUTATIONS.SET_REGISTRATION_FORM_FIRST_NAME] (state, payload) {
    state.registrationForm.firstName = payload;
  },
  [MUTATIONS.SET_REGISTRATION_FORM_LAST_NAME] (state, payload) {
    state.registrationForm.lastName = payload;
  },
  [MUTATIONS.SET_REGISTRATION_FORM_EMAIL] (state, payload) {
    state.registrationForm.email = payload;
  },
  [MUTATIONS.SET_REGISTRATION_FORM_CONFIRMED_EMAIL] (state, payload) {
    state.registrationForm.confirmedEmail = payload;
  },
  [MUTATIONS.SET_REGISTRATION_FORM_PASSWORD] (state, payload) {
    state.registrationForm.password = payload;
  },
  [MUTATIONS.SET_REGISTRATION_FORM_CONFIRMED_PASSWORD] (state, payload) {
    state.registrationForm.confirmedPassword = payload;
  },
  [MUTATIONS.SET_REGISTRATION_FORM_VALID] (state, payload) {
    state.registrationForm.valid = payload;
  },
  // -------------- REGISTRATION FORM -------------- //

  // -------------- PRINCIPAL USER FIRST NAME -------------- //
  [MUTATIONS.SET_PRINCIPAL_USER_FIRST_NAME] (state, payload) {
    state.principalUserFirstName = payload;
  },
  [MUTATIONS.CLEAR_PRINCIPAL_USER_FIRST_NAME] (state) {
    state.principalUserFirstName = null;
  },

  [MUTATIONS.SET_LOGIN_USER] (state, payload) {
    state.loginUser = payload;
  },
  [MUTATIONS.CLEAR_LOGIN_USER] (state) {
    state.loginUser = null;
  },
  // -------------- PRINCIPAL USER FIRST NAME -------------- //

  // -------------- JWT TOKEN EXPIRED -------------- //
  [MUTATIONS.SET_JWT_ACCESS_TOKEN_EXPIRED] (state, payload) {
    state.jwtAccessTokenExpired = payload;
  },
  // -------------- JWT TOKEN EXPIRED -------------- //

  [MUTATIONS.SET_LAST_REQUESTED_PATH] (state, payload) {
    state.lastRequestedPath = payload;
  },
  [MUTATIONS.CLEAR_LAST_REQUESTED_PATH] (state) {
    state.lastRequestedPath = null;
  },
  [MUTATIONS.SET_LAST_REQUEST_METHOD] (state, payload) {
    state.lastRequestMethod = payload;
  },
  [MUTATIONS.CLEAR_LAST_REQUEST_METHOD] (state) {
    state.lastRequestMethod = null;
  }
}

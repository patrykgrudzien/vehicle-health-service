const CONTEXT_PATHS = {
  UI: '/ui',
  API: '/api'
};

const DEPLOYMENT_MODES = {
  PROD: 'PROD',
  DEV: 'DEV'
};

const mutationSuffix = '_mutation';
const MUTATIONS = {
  LOGIN: 'login' + mutationSuffix,
  LOGOUT: 'logout' + mutationSuffix,
  SET_LANG: 'setLang' + mutationSuffix,
  SET_LOADING: 'setLoading' + mutationSuffix,
  SET_SERVER_RUNNING: 'setServerRunning' + mutationSuffix,
  CLEAR_SERVER_EXCEPTION_RESPONSE: 'clearServerExceptionResponse' + mutationSuffix,
  SET_SERVER_EXCEPTION_RESPONSE: 'setServerExceptionResponse' + mutationSuffix,
  SET_SERVER_SUCCESS_RESPONSE: 'setServerSuccessResponse' + mutationSuffix,
  CLEAR_SERVER_SUCCESS_RESPONSE: 'clearServerSuccessResponse' + mutationSuffix,
  SET_LOGIN_FORM_EMAIL: 'setLoginFormEmail' + mutationSuffix,
  SET_LOGIN_FORM_PASSWORD: 'setLoginFormPassword' + mutationSuffix,
  SET_LOGIN_FORM_VALID: 'setLoginFormValid' + mutationSuffix,
  CLEAR_LOGIN_FORM: 'clearLoginForm' + mutationSuffix,
  SET_SIDE_NAVIGATION: 'setSideNavigation' + mutationSuffix,
  SET_REGISTRATION_FORM_FIRST_NAME: 'setRegistrationFormFirstName' + mutationSuffix,
  SET_REGISTRATION_FORM_LAST_NAME: 'setRegistrationFormLastName' + mutationSuffix,
  SET_REGISTRATION_FORM_EMAIL: 'setRegistrationFormEmail' + mutationSuffix,
  SET_REGISTRATION_FORM_CONFIRMED_EMAIL: 'setRegistrationFormConfirmedEmail' + mutationSuffix,
  SET_REGISTRATION_FORM_PASSWORD: 'setRegistrationFormPassword' + mutationSuffix,
  SET_REGISTRATION_FORM_CONFIRMED_PASSWORD: 'setRegistrationFormConfirmedPassword' + mutationSuffix,
  SET_REGISTRATION_FORM_VALID: 'setRegistrationFormValid' + mutationSuffix,
  SET_PRINCIPAL_USER_FIRST_NAME: 'setPrincipalUserFirstName' + mutationSuffix,
  CLEAR_PRINCIPAL_USER_FIRST_NAME: 'clearPrincipalUserFirstName' + mutationSuffix,
  SET_LOGIN_USER: 'setLoginUser' + mutationSuffix,
  CLEAR_LOGIN_USER: 'clearLoginUser' + mutationSuffix,
  SET_JWT_ACCESS_TOKEN_EXPIRED: 'setJwtAccessTokenExpired' + mutationSuffix,
  SET_LAST_REQUESTED_PATH: 'setLastRequestedPath' + mutationSuffix,
  CLEAR_LAST_REQUESTED_PATH: 'clearLastRequestedPath' + mutationSuffix,
  SET_LAST_REQUEST_METHOD: 'setLastRequestMethod' + mutationSuffix,
  CLEAR_LAST_REQUEST_METHOD: 'clearLastRequestMethod' + mutationSuffix
};

const actionSuffix = '_action';
const ACTIONS = {
  REGISTER_USER_ACCOUNT: 'registerUserAccount' + actionSuffix,
  LOGIN: 'login' + actionSuffix,
  LOGOUT: 'logout' + actionSuffix,
  GET_CURRENT_MILEAGE: 'getCurrentMileage' + actionSuffix,
  GET_PRINCIPAL_USER_FIRST_NAME: 'getPrincipalUserFirstName' + actionSuffix,
  STAY_LOG_IN: 'stayLogIn' + actionSuffix,
  SET_LANG: 'setLang' + actionSuffix
};

const eventSuffix = '_event';
const EVENTS = {
  STAY_LOG_IN_EVENT: 'stayLogInEvent' + eventSuffix,
  OPEN_DIALOG_AND_SEND_LANG_EVENT: 'open-dialog-and-send-lang' + eventSuffix
};

export {
  DEPLOYMENT_MODES,
  MUTATIONS,
  ACTIONS,
  EVENTS,
  CONTEXT_PATHS
}

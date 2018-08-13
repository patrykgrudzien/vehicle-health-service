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
  CLEAR_SERVER_EXCEPTION_RESPONSE: 'clearServerExceptionResponse' + mutationSuffix
};

const actionSuffix = '_action';
const ACTIONS = {};

export {
  DEPLOYMENT_MODES,
  MUTATIONS,
  ACTIONS
}

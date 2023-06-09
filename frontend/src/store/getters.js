export default {
  // this function takes state as input argument and returns "logged" property of "state" object
  isLogged: state => state.logged,
  isLoading: state => state.loading,
  isServerRunning: state => state.serverRunning,
  getServerExceptionResponse: state => state.serverExceptionResponse,
  getServerSuccessResponse: state => state.serverSuccessResponse,
  getLang: state => state.lang,
  getLoginForm: state => state.loginForm,
  getRegistrationForm: state => state.registrationForm,
  getSideNavigation: state => state.sideNavigation,
  getLoginUser: state => state.loginUser,
  getPrincipalUserFirstName: state => state.principalUserFirstName,
  isJwtAccessTokenExpired: state => state.jwtAccessTokenExpired,
  getLastRequestedPath: state => state.lastRequestedPath,
  getLastRequestMethod: state => state.lastRequestMethod
}

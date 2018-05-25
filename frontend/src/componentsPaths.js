export default {
  home: '/',
  serverCheck: '/server-health',
  aboutMe: '/about-me',
  registrationForm: '/registration-form',
  // added wildcard to handle query params sent by server (they are used to display appropriate error message)
  confirmRegistration: '/registration-confirmed**',
  loginForm: '/login',
  loginFailed: '/login?failed=true',
  // /logout?successful=true
  logoutSuccessfulWildcard: '/logout**',
  logoutSuccessful: '/logout?successful=true',
  mainBoard: '/main-board'
}

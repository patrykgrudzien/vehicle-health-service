export default {
  home: {
    path: '/',
    name: 'Home'
  },
  aboutMe: {
    path: '/about-me',
    name: 'AboutMe'
  },
  registrationForm: {
    path: '/registration-form',
    name: 'RegistrationForm'
  },
  confirmRegistration: {
    // added wildcard to handle query params sent by server (they are used to display appropriate error message)
    path: '/registration-confirmed**',
    name: 'ConfirmRegistration'
  },
  loginForm: {
    path: '/login',
    name: 'LoginForm'
  },
  loginFailed: {
    path: '/login?failed=true',
    name: 'LoginForm-login-failed'
  },
  logoutSuccessfulWildcard: {
    // /logout?successful=true
    path: '/logout**',
    name: 'LoginForm-logout-successful-wildcard'
  },
  logoutSuccessful: {
    path: '/logout?successful=true'
  },
  mainBoard: {
    path: '/main-board',
    name: 'MainBoard'
  },
  engine: {
    path: '/main-board/vehicle/engine',
    name: 'Engine'
  },
  fluids: {
    path: '/main-board/vehicle/fluids',
    name: 'Fluids'
  },
  tires: {
    path: '/main-board/vehicle/tires',
    name: 'Tires'
  },
  maintenanceCosts: {
    path: '/main-board/vehicle/maintenance-costs',
    name: 'Maintenance-Costs'
  },
  authenticationRequired: {
    path: '/authentication-required',
    name: 'LoginForm-secured-resource-authentication-required'
  }
}

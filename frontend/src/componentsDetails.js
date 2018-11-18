import { CONTEXT_PATHS as contextPaths } from './Constants'

export default {
  home: {
    path: contextPaths.UI,
    name: 'Home'
  },
  aboutMe: {
    path: `${contextPaths.UI}/about-me`,
    name: 'AboutMe'
  },
  registrationForm: {
    path: `${contextPaths.UI}/registration-form`,
    name: 'RegistrationForm'
  },
  confirmRegistration: {
    // added wildcard to handle query params sent by server (they are used to display appropriate error message)
    path: `${contextPaths.UI}/registration-confirmed**`,
    name: 'ConfirmRegistration'
  },
  loginForm: {
    path: `${contextPaths.UI}/login`,
    name: 'LoginForm'
  },
  loginFailed: {
    path: `${contextPaths.UI}/login?failed=true`,
    name: 'LoginForm-login-failed'
  },
  logoutSuccessfulWildcard: {
    // /logout?successful=true
    path: `${contextPaths.UI}/logout**`,
    name: 'LoginForm-logout-successful-wildcard'
  },
  logoutSuccessful: {
    path: `${contextPaths.UI}/logout?successful=true`
  },
  mainBoard: {
    path: `${contextPaths.UI}/main-board`,
    name: 'MainBoard'
  },
  engine: {
    path: `${contextPaths.UI}/main-board/vehicle/engine`,
    name: 'Engine'
  },
  fluids: {
    path: `${contextPaths.UI}/main-board/vehicle/fluids`,
    name: 'Fluids'
  },
  tires: {
    path: `${contextPaths.UI}/main-board/vehicle/tires`,
    name: 'Tires'
  },
  maintenanceCosts: {
    path: `${contextPaths.UI}/main-board/vehicle/maintenance-costs`,
    name: 'Maintenance-Costs'
  },
  authenticationRequired: {
    path: `${contextPaths.UI}/authentication-required`,
    name: 'LoginForm-secured-resource-authentication-required'
  },
  userLoggedInUsingGoogle: {
    path: `${contextPaths.UI}/user-logged-in-using-google**`,
    name: 'User Logged In Using Google'
  },
  userRegisteredUsingGoogle: {
    path: `${contextPaths.UI}/user-registered-using-google**`,
    name: 'User Registered Using Google'
  }
}

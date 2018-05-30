import Home                from '../components/Home';
import ServerCheck         from '../components/ServerCheck';
import AboutMe             from '../components/AboutMe';
import RegistrationForm    from '../components/RegistrationForm';
import ConfirmRegistration from '../components/ConfirmRegistration';
import LoginForm           from '../components/LoginForm';
import MainBoard           from '../components/MainBoard';
import componentsPaths     from '../componentsPaths';

export default [
  {
    name: 'Home',
    path: componentsPaths.home,
    component: Home
  },
  {
    name: 'ServerCheck',
    path: componentsPaths.serverCheck,
    component: ServerCheck
  },
  {
    name: 'AboutMe',
    path: componentsPaths.aboutMe,
    component: AboutMe
  },
  {
    name: 'RegistrationForm',
    path: componentsPaths.registrationForm,
    component: RegistrationForm
  },
  {
    name: 'ConfirmRegistration',
    path: componentsPaths.confirmRegistration,
    component: ConfirmRegistration
  },
  {
    name: 'LoginForm',
    path: componentsPaths.loginForm,
    component: LoginForm
  },
  {
    name: 'LoginForm-login-failed',
    path: componentsPaths.loginFailed,
    component: LoginForm
  },
  {
    name: 'LoginForm-logout-successful-wildcard',
    path: componentsPaths.logoutSuccessfulWildcard,
    component: LoginForm
  },
  {
    name: 'LoginForm-secured-resource-authentication-required',
    path: componentsPaths.authenticationRequired,
    component: LoginForm
  },
  {
    name: 'MainBoard',
    path: componentsPaths.mainBoard,
    component: MainBoard,
    meta: {requiresAuth: true}
  }
];

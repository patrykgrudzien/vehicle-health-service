import Home                from '../components/home/Home';
import ServerCheck         from '../components/ServerCheck';
import AboutMe             from '../components/AboutMe';
import RegistrationForm    from '../components/registration/RegistrationForm';
import ConfirmRegistration from '../components/login/ConfirmRegistration';
import LoginForm           from '../components/login/LoginForm';
import MainBoard           from '../components/home/MainBoard';
import Engine              from '../components/vehicle/Engine';
import Fluids              from '../components/vehicle/Fluids';
import Tires               from '../components/vehicle/Tires';
import MaintenanceCosts    from '../components/vehicle/MaintenanceCosts';
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
  },
  {
    name: 'Engine',
    path: componentsPaths.engine,
    component: Engine,
    meta: {requiresAuth: true}
  },
  {
    name: 'Fluids',
    path: componentsPaths.fluids,
    component: Fluids,
    meta: {requiresAuth: true}
  },
  {
    name: 'Tires',
    path: componentsPaths.tires,
    component: Tires,
    meta: {requiresAuth: true}
  },
  {
    name: 'Maintenance-Costs',
    path: componentsPaths.maintenanceCosts,
    component: MaintenanceCosts,
    meta: {requiresAuth: true}
  }
];

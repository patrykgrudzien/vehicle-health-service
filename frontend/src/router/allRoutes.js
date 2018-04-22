import Home from '../components/Home';
import ServerCheck from '../components/ServerCheck';
import AboutMe from '../components/AboutMe';
import RegistrationFormVuetify from '../components/RegistrationFormVuetify';
import ConfirmRegistration from '../components/ConfirmRegistration';
import LoginFormVuetify from '../components/LoginFormVuetify';
import componentsPaths from '../componentsPaths';

export default [
  {
    path: componentsPaths.home,
    component: Home
  },
  {
    path: componentsPaths.serverCheck,
    component: ServerCheck
  },
  {
    path: componentsPaths.aboutMe,
    component: AboutMe
  },
  {
    path: componentsPaths.registrationForm,
    component: RegistrationFormVuetify
  },
  {
    path: componentsPaths.confirmRegistration,
    component: ConfirmRegistration
  },
  {
    path: componentsPaths.loginForm,
    component: LoginFormVuetify
  },
  {
    path: componentsPaths.loginFailed,
    component: LoginFormVuetify
  },
  {
    path: componentsPaths.logoutSuccessfulWildcard,
    component: LoginFormVuetify
  }
];

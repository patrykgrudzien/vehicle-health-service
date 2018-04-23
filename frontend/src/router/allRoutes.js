import Home from '../components/Home';
import ServerCheck from '../components/ServerCheck';
import AboutMe from '../components/AboutMe';
import RegistrationForm from '../components/RegistrationForm';
import ConfirmRegistration from '../components/ConfirmRegistration';
import LoginForm from '../components/LoginForm';
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
    component: RegistrationForm
  },
  {
    path: componentsPaths.confirmRegistration,
    component: ConfirmRegistration
  },
  {
    path: componentsPaths.loginForm,
    component: LoginForm
  },
  {
    path: componentsPaths.loginFailed,
    component: LoginForm
  },
  {
    path: componentsPaths.logoutSuccessfulWildcard,
    component: LoginForm
  }
];

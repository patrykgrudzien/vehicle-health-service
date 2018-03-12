import ServerCheck from '../components/ServerCheck';
import AboutMe from '../components/AboutMe';
import RegistrationForm from '../components/RegistrationForm';
import LoginForm from '../components/LoginForm';

export default [
  {
    path: '/server-health',
    component: ServerCheck
  },
  {
    path: '/about-me',
    component: AboutMe
  },
  {
    path: '/registration-form',
    component: RegistrationForm
  },
  {
    path: '/login',
    component: LoginForm
  }
];

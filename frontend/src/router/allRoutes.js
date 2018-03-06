import Home from '../components/Home';
import AboutMe from '../components/AboutMe';
import RegistrationForm from '../components/RegistrationForm';
import LoginForm from '../components/LoginForm';

export default [
  {
    path: '/',
    component: Home
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

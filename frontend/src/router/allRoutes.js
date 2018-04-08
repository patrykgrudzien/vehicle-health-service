import Home from '../components/Home';
import ServerCheck from '../components/ServerCheck';
import AboutMe from '../components/AboutMe';
import RegistrationForm from '../components/RegistrationForm';
import RegistrationFormVuetify from '../components/RegistrationFormVuetify';
import ConfirmRegistration from '../components/ConfirmRegistration';
import LoginForm from '../components/LoginForm';
import LoginFormVuetify from '../components/LoginFormVuetify';

export default [
  {
    path: '/',
    component: Home
  },
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
    component: RegistrationFormVuetify
  },
  {
    // added wildcard to handle query params sent by server (they are used to display appropriate error message)
    path: "/registration-confirmed**",
    component: ConfirmRegistration
  },
  {
    path: '/login',
    component: LoginFormVuetify
  },
  {
    path: `/login?failed=true`,
    component: LoginForm
  },
  {
    // /logout?successful=true
    path: `/logout**`,
    component: LoginForm
  }
];

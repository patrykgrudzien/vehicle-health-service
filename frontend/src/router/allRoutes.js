import Home from '../components/Home';
import AboutMe from '../components/AboutMe';
import AddTodoItem from '../components/AddTodoItem';
import Register from '../components/Register';

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
    path: '/add-todo',
    component: AddTodoItem
  },
  {
    path: '/register',
    component: Register
  }
];

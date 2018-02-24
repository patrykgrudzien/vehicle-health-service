import MainComponent from '../components/MainComponent';
import AboutMe from '../components/AboutMe';
import AddTodoItem from '../components/AddTodoItem';

export default [
  {
    path: '/main-component',
    component: MainComponent
  },
  {
    path: '/about-me',
    component: AboutMe
  },
  {
    path: '/add-todo',
    component: AddTodoItem
  }
];

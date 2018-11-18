import Home from '../components/home/Home'
import AboutMe from '../components/AboutMe'
import RegistrationForm from '../components/registration/RegistrationForm'
import ConfirmRegistration from '../components/registration/ConfirmRegistration'
import LoginForm from '../components/login/LoginForm'
import MainBoard from '../components/home/MainBoard'
import Engine from '../components/vehicle/Engine'
import Fluids from '../components/vehicle/Fluids'
import Tires from '../components/vehicle/Tires'
import MaintenanceCosts from '../components/vehicle/MaintenanceCosts'
import componentsDetails from '../componentsDetails'
import UserLoggedInUsingGoogle from '../components/oauth2/google/UserLoggedInUsingGoogle'
import UserRegisteredUsingGoogle from '../components/oauth2/google/UserRegisteredUsingGoogle'

export default [
  {
    name: componentsDetails.home.name,
    path: componentsDetails.home.path,
    component: Home
  },
  {
    name: componentsDetails.aboutMe.name,
    path: componentsDetails.aboutMe.path,
    component: AboutMe
  },
  {
    name: componentsDetails.registrationForm.name,
    path: componentsDetails.registrationForm.path,
    component: RegistrationForm
  },
  {
    name: componentsDetails.confirmRegistration.name,
    path: componentsDetails.confirmRegistration.path,
    component: ConfirmRegistration
  },
  {
    name: componentsDetails.loginForm.name,
    path: componentsDetails.loginForm.path,
    component: LoginForm
  },
  {
    name: componentsDetails.loginFailed.name,
    path: componentsDetails.loginFailed.path,
    component: LoginForm
  },
  {
    name: componentsDetails.logoutSuccessfulWildcard.name,
    path: componentsDetails.logoutSuccessfulWildcard.path,
    component: LoginForm
  },
  {
    name: componentsDetails.authenticationRequired.name,
    path: componentsDetails.authenticationRequired.path,
    component: LoginForm
  },
  /*
   * {
   // WrapperForMainBoardAndChildren
   path: componentsDetails.mainBoard.path, // after login I want to have main-board as main URL
   component: Wrapper,
   children: [
   {
   name: componentsDetails.mainBoard.name,
   path: '',
   component: MainBoard,
   meta: {requiresLoginUser: true}
   },
   {
   name: 'Engine',
   path: componentsDetails.engine,
   component: Engine,
   meta: {requiresLoginUser: true}
   }
   ]
   }
   */
  {
    name: componentsDetails.mainBoard.name,
    path: componentsDetails.mainBoard.path,
    component: MainBoard,
    meta: { requiresAuth: true }
  },
  {
    name: componentsDetails.engine.name,
    path: componentsDetails.engine.path,
    component: Engine,
    meta: { requiresAuth: true }
  },
  {
    name: componentsDetails.fluids.name,
    path: componentsDetails.fluids.path,
    component: Fluids,
    meta: { requiresAuth: true }
  },
  {
    name: componentsDetails.tires.name,
    path: componentsDetails.tires.path,
    component: Tires,
    meta: { requiresAuth: true }
  },
  {
    name: componentsDetails.maintenanceCosts.name,
    path: componentsDetails.maintenanceCosts.path,
    component: MaintenanceCosts,
    meta: { requiresAuth: true }
  },
  {
    name: componentsDetails.userLoggedInUsingGoogle.name,
    path: componentsDetails.userLoggedInUsingGoogle.path,
    component: UserLoggedInUsingGoogle
  },
  {
    name: componentsDetails.userRegisteredUsingGoogle.name,
    path: componentsDetails.userRegisteredUsingGoogle.path,
    component: UserRegisteredUsingGoogle
  }
]

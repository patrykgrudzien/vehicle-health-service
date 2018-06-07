import componentsPaths from "../componentsPaths";
import store           from "../store/store";

export default (to, from, next) => {
  if (to.path === componentsPaths.mainBoard && to.meta.requiresAuth && store.getters.isLogged === null) {
    // PRODUCTION
    next(componentsPaths.authenticationRequired);
    // DEVELOPMENT
    // next();
  }
  else if (to.path === componentsPaths.mainBoard && store.getters.getPrincipalFirstName === null && store.getters.isLogged !== null) {
    store.commit('setPrincipalFirstName', localStorage.getItem('principalFirstName'));
    next();
  }
  else {
    next();
  }
};

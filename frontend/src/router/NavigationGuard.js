import paths from "../componentsPaths";
import store from "../store/store";

export default (to, from, next) => {
  // user NOT logged in
  if (store.getters.isLogged === null) {
    if (to.path === paths.mainBoard && to.meta.requiresAuth) {
      // PRODUCTION
      next(paths.authenticationRequired);
      // DEVELOPMENT
      // next();
    }
    // must be called in other scenarios
    next();
  }
  // user LOGGED
  else if (store.getters.isLogged !== null) {
    // must be called in other scenarios
    next();
  }
};

import paths from "../componentsDetails";
import store from "../store/store";

export default (to, from, next) => {
  const { isLogged } = store.getters;

  // if (!isLogged || isLogged === null) {
  //   next(paths.authenticationRequired);
  // } else {
  //   const { getLoginUser } = store.getters;
  //   if (to.meta.requiresLoginUser && to.path !== paths.mainBoard.path && (!getLoginUser || getLoginUser === null)) {
  //     next(paths.authenticationRequired);
  //   } else {
  //     next();
  //   }
  // }

  next();
};

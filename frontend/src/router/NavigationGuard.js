import store                           from '../store/store';
import {CONTEXT_PATHS as contextPaths} from '../Constants';

export default (to, from, next) => {
  const { isLogged } = store.getters;

  if (to.path === '/') {
    console.log(`User entered incorrect path for router! Navigating to \"${contextPaths.UI}\" route which is handled by Home component.`);
    next(contextPaths.UI);
  }

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

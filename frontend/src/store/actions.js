import Vue from 'vue';
import {myRouter} from '../main';
import types from './types';
import serverEndpoints from '../serverEndpoints';

export default {
  login({commit}, credentials) {
    Vue.axios.post(serverEndpoints.authentication.root, credentials)
      .then(response => {
        if (response.data.errorMessage) {
          commit('setErrorMessageFromServerExistence', true);
          commit('setErrorMessageFromServer', response.data.errorMessage);
        } else {
          localStorage.setItem('token', response.data.token);
          commit(types.LOGIN);
          // reset store -> do not want to store incorrect values (user has been successfully logged in)
          commit('setErrorMessageFromServerExistence', false);
          commit('setErrorMessageFromServer', null);
          // /server-health comes from allRoutes.js (router)
          myRouter.push({path: '/server-health'});
        }
      })
  },

  logout({commit}) {
    localStorage.removeItem('token');
    commit(types.LOGOUT);
    myRouter.push({path: '/logout?successful=true'});
  }
}

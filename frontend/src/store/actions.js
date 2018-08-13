import Vue                  from 'vue';
import {myRouter}           from '../main';
import serverEndpoints      from '../serverEndpoints';
import componentsDetails    from '../componentsDetails';
import {eventBus}           from '../main';
import RequestDetailsHelper from '../classes/utils/RequestDetailsHelper';
import {MUTATIONS}          from '../Constants';

export default {

  registerUserAccount({commit}, form) {
    commit(MUTATIONS.SET_LOADING, true);
    commit(MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE);
    commit('clearServerSuccessResponse');
    Vue.axios.post(serverEndpoints.registration.registerUserAccount, {
      firstName: form.firstName,
      lastName: form.lastName,
      email: form.email,
      confirmedEmail: form.confirmedEmail,
      password: window.btoa(form.password),
      confirmedPassword: window.btoa(form.confirmedPassword)
    })
      .then(response => {
        commit(MUTATIONS.SET_LOADING, false);
        commit('setServerSuccessResponse', response.data);
        window.scrollTo(0, 0);
      })
      .catch(error => {
        if (!error.response) {
          commit(MUTATIONS.SET_SERVER_RUNNING, false);
          commit(MUTATIONS.SET_LOADING, false);
          window.scrollTo(0, 0);
        } else {
          commit(MUTATIONS.SET_LOADING, false);
          commit('setServerExceptionResponse', error.response.data);
          commit('clearServerSuccessResponse');
          window.scrollTo(0, 0);
        }
      });
  },

  login({commit}, credentials) {
    if (localStorage.getItem('access_token')) {
      localStorage.removeItem('access_token');
    }
    if (localStorage.getItem('refresh_token')) {
      localStorage.removeItem('refresh_token')
    }
    commit(MUTATIONS.SET_LOADING, true);
    commit(MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE);

    const login = new RequestDetailsHelper((serverEndpoints.authentication.root), 'login');

    Vue.axios.post(serverEndpoints.authentication.root, {
      email: window.btoa(credentials.email),
      password: window.btoa(credentials.password)
    })
       .then(response => {
         commit(MUTATIONS.SET_LOADING, false);
         if (response.data.message) {
           commit('setServerExceptionResponse', response.data);
           myRouter.replace(componentsDetails.loginFailed.path);
           window.scrollTo(0, 0);
         } else {
           localStorage.setItem('access_token', response.data.accessToken);
           localStorage.setItem('refresh_token', response.data.refreshToken);
           commit(MUTATIONS.LOGIN);
           commit(MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE);
           commit('clearLoginForm');
           myRouter.push({path: componentsDetails.mainBoard.path});
           window.scrollTo(0 ,0);
         }
       })
       .catch(error => {
         if (!error.response) {
           commit(MUTATIONS.SET_SERVER_RUNNING, false);
           commit(MUTATIONS.SET_LOADING, false);
           commit('clearPrincipalUserFirstName');
           window.scrollTo(0, 0);
         } else {
           commit(MUTATIONS.SET_LOADING, false);
           commit('setServerExceptionResponse', error.response.data);
           commit('clearServerSuccessResponse');
           commit('clearPrincipalUserFirstName');
           window.scrollTo(0, 0);
         }
       });
    },

  logout({commit}) {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    commit(MUTATIONS.LOGOUT);
    commit(MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE);
    commit('clearServerSuccessResponse');
    commit('clearPrincipalUserFirstName');
    commit('clearLastRequestedPath');
    commit('clearLastRequestMethod');
    myRouter.push({path: componentsDetails.logoutSuccessful.path});
    window.scrollTo(0, 0);
  },

  clearServerSuccessResponse({commit}) {
    commit('clearServerSuccessResponse');
  },

  setLang({commit}, payload) {
    commit(MUTATIONS.SET_LANG, payload);
    commit(MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE);
    commit('clearServerSuccessResponse');
  },

  stayLogIn({commit}) {
    Vue.axios.post(serverEndpoints.authentication.refreshToken, {
      refreshToken: localStorage.getItem('refresh_token')
    })
      .then((response) => {
        commit('setJwtAccessTokenExpired', false);
        localStorage.setItem('access_token', response.data.accessToken);

        // -------------------------------------------------------------------------------------------------------------
        eventBus.$emit('stayLogInEvent');
        // -------------------------------------------------------------------------------------------------------------

        window.scrollTo(0, 0);
      })
      .catch(() => {
        commit('setJwtAccessTokenExpired', true);
        window.scrollTo(0, 0);
      });
  },

  getPrincipalUserFirstName({commit}) {
    // returning response to component which calls this action
    return Vue.axios.get(serverEndpoints.authentication.principalUser)
              .then(response => {
                commit('setPrincipalUserFirstName', response.data.firstname);
                window.scrollTo(0, 0);
                // returning response to component which calls this action
                return response;
              })
              .catch(error => {
                const test = new RequestDetailsHelper(serverEndpoints.authentication.principalUser, 'getPrincipalUserFirstName');
                commit('setLastRequestedPath', error.response.data.lastRequestedPath);
                commit('setLastRequestMethod', error.response.data.lastRequestMethod);
              });
  },

  getCurrentMileage({commit}, pathVariable) {
    // returning response to component which calls this action
    return Vue.axios.get(`${serverEndpoints.vehiclesController.getCurrentMileage}/${pathVariable}`)
              .then(response => {
                commit('setServerSuccessResponse', response.data);
                window.scrollTo(0, 0);
                // returning response to component which calls this action
                return response;
              });
  }

  // updateCurrentMileage({commit}, payload) {}
}

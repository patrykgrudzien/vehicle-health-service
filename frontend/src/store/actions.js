import Vue from 'vue'
import { myRouter, eventBus } from '../main'
import serverEndpoints from '../serverEndpoints'
import componentsDetails from '../componentsDetails'
import RequestDetailsHelper from '../classes/utils/RequestDetailsHelper'
import { MUTATIONS, ACTIONS, EVENTS } from '../Constants'

export default {

  [ACTIONS.REGISTER_USER_ACCOUNT] ({ commit }, form) {
    commit(MUTATIONS.SET_LOADING, true)
    commit(MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE)
    commit(MUTATIONS.CLEAR_SERVER_SUCCESS_RESPONSE)
    Vue.axios.post(serverEndpoints.registration.registerUserAccount, {
      firstName: window.btoa(form.firstName),
      lastName: window.btoa(form.lastName),
      email: window.btoa(form.email),
      confirmedEmail: window.btoa(form.confirmedEmail),
      password: window.btoa(form.password),
      confirmedPassword: window.btoa(form.confirmedPassword)
    })
      .then(response => {
        commit(MUTATIONS.SET_LOADING, false)
        commit(MUTATIONS.SET_SERVER_SUCCESS_RESPONSE, response.data)
        window.scrollTo(0, 0)
      })
      .catch(error => {
        if (!error.response) {
          commit(MUTATIONS.SET_SERVER_RUNNING, false)
          commit(MUTATIONS.SET_LOADING, false)
          window.scrollTo(0, 0)
        } else {
          commit(MUTATIONS.SET_LOADING, false)
          commit(MUTATIONS.SET_SERVER_EXCEPTION_RESPONSE, error.response.data)
          commit(MUTATIONS.CLEAR_SERVER_SUCCESS_RESPONSE)
          window.scrollTo(0, 0)
        }
      })
  },

  performSuccessfulLoginOperations ({ commit }, response) {
    localStorage.setItem('access_token', response.data.accessToken)
    localStorage.setItem('refresh_token', response.data.refreshToken)
    commit(MUTATIONS.LOGIN)
    commit(MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE)
    commit(MUTATIONS.CLEAR_LOGIN_FORM)
    myRouter.push({ path: componentsDetails.mainBoard.path })
    window.scrollTo(0, 0)
  },

  [ACTIONS.LOGIN] ({ commit }, credentials) {
    if (localStorage.getItem('access_token')) {
      localStorage.removeItem('access_token')
    }
    if (localStorage.getItem('refresh_token')) {
      localStorage.removeItem('refresh_token')
    }
    commit(MUTATIONS.SET_LOADING, true)
    commit(MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE)

    const login = new RequestDetailsHelper((serverEndpoints.authentication.root), 'login') // eslint-disable-line

    Vue.axios.post(serverEndpoints.authentication.root, {
      email: window.btoa(credentials.email),
      password: window.btoa(credentials.password)
    })
      .then(response => {
        commit(MUTATIONS.SET_LOADING, false)
        if (response.data.message) {
          commit(MUTATIONS.SET_SERVER_EXCEPTION_RESPONSE, response.data)
          myRouter.replace(componentsDetails.loginFailed.path)
          window.scrollTo(0, 0)
        } else {
          localStorage.setItem('access_token', response.data.accessToken)
          localStorage.setItem('refresh_token', response.data.refreshToken)
          commit(MUTATIONS.LOGIN)
          commit(MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE)
          commit(MUTATIONS.CLEAR_LOGIN_FORM)
          myRouter.push({ path: componentsDetails.mainBoard.path })
          window.scrollTo(0, 0)
        }
      })
      .catch(error => {
        if (!error.response) {
          commit(MUTATIONS.SET_SERVER_RUNNING, false)
          commit(MUTATIONS.SET_LOADING, false)
          commit(MUTATIONS.CLEAR_PRINCIPAL_USER_FIRST_NAME)
          window.scrollTo(0, 0)
        } else {
          commit(MUTATIONS.SET_LOADING, false)
          commit(MUTATIONS.SET_SERVER_EXCEPTION_RESPONSE, error.response.data)
          commit(MUTATIONS.CLEAR_SERVER_SUCCESS_RESPONSE)
          commit(MUTATIONS.CLEAR_PRINCIPAL_USER_FIRST_NAME)
          window.scrollTo(0, 0)
        }
      })
  },

  [ACTIONS.LOGOUT] ({ commit }) {
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    commit(MUTATIONS.LOGOUT)
    commit(MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE)
    commit(MUTATIONS.CLEAR_SERVER_SUCCESS_RESPONSE)

    commit(MUTATIONS.CLEAR_LOGIN_USER)

    commit(MUTATIONS.CLEAR_PRINCIPAL_USER_FIRST_NAME)
    commit(MUTATIONS.CLEAR_LAST_REQUESTED_PATH)
    commit(MUTATIONS.CLEAR_LAST_REQUEST_METHOD)
    myRouter.push({ path: componentsDetails.logoutSuccessful.path })
    window.scrollTo(0, 0)
  },

  [ACTIONS.SET_LANG] ({ commit }, payload) {
    commit(MUTATIONS.SET_LANG, payload)
    commit(MUTATIONS.CLEAR_SERVER_EXCEPTION_RESPONSE)
    commit(MUTATIONS.CLEAR_SERVER_SUCCESS_RESPONSE)
  },

  [ACTIONS.STAY_LOG_IN] ({ commit }) {
    // Removing "access_token" because first SecurityFilterChain is trying to decode it but it's expired!
    localStorage.removeItem('access_token')
    Vue.axios.post(serverEndpoints.authentication.refreshToken, {
      refreshToken: localStorage.getItem('refresh_token')
    })
      .then((response) => {
        commit(MUTATIONS.SET_JWT_ACCESS_TOKEN_EXPIRED, false)
        localStorage.setItem('access_token', response.data)

        // -------------------------------------------------------------------------------------------------------------
        eventBus.$emit(EVENTS.STAY_LOG_IN_EVENT)
        // -------------------------------------------------------------------------------------------------------------

        window.scrollTo(0, 0)
      })
      .catch(() => {
        commit(MUTATIONS.SET_JWT_ACCESS_TOKEN_EXPIRED, true)
        window.scrollTo(0, 0)
      })
  },

  [ACTIONS.GET_PRINCIPAL_USER_FIRST_NAME] ({ commit }) {
    // returning response to component which calls this action
    return Vue.axios.get(serverEndpoints.authentication.principalUser)
      .then(response => {
        commit(MUTATIONS.SET_PRINCIPAL_USER_FIRST_NAME, response.data.firstname)
        window.scrollTo(0, 0)
        // returning response to component which calls this action
        return response
      })
      .catch(error => {
        const test = new RequestDetailsHelper(serverEndpoints.authentication.principalUser, 'getPrincipalUserFirstName') // eslint-disable-line
        commit(MUTATIONS.SET_LAST_REQUESTED_PATH, error.response.data.lastRequestedPath)
        commit(MUTATIONS.SET_LAST_REQUEST_METHOD, error.response.data.lastRequestMethod)
      })
  },

  [ACTIONS.GET_CURRENT_MILEAGE] ({ commit }, pathVariable) {
    // returning response to component which calls this action
    return Vue.axios.get(`${serverEndpoints.vehiclesController.getCurrentMileage}/${pathVariable}`)
      .then(response => {
        commit(MUTATIONS.SET_SERVER_SUCCESS_RESPONSE, response.data)
        window.scrollTo(0, 0)
        // returning response to component which calls this action
        return response
      })
  }

  // updateCurrentMileage({commit}, payload) {}
}

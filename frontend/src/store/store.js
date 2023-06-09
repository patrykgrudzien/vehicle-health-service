import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
import actions from './actions'
import mutations from './mutations'

import createPersistedState from 'vuex-persistedstate'
import * as Cookies from 'js-cookie'

Vue.use(Vuex)

const state = {
  logged: null,
  loading: false,
  serverRunning: true,
  serverExceptionResponse: null,
  serverSuccessResponse: null,
  lang: 'en',
  loginForm: {
    email: '',
    password: '',
    valid: true
  },
  registrationForm: {
    firstName: '',
    lastName: '',
    email: '',
    confirmedEmail: '',
    password: '',
    confirmedPassword: '',
    valid: true
  },
  sideNavigation: false,
  loginUser: null,
  principalUserFirstName: null,
  jwtAccessTokenExpired: false,
  lastRequestedPath: null,
  lastRequestMethod: null
}

export default new Vuex.Store({
  state,
  getters,
  actions,
  mutations,
  plugins: [
    createPersistedState({
      getState: () => {
        if (Cookies.getJSON('lang')) {
          state.lang = Cookies.getJSON('lang')
        } else {
          state.lang = 'pl'
        }
        if (localStorage.getItem('access_token')) {
          state.logged = 1
        }
      },
      setState: () => {
        Cookies.set('lang', state.lang)
      }
    })
  ]
})

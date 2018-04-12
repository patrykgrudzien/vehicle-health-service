export default {
  // this function takes state as input argument and returns "logged" property of "state" object
  isLogged: state => state.logged,
  isLoading: state => state.loading,
  getServerError: state => state.serverError,
  getServerResponse: state => state.serverResponse,
  getLang: state => state.lang
};

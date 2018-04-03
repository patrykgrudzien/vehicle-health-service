export default {
  // this function takes state as input argument and returns "logged" property of "state" object
  isLogged: state => state.logged,
  getErrorMessageFromServer: state => state.errorMessageFromServer,
  isErrorMessageFromServerExist: state => state.errorMessageFromServerExist
};

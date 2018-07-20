export default class {

  constructor(serverEndpoint, calledMethodName) {
    this.serverEndpoint = serverEndpoint;
    this.calledMethodName = calledMethodName;
  }

  getServerEndpoint() {
    return this.serverEndpoint;
  }

  getCalledMethodName() {
    return this.calledMethodName;
  }
}

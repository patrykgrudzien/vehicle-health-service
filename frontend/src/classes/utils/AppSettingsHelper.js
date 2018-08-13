import appSettings from '../../appSettings';

export default class {

  constructor() {
    this.appSettings = appSettings;
  }

  isModeActive(modeName) {
    let found = false;
    this.appSettings.deploymentModes.forEach(mode => {
      if (mode.name === modeName && mode.active) {
        console.log(mode.description);
        found = true;
      }
    });
    return found;
  }
}

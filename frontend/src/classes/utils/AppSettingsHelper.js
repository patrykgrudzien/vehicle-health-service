import appSettings from '../../appSettings';

export default class {

  constructor() {
    this.appSettings = appSettings;
  }

  isModeActive(modeName) {
    return this.appSettings.deploymentModes.forEach(mode => {
      if (mode.name === modeName && mode.active) {
        console.log(mode.description);
        return true;
      }
    });
  }
}

import { DEPLOYMENT_MODES } from './Constants'

export default {
  deploymentModes: [
    {
      name: DEPLOYMENT_MODES.PROD,
      active: false,
      description: 'PROD settings active - used to deploy the application to Heroku with default (axios) configuration.'
    },
    {
      name: DEPLOYMENT_MODES.DEV,
      active: true,
      description: 'DEV settings active - used during development with custom (axios) configuration.'
    }
  ]
}

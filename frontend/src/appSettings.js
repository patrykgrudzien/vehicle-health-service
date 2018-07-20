export default {
  deploymentModes: [
    {
      PROD: {
        active: false,
        description: 'PROD settings active - used to deploy the application to Heroku with default (axios) configuration.'
      }
    },
    {
      DEV: {
          active: true,
          description: 'DEV settings active - used during development with custom (axios) configuration.'
        }
    }
  ]
}

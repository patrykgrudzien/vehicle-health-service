import { CONTEXT_PATHS as contextPaths } from './Constants';

export default {
  authentication: {
    root: `${contextPaths.API}/auth`,
    principalUser: `${contextPaths.API}/principal-user`,
    refreshToken: `${contextPaths.API}/refresh-token`
  },
  registration: {
    root: `${contextPaths.API}/registration`,
    registerUserAccount: `${contextPaths.API}/registration/register-user-account`
  },
  vehiclesController: {
    getCurrentMileage: `${contextPaths.API}/vehicles/vehicle/get-current-mileage`,
    updateCurrentMileage: `${contextPaths.API}/vehicles/vehicle/update-current-mileage`
  }
}

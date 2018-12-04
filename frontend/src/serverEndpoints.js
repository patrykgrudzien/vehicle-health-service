export default {
  authentication: {
    root: `/auth`,
    principalUser: `/principal-user`,
    refreshToken: `/token/refresh-access-token`
  },
  registration: {
    root: `/registration`,
    registerUserAccount: `/registration/register-user-account`
  },
  vehiclesController: {
    getCurrentMileage: `/vehicles/vehicle/get-current-mileage`,
    updateCurrentMileage: `/vehicles/vehicle/update-current-mileage`
  }
}

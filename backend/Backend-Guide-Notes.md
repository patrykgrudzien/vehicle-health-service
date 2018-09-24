## General guide (notes) for backend module

#### 1. Security
#### 2. Filters
#### 3. Controllers
#### 4. Services
#### 5. Repositories
#### 6. Exception Handlers
#### 7. App Flows
* **_OAuth2 & OpenID Connect (Spring Security & Vue.js)_**
    * **_STEP 1_**
        * User presses a "Social Sign On" button -> "{baseUrl}/oauth2/authorization/google"
    * **_STEP 2 - Spring Security Intercepts the Above Request_**
        * Spring Security has a [OAuth2AuthorizationRequestRedirectFilter](), which intercepts requests of the pattern /oauth2/authorization/* and does the following:
            * Creates an  [OAuth2AuthorizationRequest]() object
            * Stores that using a configured [AuthorizationRequestRepository]()
            * Redirects the browser to the provider's [authorization-uri]() page (e.g. https://accounts.google.com/o/oauth2/v2/auth), with a few parameters. Among the parameters would be a callback URL (e.g. https://api.example.com/login/oauth2/code/google)
    * **_STEP 3 - User Logs in to the Provider and Approves Our Application_**
        * 
    * **_STEP 4 - Spring Security Intercepts the Above Request_**<br>
        In the backend, Spring's [OAuth2LoginAuthenticationFilter]() intercepts the above request (e.g. https://api.example.com/login/oauth2/code/google), and:
        * Receives the parameters, e.g. <b>accessToken</b> and <b>state</b>.
        * Retrieves the [OAuth2AuthorizationRequest]() saved earlier, using the configured AuthorizationRequestRepository discussed earlier, and does things like matching the state.
        * Calls a configured [OAuth2UserService]() implementation to retrieve the user information (email etc.) by calling the user-info-uri of the provider (e.g. https://www.googleapis.com/oauth2/v3/userinfo).
        * Authenticates the user.
        * Clears the stored [OAuth2AuthorizationRequest]().
        * Redirects the user to a configured <b>success-url</b>. You may be thinking that you could configure the success-url to be the homepage of your front-end, but that won’t work with a stateless backend.
    * **_STEP 5 - Front-End Uses the Short-Lived Token for Fetching User Information and a Long-Lived Token_**<br>
        After getting a nonce or short-lived token in the above step, the front-end then:
        * Calls your backend to fetch the user information and a long-lived token.
        * Updates the user information in the front-end models.
        * Stores the long-lived token in the local storage.
        * Closes the modal browser window.
        <br><br>
        **My Implementation:**
            * [CustomOAuth2UserService]() uses [OAuth2FlowDelegator.]()[determineFlowAndPreparePrincipal()]() to proceed with Google or Facebook flow, following with **login** / **register** logic which returns
            [CustomOAuth2OidcPrincipalUser]() principal to [CustomOidcUserService]().
            * If all code above went successfully, [CustomOAuth2AuthenticationSuccessHandler]() is called which returns **target url** with **shortLivedAuthToken**.
            * [GoogleOAuth2Controller]() intercepts such request and redirects user to [GoogleRedirectionSuccessful.vue]() component.
            * That component makes post request with **Authorization** header and **shortLivedAuthToken** with **Baerer** prefix.
            * [JwtAuthorizationTokenFilter]() filters that request because [SecurityConfig]() **does not** permit such path and [SecurityContextHolder]() is populated after token validation.
            * [GoogleOAuth2Controller]() is called at the end to exchange **shortLivedAuthToken** and return some additional data.
             
        

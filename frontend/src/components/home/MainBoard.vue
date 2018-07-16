<template>
  <v-container fluid
               fill-height
               grid-list-xl>
    <v-layout align-center
              justify-center
              row
              wrap>
      <!-- MILEAGE FLEX -->
      <v-flex xs12 sm12 md12 lg12 xl12 class="mb-0 mt-0 pb-0 pt-0">
        <!-- MILEAGE TITLE, CURRENT STATE, KM -->
        <v-layout align-center
                  justify-center
                  v-bind="rowColumnDeterminer"
                  wrap>
          <v-flex :class="mileageTitleClasses">
            <span class="headline notSelectable">
              {{ $t('current-mileage-title') }}
            </span>
          </v-flex>
          <v-flex :class="mileageValueClasses">
            <span v-if="isLoading === false"
                  id="mileage-field"
                  class="headline primary--text notSelectable"
                  @click="toggleDialogWindow">
            {{ mileage.current }}
            </span>
            <v-progress-circular v-else
                                 :size="30"
                                 color="primary"
                                 indeterminate>
            </v-progress-circular>
            <span class="headline notSelectable"
                  v-if="isLoading === false">
              km
            </span>
          </v-flex>
        </v-layout>
        <!-- MILEAGE TITLE, CURRENT STATE, KM -->

        <!-- MY DIALOG WINDOW WITH MILEAGE INPUT FIELD -->
        <my-dialog :visibility="mileage.editMode"
                   include-text-field
                   :value="mileage.new"
                   @onChangeEvent="catchChangeEvent"
                   @onInputEvent="catchInputEvent"
                   @onEnterClicked="updateCurrentMileage"
                   @onEscClicked="closeDialogAndRevertTemporaryValue"
                   :hint="'update-mileage-text-field-hint'"
                   dialog-title="update-mileage-dialog-title"
                   agree-button-text="mileage-agree-button-text"
                   disagree-button-text="mileage-disagree-button-text"
                   :agree-button-function="updateCurrentMileage"
                   :agree-button-disabled-condition="snackbarVisibility"
                   :disagree-button-function="closeDialogAndRevertTemporaryValue"
                   :disagree-button-disabled-condition="false"
                   :inputFieldAutofocus="inputFieldAutofocus" />
        <!-- MY DIALOG WINDOW WITH MILEAGE INPUT FIELD -->
      </v-flex>
      <!-- MILEAGE FLEX -->

      <!-- CARD 1 -->
      <v-flex xs10 sm6 md5
              :class="card1Classes">
        <v-card disabled
                class="elevation-12">
          <v-card-media
            src="../../../static/engine-1.jpg"
            :height="cardMediaHeight"
            class="white--text text-xs-center">
          </v-card-media>
          <v-card-title>
            <div class="text-xs-center full-width">
              <div class="mb-2">
                <span class="white--text title">{{ $t('engine-card-header') }}</span>
              </div>
              <span class="grey--text">
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
                dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip
                ex ea commodo consequat.
              </span>
            </div>
          </v-card-title>
          <v-card-actions class="centerTextInsideDiv pt-0">
            <v-btn flat
                   color="primary"
                   router
                   :to.native="enginePath"
                   ripple >
              {{ $t('details-button') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-flex>
      <!-- CARD 1 -->

      <!-- CARD 2 -->
      <v-flex xs10 sm6 md5
              :class="card2Classes">
        <v-card disabled
                class="elevation-12">
          <v-card-media
            src="../../../static/engine-2.jpg"
            :height="cardMediaHeight"
            class="white--text text-xs-center">
          </v-card-media>
          <v-card-title>
            <div class="text-xs-center full-width">
              <div class="mb-2">
                <span class="white--text title">{{ $t('fluids-card-header') }}</span>
              </div>
              <span class="grey--text">
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
                dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip
                ex ea commodo consequat.
              </span>
            </div>
          </v-card-title>
          <v-card-actions class="centerTextInsideDiv pt-0">
            <v-btn flat
                   color="primary"
                   router
                   :to.native="fluidsPath"
                   ripple >
              {{ $t('details-button') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-flex>
      <!-- CARD 2 -->

      <!-- CARD 3 -->
      <v-flex xs10 sm6 md5
              :class="card3Classes">
        <v-card disabled
                class="elevation-12">
          <v-card-media
            src="../../../static/engine-3.jpg"
            :height="cardMediaHeight"
            class="white--text text-xs-center">
          </v-card-media>
          <v-card-title>
            <div class="text-xs-center full-width">
              <div class="mb-2">
                <span class="white--text title">{{ $t('tires-card-header') }}</span>
              </div>
              <span class="grey--text">
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
                dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip
                ex ea commodo consequat.
              </span>
            </div>
          </v-card-title>
          <v-card-actions class="centerTextInsideDiv pt-0">
            <v-btn flat
                   color="primary"
                   router
                   :to.native="tiresPath"
                   ripple >
              {{ $t('details-button') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-flex>
      <!-- CARD 3 -->

      <!-- CARD 4 -->
      <v-flex xs10 sm6 md5
              :class="card4Classes">
        <v-card disabled
                class="elevation-12">
          <v-card-media
            src="../../../static/engine-4.jpg"
            :height="cardMediaHeight"
            class="white--text text-xs-center">
          </v-card-media>
          <v-card-title>
            <div class="text-xs-center full-width">
              <div class="mb-2">
                <span class="white--text title">{{ $t('maintenance-costs-card-header') }}</span>
              </div>
              <span class="grey--text">
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
                dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip
                ex ea commodo consequat.
              </span>
            </div>
          </v-card-title>
          <v-card-actions class="centerTextInsideDiv pt-0">
            <v-btn flat
                   color="primary"
                   router
                   :to.native="maintenanceCostsPath"
                   ripple >
              {{ $t('details-button') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-flex>
      <!-- CARD 4 -->
    </v-layout>

    <!-- MY SNACKBAR -->
    <my-snackbar id="mileage-empty-snackbar"
                 :timeout="0"
                 bottom
                 color="secondary"
                 :snackbar-visibility="snackbarVisibility"
                 snackbarSpanText="main-board-snackbar-text"
                 :snackbarButtonFunction="toggleDialogWindow"
                 :snackbarButtonVisibility="mileage.editMode === false"
                 snackbarButtonText="snackbar-button-text" />
    <!-- MY SNACKBAR -->

  </v-container>
</template>

<script>
  import {mapGetters} from 'vuex';
  import componentsPaths from '../../componentsPaths';

  export default {
    data() {
      return {
        mileage: {
          editMode: false,
          current: null,
          new: null
        },
        ownerEmailAddress: null,
        ownerId: null,
        dialogInputFieldValue: null,
        inputFieldAutofocus: false,
        enginePath: componentsPaths.engine,
        fluidsPath: componentsPaths.fluids,
        tiresPath: componentsPaths.tires,
        maintenanceCostsPath: componentsPaths.maintenanceCosts
      }
    },
    methods: {
      toggleDialogWindow() {
        this.mileage.editMode = !this.mileage.editMode;
        this.inputFieldAutofocus = !this.inputFieldAutofocus;
      },
      catchChangeEvent() {
        this.mileage.new = this.dialogInputFieldValue;
      },
      catchInputEvent(payload) {
        this.dialogInputFieldValue = parseInt(payload);
        // noinspection JSValidateTypes
        this.mileage.new = parseInt(payload);
      },
      closeDialogAndRevertTemporaryValue() {
        this.toggleDialogWindow();
        this.mileage.new = this.mileage.current;
        this.dialogInputFieldValue = this.mileage.current;
      },
      updateCurrentMileage() {
        this.axios.put(`/vehicles/vehicle/update-current-mileage/${window.btoa(this.ownerEmailAddress)}`, {
          encodedMileage: window.btoa(this.mileage.new)
        }).then(() => {
          this.toggleDialogWindow();
          // --- CURRENT MILEAGE --- //
          this.$store.commit('setLoading', true);
          this.axios.get(`/vehicles/vehicle/get-current-mileage/${window.btoa(this.ownerEmailAddress)}`)
              .then(response => {
                this.$store.commit('setLoading', false);
                this.mileage.current = response.data;
                this.mileage.new = this.mileage.current;
                this.dialogInputFieldValue = this.mileage.current;
              })
              .catch(error => {
				this.$store.commit('setLoading', false);
                console.log(error.response);
                console.log('ERROR -> /vehicles/vehicle/get-current-mileage');
              });
          // --- CURRENT MILEAGE --- //
        }).catch(error => {
          this.$store.commit('setLoading', false);
          console.log(error.response);
          console.log('ERROR -> /vehicles/vehicle/update-current-mileage');
          this.$store.commit('setJwtTokenExpired', true);
          // --- TOKEN EXPIRED --- //
          // this.$store.dispatch('logout')
          //   .then(() => {
          //     this.$store.commit('setLoading', false);
          //     console.log('User logged out successfully.')
          //   });
          // --- TOKEN EXPIRED --- //
        })
      }
    },
    computed: {
      ...mapGetters([
        'isLogged',
        'isLoading'
      ]),
      snackbarVisibility() {
        return (this.mileage.current < 0 || isNaN(this.mileage.current)) ||
          (this.mileage.new < 0 || isNaN(this.mileage.new)) ||
          (this.dialogInputFieldValue < 0 || isNaN(this.dialogInputFieldValue));
      },
      card1Classes () {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return {
            'mr-4': true,
            'ml-5': true
          }
        }
      },
      card2Classes () {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return {
            'mr-5': true,
            'ml-4': true
          }
        }
      },
      card3Classes () {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return {
            'mr-4': true,
            'ml-5': true
          }
        }
      },
      card4Classes () {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return {
            'mr-5': true,
            'ml-4': true
          }
        }
      },
      cardMediaHeight() {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return '250px';
        } else {
          return '150px';
        }
      },
      rowColumnDeterminer() {
        const binding = {};
        if (this.$vuetify.breakpoint.xsOnly) {
          binding.column = true;
        } else {
          binding.row = true;
        }
        return binding;
      },
      mileageTitleClasses() {
        if (this.$vuetify.breakpoint.xsOnly) {
          return {
            'text-xs-center': true,
            'pt-0': true,
            'pb-0': true,
            'mt-0': true,
            'mb-0': true
          }
        } else {
          return {
            'text-xs-right': true,
            'pt-1': true,
            'pb-1': true,
            'mt-0': true,
            'mb-0': true,
            'pr-1': true
          }
        }
      },
      mileageValueClasses() {
        if (this.$vuetify.breakpoint.xsOnly) {
          return {
            'text-xs-center': true,
            'pt-0': true,
            'pb-0': true,
            'mt-0': true,
            'mb-0': true
          }
        } else {
          return {
            'text-xs-left': true,
            'pt-1': true,
            'pb-1': true,
            'mt-0': true,
            'mb-0': true,
            'pl-1': true
          }
        }
      }
    },
    mounted() {
      if (this.isLogged === 1) {
        // --- PAGE REFRESH EVENT --- //
        this.$router.onReady(() => {
          this.$store.commit('setLoading', true);
          this.axios.get('/principal-user')
            .then(response => {
              this.$store.commit('setPrincipalUserFirstName', response.data.firstname);

              // --- CURRENT MILEAGE --- //
              this.ownerEmailAddress = response.data.email;
              this.ownerId = response.data.id;
              this.axios.get(`/vehicles/vehicle/get-current-mileage/${window.btoa(this.ownerEmailAddress)}`)
                .then(response => {
                  this.$store.commit('setLoading', false);
                  this.mileage.current = response.data;
                  this.mileage.new = this.mileage.current;
                  this.dialogInputFieldValue = this.mileage.current;
                })
                .catch(error => {
                  this.$store.commit('setLoading', false);
                  console.log(error.response);
                  console.log('ERROR -> /vehicles/vehicle/get-current-mileage');
                });
              // --- CURRENT MILEAGE --- //

            })
            .catch(error => {
              this.$store.commit('setLoading', false);
              console.log(error.response);
              console.log('ERROR -> /principal-user');
              this.$store.commit('setJwtTokenExpired', true);
              // --- TOKEN EXPIRED --- //
              // this.$store.dispatch('logout')
              //   .then(() => {
              //     this.$store.commit('setLoading', false);
              //     console.log('User logged out successfully.')
              //   });
              // --- TOKEN EXPIRED --- //
            });
        });
        // --- PAGE REFRESH EVENT --- //
      }
    }
  }
</script>

<style scoped>
  #mileage-field {
    cursor: pointer;
  }

  #mileage-field:hover {
    background-color: rgba(60, 60, 60, 1);;
  }

  .full-width {
    width: 100%;
  }
</style>

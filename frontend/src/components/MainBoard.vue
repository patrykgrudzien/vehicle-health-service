<template>
  <v-container fluid
               fill-height
               grid-list-xl>
    <v-layout align-center
              justify-center
              row
              wrap>
      <!-- MILEAGE FLEX -->
      <v-flex xs12 sm12 md12 lg12 xl12>
        <!-- SNACKBAR -->
        <v-snackbar
          :timeout="0"
          :bottom="true"
          color="secondary"
          class="notSelectable"
          :value="snackbarVisibility">
          <span class="centerSpanInsideDiv">{{ $t('main-board-snackbar-text') }}</span>
          <v-btn flat
                 ripple
                 @click.native="toggleDialogWindow"
                 color="background"
                 class="ml-3 notSelectable"
                 v-if="mileage.editMode === false">
            {{ $t('snackbar-button-text') }}
          </v-btn>
        </v-snackbar>
        <!-- SNACKBAR -->

        <!-- MILEAGE TITLE, CURRENT STATE, KM -->
        <v-flex class="text-xs-center ma-0 pa-0">
          <span class="headline notSelectable">
            {{ $t('current-mileage-title') }}
          </span>
          <span id="mileage-field"
                class="headline primary--text notSelectable"
                @click="toggleDialogWindow">
            {{ mileage.current }}
            </span>
          <span class="headline notSelectable">km</span>
        </v-flex>
        <!-- MILEAGE TITLE, CURRENT STATE, KM -->

        <!-- MY DIALOG WINDOW WITH MILEAGE INPUT FIELD -->
        <my-dialog :visibility="mileage.editMode"
                   include-text-field
                   :value="mileage.new"
                   @onChange="catchChangeEvent"
                   @onInput="catchInputEvent"
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
      <v-flex xs10 sm6 md5 :class="card1Classes">
        <v-card disabled class="elevation-12">
          <v-card-media
            src="../../static/engine-1.jpg"
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
            <v-btn flat color="primary" @click="showEngineDetails">
              {{ $t('details-button') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-flex>
      <!-- CARD 1 -->

      <!-- CARD 2 -->
      <v-flex xs10 sm6 md5 :class="card2Classes">
        <v-card disabled class="elevation-12">
          <v-card-media
            src="../../static/engine-2.jpg"
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
            <v-btn flat color="primary" @click="showFluidsDetails">
              {{ $t('details-button') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-flex>
      <!-- CARD 2 -->

      <!-- CARD 3 -->
      <v-flex xs10 sm6 md5 :class="card3Classes">
        <v-card disabled class="elevation-12">
          <v-card-media
            src="../../static/engine-3.jpg"
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
            <v-btn flat color="primary" @click="showTiresDetails">
              {{ $t('details-button') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-flex>
      <!-- CARD 3 -->

      <!-- CARD 4 -->
      <v-flex xs10 sm6 md5 :class="card4Classes">
        <v-card disabled class="elevation-12">
          <v-card-media
            src="../../static/engine-4.jpg"
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
            <v-btn flat color="primary" @click="showIntervalsDetails">
              {{ $t('details-button') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-flex>
      <!-- CARD 4 -->
    </v-layout>
  </v-container>
</template>

<script>
  import {mapGetters} from 'vuex';

  export default {
    data() {
      return {
        mileage: {
          editMode: false,
          current: null,
          new: null
        },
        ownerEmailAddress: null,
        dialogInputFieldValue: null,
        inputFieldAutofocus: false
      }
    },
    methods: {
      showEngineDetails() {
        alert('IN PROGRESS...');
      },
      showFluidsDetails() {
        alert('IN PROGRESS...');
      },
      showTiresDetails() {
        alert('IN PROGRESS...');
      },
      showIntervalsDetails() {
        alert('IN PROGRESS...');
      },
      toggleDialogWindow() {
        this.mileage.editMode = !this.mileage.editMode;
        this.inputFieldAutofocus = !this.inputFieldAutofocus;
      },
      catchChangeEvent() {
        this.mileage.new = this.dialogInputFieldValue;
      },
      catchInputEvent(payload) {
        this.dialogInputFieldValue = payload;
        this.mileage.new = payload;
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
          this.axios.get(`/vehicles/vehicle/get-current-mileage/${window.btoa(this.ownerEmailAddress)}`)
              .then(response => {
                this.mileage.current = response.data;
                this.mileage.new = this.mileage.current;
              })
              .catch(error => {
                console.log(error.response);
                console.log('ERROR -> /vehicles/vehicle/get-current-mileage');
              });
          // --- CURRENT MILEAGE --- //
        }).catch(error => {
          console.log(error.response);
          console.log('ERROR -> /vehicles/vehicle/update-current-mileage');
          // --- TOKEN EXPIRED --- //
          this.$store.dispatch('logout')
              .then(() => {
                console.log('User logged out successfully.')
              });
          // --- TOKEN EXPIRED --- //
        })
      }
    },
    computed: {
      ...mapGetters([
        'isLogged'
      ]),
      snackbarVisibility() {
        return (this.mileage.current === null || !this.mileage.current || this.mileage.current === '') ||
          (this.mileage.new === null || !this.mileage.new || this.mileage.new === '') ||
          (this.dialogInputFieldValue !== null && !this.dialogInputFieldValue && this.dialogInputFieldValue === '');
      },
      card1Classes () {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return {
            'mr-5': true,
            'ml-5': true
          }
        }
      },
      card2Classes () {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return {
            'mr-5': true,
            'ml-5': true
          }
        }
      },
      card3Classes () {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return {
            'mr-5': true,
            'ml-5': true
          }
        }
      },
      card4Classes () {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return {
            'mr-5': true,
            'ml-5': true
          }
        }
      },
      cardMediaHeight() {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return '250px';
        } else {
          return '150px';
        }
      }
    },
    mounted() {
      if (this.isLogged === 1) {
        // --- PAGE REFRESH EVENT --- //
        this.$router.onReady(() => {
          this.axios.get('/principal-user')
            .then(response => {
              this.$store.commit('setPrincipalUserFirstName', response.data.firstname);

              // --- CURRENT MILEAGE --- //
              this.ownerEmailAddress = response.data.email;
              this.axios.get(`/vehicles/vehicle/get-current-mileage/${window.btoa(this.ownerEmailAddress)}`)
                .then(response => {
                  this.mileage.current = response.data;
                  this.mileage.new = this.mileage.current;
                })
                .catch(error => {
                  console.log(error.response);
                  console.log('ERROR -> /vehicles/vehicle/get-current-mileage');
                });
              // --- CURRENT MILEAGE --- //

            })
            .catch(error => {
              console.log(error.response);
              console.log('ERROR -> /principal-user');
              // --- TOKEN EXPIRED --- //
              this.$store.dispatch('logout')
                  .then(() => {
                    console.log('User logged out successfully.')
                  });
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

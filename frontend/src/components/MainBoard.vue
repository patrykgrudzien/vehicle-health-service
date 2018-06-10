<template>
  <v-container fluid
               fill-height
               grid-list-xl>
    <v-layout align-center
              justify-center
              row
              wrap>
      <v-flex xs12 sm12 md12 lg12 xl12>
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

        <div class="text-xs-center">
          <span class="headline notSelectable">
            {{ $t('current-mileage-title') }}
          </span>
          <span id="mileage-field"
                class="headline primary--text notSelectable"
                @click="toggleDialogWindow">
            {{ mileage.value }}
            </span>
          <span class="headline notSelectable">km</span>
        </div>

        <!-- MY DIALOG WINDOW WITH MILEAGE INPUT FIELD -->
        <my-dialog :visibility="mileage.editMode"
                   include-text-field
                   :value="mileage.value"
                   :hint="'update-mileage-text-field-hint'"
                   dialog-title="update-mileage-dialog-title"
                   agree-button-text="mileage-agree-button-text"
                   disagree-button-text="mileage-disagree-button-text"
                   :agree-button-function="updateCurrentMileage"
                   :disagree-button-function="toggleDialogWindow"/>
        <!-- MY DIALOG WINDOW WITH MILEAGE INPUT FIELD -->

      </v-flex>

      <!-- CARD 1 -->
      <v-flex xs10 sm6 md5 :class="{'mr-5': this.$vuetify.breakpoint.lgAndUp}">
        <v-card disabled class="elevation-12">
          <v-card-media
            src="../../static/engine-1.jpg"
            height="150px"
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
      <v-flex xs10 sm6 md5 :class="{'ml-5': this.$vuetify.breakpoint.lgAndUp}">
        <v-card disabled class="elevation-12">
          <v-card-media
            src="../../static/engine-2.jpg"
            height="150px"
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
      <v-flex xs10 sm6 md5 :class="{'mr-5': this.$vuetify.breakpoint.lgAndUp}">
        <v-card disabled class="elevation-12">
          <v-card-media
            src="../../static/engine-3.jpg"
            height="150px"
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
      <v-flex xs10 sm6 md5 :class="{'ml-5': this.$vuetify.breakpoint.lgAndUp}">
        <v-card disabled class="elevation-12">
          <v-card-media
            src="../../static/engine-4.jpg"
            height="150px"
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
          value: null
        }
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
      updateCurrentMileage() {
      },
      toggleDialogWindow() {
        this.mileage.editMode = !this.mileage.editMode;
      }
    },
    computed: {
      ...mapGetters([
        'isLogged'
      ]),
      snackbarVisibility() {
        return this.mileage.value === null;
      }
    },
    mounted() {
      if (this.isLogged === 1) {
        // --- PAGE REFRESH EVENT --- //
        this.$router.onReady(() => {
          this.axios.get('/principal-user')
            .then(response => {
              this.$store.commit('setPrincipalUserFirstName', response.data.firstname);

              let ownerEmailAddress = response.data.email;
              // --- CURRENT MILEAGE --- //
              this.axios.get(`/vehicles/get-current-mileage/${ownerEmailAddress}`)
                .then(response => {
                  this.mileage.value = response.data;
                })
                .catch(error => {
                  console.log(error.response.data);
                });
              // --- CURRENT MILEAGE --- //

            })
            .catch(error => {
              console.log(error.response.data);
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

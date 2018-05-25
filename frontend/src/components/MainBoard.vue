<template>
  <v-container fluid
               fill-height
               grid-list-xl>
    <v-layout align-center
              justify-center
              row
              wrap>
      <v-flex xs12 sm12 md12 lg12 xl12>
        <div class="text-xs-center">
          <span class="headline">{{ $t('current-mileage-title') }}</span>

          <v-tooltip bottom dark open-delay="150">
            <span id="mileage-field"
                  class="headline primary--text"
                  @click="updateMileage"
                  slot="activator">200000</span>
            <span>{{ $t('mileage-tooltip-text') }}</span>
          </v-tooltip>
          <span class="headline">km</span>
        </div>
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
          <v-card-actions class="button-container pt-0">
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
          <v-card-actions class="button-container pt-0">
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
          <v-card-actions class="button-container pt-0">
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
                <span class="white--text title">{{ $t('intervals-card-header') }}</span>
              </div>
              <span class="grey--text">
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
                dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip
                ex ea commodo consequat.
              </span>
            </div>
          </v-card-title>
          <v-card-actions class="button-container pt-0">
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
  export default {
    data() {
      return {}
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
      updateMileage() {
        alert('IN PROGRESS...');
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

  .button-container {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .full-width {
    width: 100%;
  }
</style>

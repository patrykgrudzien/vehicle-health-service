<template>
  <!-- full size home-background -->
  <v-container id="home-background" fluid fill-height>
    <v-layout align-center justify-center>
      <v-flex xs12 sm10 md8 xl6>
        <v-card id="cards-container" flat disabled>
          <v-container grid-list-xs>
            <v-layout>
              <v-flex>
                <v-card-text class="text-xs-center display-2">{{ $t('background-text-1') }}</v-card-text>
              </v-flex>
            </v-layout>
          </v-container>
          <v-container grid-list-xs>
            <v-layout>
              <v-flex xs6>
                <v-card-text class="headline text-xs-center">{{ $t('background-text-2') }}</v-card-text>
              </v-flex>
              <v-flex xs6>
                <v-card-text class="headline text-xs-center">{{ $t('background-text-3') }}</v-card-text>
              </v-flex>
            </v-layout>
          </v-container>
          <v-container grid-list-xs>
            <v-layout>
              <v-flex xs6>
                <v-card-text class="headline text-xs-center">{{ $t('background-text-4') }}</v-card-text>
              </v-flex>
              <v-flex xs6>
                <v-card-text class="headline text-xs-center">{{ $t('background-text-5') }}</v-card-text>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  export default {
    data() {
      return {}
    }
  }
</script>

<style scoped>
  #cards-container {
    background: rgba(60, 60, 60, 0.7);
    font-family: 'Arsenal', sans-serif;
  }

  #home-background {
    background: url('../../static/engine-5.jpg') no-repeat center center fixed;
    background-size: contain;
    box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.55);
  }

  @media screen and (max-width: 768px) and (max-height: 1024px) {
    #home-background {
      width: 768px;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.55);
    }
  }

  @media screen and (max-width: 1024px) and (max-height: 1366px) {
    #home-background {
      width: 1024px;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.55);
    }
  }

  @media screen and (max-width: 1366px) and (max-height: 1024px) {
    #home-background {
      width: 1366px;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.55);
    }
  }

  @media screen and (max-width: 1366px) and (max-height: 768px) {
    #home-background {
      width: 1366px;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.55);
    }
  }

  @media screen and (max-width: 1080px) and (max-height: 1920px) {
    #home-background {
      width: 1080px;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.55);
    }
  }

  @media screen and (max-width: 1920px) and (max-height: 1080px) {
    #home-background {
      width: 1920px;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.55);
    }
  }

  @media all and (min-width: 1920px) {
    #home-background {
      width: 100%;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.55);
    }
  }
</style>

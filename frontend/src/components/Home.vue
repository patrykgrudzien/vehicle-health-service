<template>
  <v-container id="home-background" fluid fill-height>
    <!-- full size home-background -->
  </v-container>
</template>

<style scoped>
  #home-background {
    background: url('../../static/home-background.jpg') no-repeat center center fixed;
    background-size: contain;
  }

  @media screen and (max-width: 768px) and (max-height: 1024px) {
    #home-background {
      width: 768px;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.4);
    }
  }

  @media screen and (max-width: 1024px) and (max-height: 1366px) {
    #home-background {
      width: 1024px;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.4);
    }
  }

  @media screen and (max-width: 1366px) and (max-height: 1024px) {
    #home-background {
      width: 1366px;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.4);
    }
  }

  @media screen and (max-width: 1366px) and (max-height: 768px) {
    #home-background {
      width: 1366px;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.4);
    }
  }

  @media screen and (max-width: 1080px) and (max-height: 1920px) {
    #home-background {
      width: 1080px;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.4);
    }
  }

  @media screen and (max-width: 1920px) and (max-height: 1080px) {
    #home-background {
      width: 1920px;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.4);
    }
  }

  @media all and (min-width: 1920px) {
    #home-background {
      width: 100%;
      height: 100%;
      background-size: cover;
      box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.4);
    }
  }
</style>

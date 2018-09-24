<template>
  <v-layout row align-center justify-center>

    <v-flex xs12 sm8 md6>
      <!-- LOADING DIALOG WINDOW -->
      <loading-dialog :visibility="isLoading"/>
      <!-- LOADING DIALOG WINDOW -->
    </v-flex>

  </v-layout>
</template>

<script>
  import {mapGetters} from 'vuex';
  import {MUTATIONS}  from "../../Constants";

  export default {
    computed: {
      ...mapGetters([
        'isLoading'
      ])
    },
    created() {
      let config = {
        headers: {
          Authorization: `Bearer ${this.$route.query.shortLivedAuthToken}`
        }
      };

      this.$store.commit(MUTATIONS.SET_LOADING, true);
      this.axios.get('/exchange-short-lived-token', config)
          .then(response => {
            this.$store.commit(MUTATIONS.SET_LOADING, false);
            this.$store.dispatch('performSuccessfulLoginOperations', response);
          })
          .catch(() => {
            this.$store.commit(MUTATIONS.SET_LOADING, false);
          })
    }
  }
</script>

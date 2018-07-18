<template>
  <v-dialog v-model="visibility"
            persistent
            :max-width="determineResponsiveMaxWidth">
    <v-card class="text-xs-center notSelectable">
      <v-container fluid
                   class="pb-0">
        <v-layout row
                  align-center
                  justify-center
                  class="notSelectable">
          <v-flex xs8>
            <v-progress-circular color="primary"
                                 indeterminate
                                 :size="determineResponsiveSize"/>
          </v-flex>
        </v-layout>
      </v-container>
      <v-card-title id="dialog-title"
                    :class="determineResponsiveClasses"
                    v-if="dialogTitle && dialogTitle !== ''">
        {{ $t(`${dialogTitle}`) }}
      </v-card-title>
    </v-card>
  </v-dialog>
</template>

<script>
  export default {
    props: {
      visibility: {
        default: false,
        type: Boolean
      },
      dialogTitle: {
        default: 'loading-dialog-title',
        type: String
      }
    },
    computed: {
      determineResponsiveSize() {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return 75;
        } else {
          return 50;
        }
      },
      determineResponsiveMaxWidth() {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return 300;
        } else {
          return 225;
        }
      },
      determineResponsiveClasses() {
        if (this.$vuetify.breakpoint.lgAndUp) {
          return {
            'headline': true,
            'notSelectable': true
          };
        } else {
          return {
            'notSelectable': true
          };
        }
      }
    }
  }
</script>

<style scoped>
  button {
    border: none;
  }

  #dialog-title {
    display: flex;
    justify-content: center;
    align-items: center;
  }
</style>

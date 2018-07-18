<template>
  <v-dialog v-model="visibility"
            persistent
            max-width="300">
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
                                 size="75"/>
          </v-flex>
        </v-layout>
      </v-container>
      <v-card-title id="dialog-title"
                    class="headline notSelectable"
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

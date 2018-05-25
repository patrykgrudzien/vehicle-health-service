<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex xs12 sm8 md6>
        <v-card color="background" style="box-shadow: none">
          <v-card-text>
            <div class="text-xs-center mb-4">
              <span class="display-1 pr-1">{{ name }}</span>
              <span class="display-1 pl-1">{{ lastName }}</span>
            </div>
            <div class="text-xs-center mt-4">
              <span class="headline">{{ profession }}</span>
            </div>
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>

<script>
  export default {
    data() {
      return {
        name: 'Patryk',
        lastName: 'Grudzień',
        profession: 'Junior Software Engineer'
      }
    }
  }
</script>

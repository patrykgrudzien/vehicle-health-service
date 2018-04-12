<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex xs12 sm8 md6>
        <v-card color="background" style="box-shadow: none">
          <v-card-text>
            <h3><p>{{ $t('my-name') }}<br>{{ name }}</p><br></h3>
            <h3><p>{{ $t('my-last-name') }}<br>{{ lastName}}</p><br></h3>
            <h3><p>{{ $t('my-age') }}<br>{{ age }}</p><br></h3>
            <h3><p>{{ $t('my-profession') }}<br>{{ profession }}</p></h3>
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
        age: 27,
        profession: 'Junior Software Engineer'
      }
    }
  }
</script>

<style scoped>
  p {
    text-align: center;
    margin-bottom: 0;
  }

  h6 {
    text-align: center;
  }
</style>

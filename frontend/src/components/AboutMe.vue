<template>
  <v-container fluid fill-height>
    <v-layout row align-center justify-center>
      <v-flex xs12 sm8 md6>
        <v-card>
          <v-card-text class="headline">
            <p>My name is:<br>{{ name }}</p><br>
            <p>My last name is:<br>{{ lastName}}</p><br>
            <p>I'm:<br>{{ age }}</p><br>
            <p>My Profession is:<br>{{ profession }}</p>
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

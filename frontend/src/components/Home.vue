<template>

  <v-container>

    <v-layout row wrap align-center>
      <v-flex xs12 sm6 class="text-xs-center text-sm-center">
        <v-btn large class="secondary" router to="/registration-form">Sign Up</v-btn>
      </v-flex>
      <v-flex xs12 sm6 class="text-xs-center text-sm-center">
        <v-btn large class="secondary" router to="/login">Sign In</v-btn>
      </v-flex>
    </v-layout>

    <v-layout row>
      <v-flex xs12>
        <v-carousel>
          <v-carousel-item
            v-for="meetup in meetups"
            :src="meetup.imageUrl"
            :key="meetup.id">
            <div class="title">
              {{ meetup.title }}
            </div>
          </v-carousel-item>
        </v-carousel>
      </v-flex>
    </v-layout>

  </v-container>

</template>

<script>
  export default {
    data() {
      return {
        meetups: [
          {
            imageUrl: 'http://www.marathon-travel.pl/wp-content/uploads/2013/08/New_york_times_square-terabass.jpg',
            id: 'asdfsadfsadfsad',
            title: 'Meetup in New York'
          },
          {
            imageUrl: 'https://i.ytimg.com/vi/_FYKIhJZdaI/maxresdefault.jpg',
            id: 'asdfsadf123123123sadfsad',
            title: 'Meetup in Paris'
          }
        ]
      }
    }
  }
</script>

<style scoped>
  .title {
    position: absolute;
    bottom: 50px;
  }
</style>

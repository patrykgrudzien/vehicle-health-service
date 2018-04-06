<template>
  <v-card>
    <v-card-media :src="meetup.imageUrl" height="70vh"/>
  </v-card>
</template>

<script>
  export default {
    data() {
      return {
        meetup:
          {
            imageUrl: 'http://www.marathon-travel.pl/wp-content/uploads/2013/08/New_york_times_square-terabass.jpg',
            id: 'asdfsadfsadfsad',
            title: 'Meetup in New York',
            location: 'New York'
          }
      }
    }
  }
</script>

<template>
  <b-container>
    <b-row>
      <b-col>
        <h2 class="text-center" style="margin-top: 50px;">{{ confirmationMessage }}</h2>
      </b-col>
    </b-row>
    <div class="text-center">
      <b-link to="/login" router-tag="b-button">Login there!</b-link>
    </div>
  </b-container>
</template>

<script>
  import bContainer from 'bootstrap-vue/es/components/layout/container';

  export default {
    components: {
      bContainer
    },
    data() {
      return {
        confirmationMessage: 'Your account has been confirmed and created!'
      }
    }
  }
</script>

<style scoped>

</style>

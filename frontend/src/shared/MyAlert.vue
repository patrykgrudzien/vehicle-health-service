<template>
  <!--
    (dismissible) - specifies that the Alert can be closed
    (value) - controls visibility
    (input) - "event" -> the updated bound model
  -->
  <v-alert :type="type"
           :dismissible="dismissible"
           @input="onClose"
           :value="true"
           transition="scale-transition"
           :errors="errors">
    {{ message }}
    <template v-if="errors">
      <p class="pl-1 mb-1">{{ message }}</p>
      <ul class="pl-3" v-for="error in errors">
        <li>{{ error }}</li>
      </ul>
    </template>
  </v-alert>
</template>

<script>
  export default {
    props: [
      'message',
      'type',
      'errors',
      'dismissible'
    ],
    methods: {
      onClose() {
        this.$emit('dismissed');
      }
    }
  }
</script>

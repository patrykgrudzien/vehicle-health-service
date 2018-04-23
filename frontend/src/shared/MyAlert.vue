<template>
  <v-alert :type="type"
           dismissible
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
      'errors'
    ],
    methods: {
      onClose() {
        this.$emit('dismissed');
      }
    }
  }
</script>

<template>
  <v-alert :type="type"
           dismissible
           @input="onClose"
           :value="true"
           transition="scale-transition">
    {{ errorMessage }}
  </v-alert>
</template>

<script>
  export default {
    props: [
      'errorMessage',
      'type'
    ],
    methods: {
      onClose() {
        this.$emit('dismissed');
      }
    }
  }
</script>

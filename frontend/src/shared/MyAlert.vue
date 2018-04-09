<template>
  <v-alert type="error"
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
      'errorMessage'
    ],
    methods: {
      onClose() {
        this.$emit('dismissed');
      }
    }
  }
</script>

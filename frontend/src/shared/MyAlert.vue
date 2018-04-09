<template>
  <v-alert type="error" dismissible @input="onClose" :value="true">
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

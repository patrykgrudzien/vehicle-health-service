<template>

  <v-snackbar
    :timeout="timeout"
    :bottom="bottom"
    :color="color"
    class="notSelectable"
    :value="snackbarVisibility">
    <span class="centerSpanInsideDiv">{{ $t(snackbarSpanText) }}</span>
    <v-btn flat
           ripple
           @click.native="snackbarButtonFunction"
           color="background"
           class="ml-3 notSelectable"
           v-if="snackbarButtonVisibility">
      {{ $t(snackbarButtonText) }}
    </v-btn>
  </v-snackbar>

</template>

<script>
export default {
  props: {
    timeout: {
      default: 0,
      type: Number
    },
    bottom: {
      default: true,
      type: Boolean
    },
    color: {
      default: 'secondary',
      type: String
    },
    snackbarVisibility: {
      default: false,
      type: Boolean
    },
    snackbarSpanText: {
      default: '',
      type: String
    },
    snackbarButtonFunction: {
      type: Function
    },
    snackbarButtonVisibility: {
      default: false,
      type: Boolean
    },
    snackbarButtonText: {
      default: '',
      type: String
    }
  }
}
</script>

<style scoped>

</style>

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
      <ul class="pl-3"
          v-for="error in errors"
          :key="error">
        <li>{{ error }}</li>
      </ul>
    </template>
  </v-alert>
</template>

<script>
export default {
  props: {
    message: {},
    type: {
      default: 'success',
      type: String
    },
    errors: {
      default: null,
      type: Array
    },
    dismissible: {
      default: false,
      type: Boolean
    }
  },
  methods: {
    onClose () {
      this.$emit('dismissed')
    }
  }
}
</script>

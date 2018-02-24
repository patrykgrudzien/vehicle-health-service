<template>
  <div id="add-todo-item">
    <h2>Add new Todo Item</h2>
    <form>
      <!-- Item description -->
      <label for="todo-description">Todo description:</label>
      <input v-model="todoItem.description" id="todo-description" type="text" required>

      <!-- Item completed flag -->
      <label for="todo-completed">Completed (true/false)</label>
      <input v-model="todoItem.completed" id="todo-completed" type="text">

      <button class="btn btn-primary" @click.prevent="post">Add Todo Item</button>

      <p v-if="successfulResponse">Successfully added with ID: {{ response }}</p>
    </form>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        todoItem: {
          id: '',
          description: '',
          completed: ''
        },
        successfulResponse: false,
        response: []
      }
    },
    methods: {
      post() {
        this.axios.post(`todo-item/add`, this.todoItem)
          .then(response => {
            this.successfulResponse = true;
            this.response = response.data;
          })
          .catch(error => {
            this.successfulResponse = false;
            console.log(error);
          })
      }
    }
  }
</script>

<style scoped>
  #add-todo-item * {
    box-sizing: border-box;
  }

  #add-todo-item {
    margin: 20px auto;
    max-width: 500px;
  }

  label {
    display: block;
    margin: 20px 0 10px;
  }

  input[type="text"] {
    display: block;
    width: 100%;
    max-width: 500px;
    padding: 8px;
  }

  h3 {
    margin-top: 10px;
  }

  .btn-primary {
    margin-top: 20px;
    margin-bottom: 20px;
  }
</style>

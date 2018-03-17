<template>
  <b-navbar toggleable="md" type="dark" variant="info" style="margin-bottom: 20px;">

    <b-navbar-brand>Full-Stack Web App</b-navbar-brand>
    <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>

    <b-collapse is-nav id="nav_collapse">

      <b-navbar-nav>
        <b-nav-item to="/" exact>Home</b-nav-item>
        <b-nav-item to="/server-health" exact>Server Health</b-nav-item>
        <b-nav-item to="/about-me" exact>About me</b-nav-item>
      </b-navbar-nav>

      <!-- Right aligned nav items -->
      <b-navbar-nav class="ml-auto">
        <b-nav-item to="/login" exact>Login</b-nav-item>

        <b-nav-item-dropdown text="Language" right style="padding-left: 10px;">
          <b-dropdown-item to="/" active-class="dropdown-item">EN</b-dropdown-item>
          <b-dropdown-item to="/" active-class="dropdown-item">PL</b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>

    </b-collapse>

  </b-navbar>
</template>

<script>
  export default {}
</script>

<style scoped>
  @media screen and (max-width: 1080px) {
    .nav-link {
      display: inline-block;
      padding-left: 10px;
      padding-right: 10px;
    }
  }

  .active {
    background-color: #303030;
    color: #303030;
    text-decoration: none;
    border-radius: 10px;
  }

  .dropdown-item {
    background-color: transparent;
  }
</style>

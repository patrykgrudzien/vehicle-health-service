<template>
  <ul class="cb-slideshow">
    <li v-for="image in images" :key="image.id">
      <span :style="backgroundClass(image)">Image {{ image.id }}</span>
      <div :style="titleClass(image)"><h3>{{ $t(`${image.title}`) }}</h3></div>
    </li>
  </ul>
</template>

<script>
export default {
  data: function () {
    return {
      images: [{
        id: 1,
        title: 'background-text-1',
        path: '../../assets/engine-1.jpg'
      }, {
        id: 2,
        title: 'background-text-2',
        path: '../../assets/engine-2.jpg'
      }, {
        id: 3,
        title: 'background-text-3',
        path: '../../assets/engine-3.jpg'
      }, {
        id: 4,
        title: 'background-text-4',
        path: '../../assets/engine-4.jpg'
      }, {
        id: 5,
        title: 'background-text-5',
        path: '../../assets/engine-5.jpg'
      }]
    }
  },
  methods: {
    backgroundClass (image) {
      // determine place of image in array
      let pos = this.images.map(function (x) {
        return x.id
      }).indexOf(image.id)
      if (pos > 0) {
        let styles = {
          'animation-delay': pos * 5 + 's',
          'background-image': `url(${image.path})`
        }
        return styles
      }
      return {
        'background-image': `url(${image.path})`
      }
    },
    titleClass (image) {
      // determine place of image in array
      let pos = this.images.map(function (x) {
        return x.id
      }).indexOf(image.id)
      if (pos > 0) {
        return {
          'animation-delay': pos * 5 + 's'
        }
      }
    }
  }
}
</script>

<style scoped>
  .cb-slideshow,
  .cb-slideshow:after {
    position: fixed;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    z-index: 0;
    box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.4);
  }

  .cb-slideshow:after {
    content: '';
  }

  .cb-slideshow li span {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    color: transparent;
    background-size: cover;
    background-position: 50% 50%;
    opacity: 0;
    z-index: 0;
    animation: imageAnimation 25s linear infinite 0s;
    box-shadow: inset 0 0 0 2000px rgba(48, 48, 48, 0.1);
  }

  .cb-slideshow li div {
    z-index: 1000;
    position: absolute;
    bottom: 30px;
    left: 0;
    width: 100%;
    text-align: center;
    opacity: 0;
    color: #fff;
    animation: titleAnimation 25s linear infinite 0s;
  }

  .cb-slideshow li div h3 {
    font-family: 'BebasNeueRegular', 'Arial Narrow', Arial, sans-serif;
    font-size: 50px;
    padding: 0;
    line-height: 100px;
  }

  @keyframes imageAnimation {
    0% {
      opacity: 0;
      animation-timing-function: ease-in;
    }
    8% {
      opacity: 1;
      animation-timing-function: ease-out;
    }
    17% {
      opacity: 1;
      transform: scale(1.1) rotate(2deg);
    }
    25% {
      opacity: 0;
      transform: scale(1.1) rotate(2deg);
    }
    100% {
      opacity: 0
    }
  }

  @keyframes titleAnimation {
    0% {
      opacity: 0;
      transform: translateX(200px);
    }
    8% {
      opacity: 1;
      transform: translateX(0px);
    }
    17% {
      opacity: 1;
      transform: translateX(0px);
    }
    19% {
      opacity: 0;
      transform: translateX(400px);
    }
    25% {
      opacity: 0
    }
    100% {
      opacity: 0
    }
  }

  @media screen and (max-width: 1140px) {
    .cb-slideshow li div h3 {
      font-size: 50px;
    }
  }

  @media screen and (max-width: 600px) {
    .cb-slideshow li div h3 {
      font-size: 40px
    }
  }

</style>

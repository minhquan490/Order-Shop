<script lang="ts">
export default {
  props: {
    pitureUrls: Array<string>
  },
  computed: {
    cssVars() {
      if (this.pitureUrls && this.pitureUrls.length !== 0) {
        return {
          '--bg-urls': this.pitureUrls.toLocaleString()
        }
      } else {
        return {}
      }
    }
  }
}
</script>

<template>
  <div class="container">
    <div v-if="pitureUrls && pitureUrls.length !== 0" class="slideshow">
      <div v-for="(pic, i) in pitureUrls" :id="`slide-${i}`" class="slide">
        <a :href="i === 0 ? `#slide-${pitureUrls.length - 1}` : `#slide-${i - 1}`"></a>
        <a :href="i === pitureUrls.length ? '#slide-1' : `#slide-${i + 1}`"></a>
        <img :src="pic" alt="">
      </div>
    </div>
    <div v-else class="flex items-center justify-center">
      <img src="/no-img.svg" alt="">
    </div>
  </div>
</template>

<style lang="scss" scoped>
h1 {
  text-align: center;
}

h1,
aside,
.container {
  margin: 0 auto 20px;
  max-width: 500px;
  max-height: 300px;
}

.container {
  border-radius: 5px;
  margin: 0 auto;
  position: relative;
  transition: box-shadow 200ms;

  &:hover {
    box-shadow: 0 10px 50px -10px rgba(0, 0, 0, 0.25);
  }
}

.slideshow {
  position: relative;
  width: 100%;

  &:after {
    content: '';
    display: block;
    padding-bottom: calc((100% / 6) * 4);
  }

  &:hover a {
    opacity: 1;
  }

  a {
    opacity: 0;
    position: relative;
    text-decoration: none;
    transition: opacity 0.5s;

    &:after {
      border-color: #FFF #FFF transparent transparent;
      border-style: solid;
      border-width: 2px;
      color: #FFF;
      display: block;
      height: 10px;
      position: absolute;
      top: calc(50% - 5px);
      width: 10px;
    }

    &:first-child:after {
      content: '';
      left: 10px;
      transform: rotate(-135deg);
    }

    &:nth-child(2):after {
      content: '';
      right: 10px;
      transform: rotate(45deg);
    }
  }

  .slide {
    background-color: #FFF;
    box-sizing: border-box;
    display: none;
    height: 100%;
    position: absolute;
    width: 100%;

    &:first-child,
    &:target {
      display: block;
    }

    a {
      display: block;
      height: 100%;
      position: absolute;
      width: 50%;

      &:nth-child(2) {
        left: 50%;
      }
    }

    img {
      border-radius: 5px;
      width: 100%;
      height: 100%;
    }
  }
}

a:target {
  color: red;
}
</style>
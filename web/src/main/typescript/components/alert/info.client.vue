<script lang="ts">
export default {
  props: {
    contents: Array<string>
  }
}
</script>

<template>
  <div
    class="alert fade alert-simple alert-info alert-dismissible text-left font__family-montserrat font__size-16 font__weight-light brk-library-rendered rendered show p-3 flex flex-row-reverse items-center">
    <div class="flex items-center flex-col pl-3">
      <span v-for="content of contents" class="blue-cross text-sm" v-text="content"></span>
    </div>
    <Icon name="ep:info-filled" width="18" height="18" class="faa-shake animated start-icon" />
  </div>
</template>

<style scoped>
.alert>.start-icon {
  margin-right: 0;
  min-width: 20px;
  text-align: center;
}

.alert>.start-icon {
  margin-right: 5px;
}

.alert-simple.alert-info {
  border: 1px solid rgba(6, 44, 241, 0.46);
  background-color: rgba(7, 73, 149, 0.12156862745098039);
  box-shadow: 0 0 2px #0396ff;
  color: #0396ff;
  text-shadow: 2px 1px #00040a;
  transition: 0.5s;
  cursor: pointer;
}

.alert-info:hover {
  background-color: rgba(7, 73, 149, 0.35);
  transition: 0.5s;
}

.blue-cross {
  font-size: 18px;
  color: #fff;
  text-shadow: none;
}

.alert:before {
  content: '';
  position: absolute;
  width: 0;
  height: calc(100% - 44px);
  border-left: 1px solid;
  border-right: 2px solid;
  border-bottom-right-radius: 3px;
  border-top-right-radius: 3px;
  left: 0;
  top: 50%;
  transform: translate(0, -50%);
  height: 20px;
}

@-webkit-keyframes wrench {
  0% {
    -webkit-transform: rotate(-12deg);
    transform: rotate(-12deg);
  }

  8% {
    -webkit-transform: rotate(12deg);
    transform: rotate(12deg);
  }

  10% {
    -webkit-transform: rotate(24deg);
    transform: rotate(24deg);
  }

  18% {
    -webkit-transform: rotate(-24deg);
    transform: rotate(-24deg);
  }

  20% {
    -webkit-transform: rotate(-24deg);
    transform: rotate(-24deg);
  }

  28% {
    -webkit-transform: rotate(24deg);
    transform: rotate(24deg);
  }

  30% {
    -webkit-transform: rotate(24deg);
    transform: rotate(24deg);
  }

  38% {
    -webkit-transform: rotate(-24deg);
    transform: rotate(-24deg);
  }

  40% {
    -webkit-transform: rotate(-24deg);
    transform: rotate(-24deg);
  }

  48% {
    -webkit-transform: rotate(24deg);
    transform: rotate(24deg);
  }

  50% {
    -webkit-transform: rotate(24deg);
    transform: rotate(24deg);
  }

  58% {
    -webkit-transform: rotate(-24deg);
    transform: rotate(-24deg);
  }

  60% {
    -webkit-transform: rotate(-24deg);
    transform: rotate(-24deg);
  }

  68% {
    -webkit-transform: rotate(24deg);
    transform: rotate(24deg);
  }

  100%,
  75% {
    -webkit-transform: rotate(0);
    transform: rotate(0);
  }
}

@keyframes wrench {
  0% {
    -webkit-transform: rotate(-12deg);
    transform: rotate(-12deg);
  }

  8% {
    -webkit-transform: rotate(12deg);
    transform: rotate(12deg);
  }

  10% {
    -webkit-transform: rotate(24deg);
    transform: rotate(24deg);
  }

  18% {
    -webkit-transform: rotate(-24deg);
    transform: rotate(-24deg);
  }

  20% {
    -webkit-transform: rotate(-24deg);
    transform: rotate(-24deg);
  }

  28% {
    -webkit-transform: rotate(24deg);
    transform: rotate(24deg);
  }

  30% {
    -webkit-transform: rotate(24deg);
    transform: rotate(24deg);
  }

  38% {
    -webkit-transform: rotate(-24deg);
    transform: rotate(-24deg);
  }

  40% {
    -webkit-transform: rotate(-24deg);
    transform: rotate(-24deg);
  }

  48% {
    -webkit-transform: rotate(24deg);
    transform: rotate(24deg);
  }

  50% {
    -webkit-transform: rotate(24deg);
    transform: rotate(24deg);
  }

  58% {
    -webkit-transform: rotate(-24deg);
    transform: rotate(-24deg);
  }

  60% {
    -webkit-transform: rotate(-24deg);
    transform: rotate(-24deg);
  }

  68% {
    -webkit-transform: rotate(24deg);
    transform: rotate(24deg);
  }

  100%,
  75% {
    -webkit-transform: rotate(0);
    transform: rotate(0);
  }
}
</style>

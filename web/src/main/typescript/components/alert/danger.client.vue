<script lang="ts">
export default {
  props: {
    contents: Array<string>
  }
}
</script>

<template>
  <div
    class="alert fade alert-simple alert-danger alert-dismissible text-left font__family-montserrat font__size-16 font__weight-light brk-library-rendered rendered show flex items-center p-3">
    <Icon class="faa-pulse animated" name="zondicons:close-outline" width="18" height="18" />
    <div class="flex items-center flex-col pl-3">
      <span v-for="content of contents" class="danger text-sm" v-text="content"></span>
    </div>
  </div>
</template>

<style scoped>
.alert-simple.alert-danger {
  border: 1px solid rgba(241, 6, 6, 0.81);
  background-color: rgba(220, 17, 1, 0.16);
  box-shadow: 0 0 2px #ff0303;
  color: #ff0303;
  text-shadow: 2px 1px #00040a;
  transition: 0.5s;
  cursor: pointer;
}

.alert-danger:hover {
  background-color: rgba(220, 17, 1, 0.33);
  transition: 0.5s;
}

.danger {
  font-size: 18px;
  color: #ff0303;
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

@-webkit-keyframes pulse {
  0% {
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
  }

  50% {
    -webkit-transform: scale(.8);
    transform: scale(.8);
  }

  100% {
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
  }
}

@keyframes pulse {
  0% {
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
  }

  50% {
    -webkit-transform: scale(.8);
    transform: scale(.8);
  }

  100% {
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
  }
}
</style>

<script lang="ts">
export default {
  props: {
    content: String
  }
}
</script>

<template>
  <div
    class="alert fade alert-simple alert-success alert-dismissible text-left font__family-montserrat font__size-16 font__weight-light brk-library-rendered rendered show p-3 flex flex-row-reverse items-center">
    <span class="greencross" v-text="content"></span>
    <Icon class="start-icon faa-tada animated" name="mdi:success-circle-outline" width="18" height="18" />
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

.greencross {
  font-size: 18px;
  color: #2cc51b;
  text-shadow: none;
}

.alert-simple.alert-success {
  border: 1px solid rgba(36, 241, 6, 0.46);
  background-color: rgba(7, 149, 66, 0.12156862745098039);
  box-shadow: 0 0 2px #259c08;
  color: rgba(36, 241, 6, 0.46);
  text-shadow: 2px 1px #00040a;
  transition: 0.5s;
  cursor: pointer;
}

.alert-success:hover {
  background-color: rgba(7, 149, 66, 0.35);
  transition: 0.5s;
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


/**
 * ----------------------------------------
 * animation blink-1
 * ----------------------------------------
 */
@-webkit-keyframes blink-1 {

  0%,
  50%,
  100% {
    opacity: 1;
  }

  25%,
  75% {
    opacity: 0;
  }
}

@keyframes blink-1 {

  0%,
  50%,
  100% {
    opacity: 1;
  }

  25%,
  75% {
    opacity: 0;
  }
}

@keyframes tada {
  0% {
    -webkit-transform: scale(1);
    transform: scale(1);
  }

  10%,
  20% {
    -webkit-transform: scale(.9) rotate(-8deg);
    transform: scale(.9) rotate(-8deg);
  }

  30%,
  50%,
  70% {
    -webkit-transform: scale(1.3) rotate(8deg);
    transform: scale(1.3) rotate(8deg);
  }

  40%,
  60% {
    -webkit-transform: scale(1.3) rotate(-8deg);
    transform: scale(1.3) rotate(-8deg);
  }

  100%,
  80% {
    -webkit-transform: scale(1) rotate(0);
    transform: scale(1) rotate(0);
  }
}

@-webkit-keyframes tada {
  0% {
    -webkit-transform: scale(1);
    transform: scale(1);
  }

  10%,
  20% {
    -webkit-transform: scale(.9) rotate(-8deg);
    transform: scale(.9) rotate(-8deg);
  }

  30%,
  50%,
  70% {
    -webkit-transform: scale(1.3) rotate(8deg);
    transform: scale(1.3) rotate(8deg);
  }

  40%,
  60% {
    -webkit-transform: scale(1.3) rotate(-8deg);
    transform: scale(1.3) rotate(-8deg);
  }

  100%,
  80% {
    -webkit-transform: scale(1) rotate(0);
    transform: scale(1) rotate(0);
  }
}
</style>

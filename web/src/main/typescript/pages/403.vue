<template>
  <div>
    <div id="app">
      <div>403</div>
      <div class="txt">Forbidden<span class="blink">_</span></div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import url("https://fonts.googleapis.com/css?family=Press+Start+2P");

$color: #54fe55;
$color2: #1a4f1a;
$glowSize: 10px;

html,
body {
  width: 100%;
  margin: 0;
}

* {
  font-family: "Press Start 2P", cursive;
  box-sizing: border-box;
}

#app {
  padding: 1rem;
  background: black;
  display: flex;
  height: 100vh;
  justify-content: center;
  align-items: center;
  color: $color;
  text-shadow: 0 0 $glowSize;
  font-size: 6rem;
  flex-direction: column;

  .txt {
    font-size: 1.8rem;
  }
}

@keyframes blink {
  0% {
    opacity: 0;
  }

  49% {
    opacity: 0;
  }

  50% {
    opacity: 1;
  }

  100% {
    opacity: 1;
  }
}

.blink {
  animation-name: blink;
  animation-duration: 1s;
  animation-iteration-count: infinite;
}
</style>
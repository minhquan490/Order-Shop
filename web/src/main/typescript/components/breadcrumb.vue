<script lang="ts" setup>
const paths = useRoute().path.split('/').filter(path => path.length !== 0);
const rootPath = `/${paths[0]}`;

const calculatePath = (index: number) => {
  if (index === 0) {
    return rootPath;
  }
  let path = rootPath;
  for (let i = 1; i <= index; i++) {
    path = path.concat(`/${paths[i]}`);
  }
  return path;
};
</script>

<template>
  <section>
    <nav class="w-2/5">
      <ol class="cd-breadcrumb custom-separator custom-icons">
        <li v-for="(path, i) in paths">
          <a v-if="i === paths.length -1" :href="calculatePath(i)" class="current">
            <em v-text="path"></em>
          </a>
          <a v-else :href="calculatePath(i)" v-text="path"></a>
        </li>
      </ol>
    </nav>
  </section>
</template>

<style scoped>
.cd-breadcrumb::after, .cd-multi-steps::after {
  clear: both;
  content: "";
  display: table;
}
.cd-breadcrumb li, .cd-multi-steps li {
  display: inline-block;
  margin: 0.5em 0;
}
.cd-breadcrumb li::after, .cd-multi-steps li::after {
  /* this is the separator between items */
  display: inline-block;
  content: '\00bb';
  margin: 0 .6em;
  color: #959fa5;
}
.cd-breadcrumb li:last-of-type::after, .cd-multi-steps li:last-of-type::after {
  /* hide separator after the last item */
  display: none;
}
.cd-breadcrumb li > *, .cd-multi-steps li > * {
  /* single step */
  display: inline-block;
  font-size: 0.8rem;
  color: #2c3f4c;
}
.cd-breadcrumb li.current > *, .cd-multi-steps li.current > * {
  /* selected step */
  color: #96c03d;
}
.no-touch .cd-breadcrumb a:hover, .no-touch .cd-multi-steps a:hover {
  /* steps already visited */
  color: #96c03d;
}
.cd-breadcrumb.custom-separator li::after, .cd-multi-steps.custom-separator li::after {
  /* replace the default separator with a custom icon */
  content: '';
  height: 16px;
  width: 16px;
  background: url(/cd-custom-separator.svg) no-repeat center center;
  vertical-align: middle;
}
.cd-breadcrumb.custom-icons li:not(.current):nth-of-type(2) > *::before, .cd-multi-steps.custom-icons li:not(.current):nth-of-type(2) > *::before {
  /* change custom icon using image sprites */
  background-position: -20px 0;
}
.cd-breadcrumb.custom-icons li:not(.current):nth-of-type(3) > *::before, .cd-multi-steps.custom-icons li:not(.current):nth-of-type(3) > *::before {
  background-position: -40px 0;
}
.cd-breadcrumb.custom-icons li:not(.current):nth-of-type(4) > *::before, .cd-multi-steps.custom-icons li:not(.current):nth-of-type(4) > *::before {
  background-position: -60px 0;
}
.cd-breadcrumb.custom-icons li.current:first-of-type > *::before, .cd-multi-steps.custom-icons li.current:first-of-type > *::before {
  /* change custom icon for the current item */
  background-position: 0 -20px;
}
.cd-breadcrumb.custom-icons li.current:nth-of-type(2) > *::before, .cd-multi-steps.custom-icons li.current:nth-of-type(2) > *::before {
  background-position: -20px -20px;
}
.cd-breadcrumb.custom-icons li.current:nth-of-type(3) > *::before, .cd-multi-steps.custom-icons li.current:nth-of-type(3) > *::before {
  background-position: -40px -20px;
}
.cd-breadcrumb.custom-icons li.current:nth-of-type(4) > *::before, .cd-multi-steps.custom-icons li.current:nth-of-type(4) > *::before {
  background-position: -60px -20px;
}
</style>

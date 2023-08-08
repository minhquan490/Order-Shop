<script setup lang="ts">
import {AppError} from "~/types";

const props = defineProps({
  error: AppError
})

const navigate = useNavigation().value;

console.log(props.error?.stack);

if (props.error?.statusCode === 404) {
  navigate('/404', true, 301);
}

if (props.error?.statusCode === 403) {
  navigate('/403', true, 301);
}
</script>
<template></template>

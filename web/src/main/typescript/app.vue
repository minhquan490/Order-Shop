<script lang="ts" setup>
import { HttpServiceProvider } from '~/services/http-service';
import { DefaultChunkFileUploadService } from '~/services/implementer/default-chunk-file-upload-service';
import { HttpJsonOpener } from '~/services/implementer/http-json-opener';
import { JsonStringConverter, ObjectConverter } from '~/services/implementer/json-converter';

const httpServiceProvider: HttpServiceProvider = new HttpJsonOpener(new JsonStringConverter(), new ObjectConverter());
provide('httpClient', httpServiceProvider);
provide('fileUploadService', new DefaultChunkFileUploadService(httpServiceProvider));
</script>

<template>
  <div>
    <NuxtLayout>
      <NuxtPage/>
    </NuxtLayout>
  </div>
</template>
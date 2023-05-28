<script lang="ts" setup>
import { AuthService } from '~/services/auth.service';
import { HttpServiceProvider } from '~/services/http.service.js';
import { DefaultAuthService } from '~/services/implementer/default-auth-service';
import { DefaultChunkFileUploadService } from '~/services/implementer/default-chunk-file-upload-service';
import { HttpJsonOpener } from '~/services/implementer/http-json-opener';
import { JsonStringConverter, ObjectConverter } from '~/services/implementer/json-converter';
import { LoginServiceImpl } from '~/services/implementer/login-service-impl';

const httpServiceProvider: HttpServiceProvider = new HttpJsonOpener(new JsonStringConverter(), new ObjectConverter());
const authService: AuthService = new DefaultAuthService();

provide('authService', authService);
provide('httpClient', httpServiceProvider);
provide('fileUploadService', new DefaultChunkFileUploadService(httpServiceProvider));
provide('loginService', new LoginServiceImpl(httpServiceProvider, authService));

const checkDevice = () => {
  const headers = useRequestHeaders();
  if (headers['Sec-Ch-Ua-Mobile'.toLocaleLowerCase()] === "?1") {
    return ref("mobile");
  } else {
    return ref("pc");
  }
}

provide('device', checkDevice);

</script>

<template>
  <div>
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>
<script lang="ts" setup>
import MobileDetect from 'mobile-detect';
import { AuthService } from '~/services/auth.service';
import { HttpServiceProvider } from '~/services/http.service.js';
import { DefaultAuthService } from '~/services/implementer/default-auth-service';
import { DefaultChunkFileUploadService } from '~/services/implementer/default-chunk-file-upload-service';
import { HttpJsonOpener } from '~/services/implementer/http-json-opener';
import { JsonStringConverter, ObjectConverter } from '~/services/implementer/json-converter';
import { LoginServiceImpl } from '~/services/implementer/login-service-impl';
import { ForgotPasswordServiceImpl } from './services/implementer/forgot-password-service-impl';
import { RegisterServiceImpl } from './services/implementer/register-service';

const checkDevice = () => {
  const headers = useRequestHeaders();
  const detector = new MobileDetect(headers['user-agent']);
  const isMobile = detector.phone() !== null || detector.mobile() === 'UnknownMobile';
  if (isMobile) {
    return ref("mobile");
  } else {
    return ref("pc");
  }
}

const httpServiceProvider: HttpServiceProvider = new HttpJsonOpener(new JsonStringConverter(), new ObjectConverter());
const authService: AuthService = new DefaultAuthService();

provide('authService', authService);
provide('httpClient', httpServiceProvider);
provide('fileUploadService', new DefaultChunkFileUploadService(httpServiceProvider));
provide('loginService', new LoginServiceImpl(httpServiceProvider, authService));
provide('forgotPasswordService', new ForgotPasswordServiceImpl(httpServiceProvider));
provide('registerService', new RegisterServiceImpl(httpServiceProvider));
provide('device', checkDevice);

</script>

<template>
  <div>
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>
import { fileURLToPath, URL } from 'node:url';

import vue from '@vitejs/plugin-vue';
import vueJsx from '@vitejs/plugin-vue-jsx';
import { ConfigEnv, defineConfig, UserConfigExport } from 'vite';

const virtualFile = '@virtual-file';
const virtualId = '\0' + virtualFile;
const nestedVirtualFile = '@nested-virtual-file';
const nestedVirtualId = '\0' + nestedVirtualFile;

const base = process.env.BASE_URL;

// https://vitejs.dev/config/
export default function (configEnv: ConfigEnv): UserConfigExport {
  return defineConfig({
    plugins: [
      vue(), 
      vueJsx(),
      {
        name: 'virtual',
        resolveId(id) {
          if (id === '@foo') {
            return id;
          }
        },
        load(id, options) {
          const ssrFromOptions = options?.ssr ?? false;
          if (id === '@foo') {
            return `export default { msg: '${configEnv.command === 'build' && !!configEnv.ssrBuild !== ssrFromOptions ? `defineConfig ssrBuild !== ssr from load options` : 'hi'}' }`;
          }
        },
      },
      {
        name: 'virtual-module',
        resolveId(id) {
          if (id === virtualFile) {
            return virtualId;
          } else if (id === nestedVirtualFile) {
            return nestedVirtualId;
          }
        },
        load(id) {
          if (id === virtualId) {
            return `export { msg } from "@nested-virtual-file";`;
          } else if (id === nestedVirtualId) {
            return `export const msg = "[success] from conventional virtual file"`;
          }
        },
      },
      // Example of a plugin that injects a helper from a virtual module that can
      // be used in renderBuiltUrl
      (function () {
        const queryRE = /\?.*$/s;
        const hashRE = /#.*$/s;
        const cleanUrl = (url: string) => url.replace(hashRE, '').replace(queryRE, '');
        let config: any;
  
        const virtualId = '\0virtual:ssr-vue-built-url';
        return {
          name: 'built-url',
          enforce: 'post',
          configResolved(_config) {
            config = _config;
          },
          resolveId(id) {
            if (id === virtualId) {
              return id;
            }
          },
          load(id) {
            if (id === virtualId) {
              return {
                code: `export const __ssr_vue_processAssetPath = (url) => '${base}' + url`,
                moduleSideEffects: 'no-treeshake',
              };
            }
          },
          transform(code, id) {
            const cleanId = cleanUrl(id);
            if (
              config.build.ssr &&
              (cleanId.endsWith('.js') || cleanId.endsWith('.vue')) &&
              !code.includes('__ssr_vue_processAssetPath')
            ) {
              return {
                code: `import { __ssr_vue_processAssetPath } from '${virtualId}';__ssr_vue_processAssetPath;${code}`,
                sourcemap: null, // no sourcemap support to speed up CI
              };
            }
          },
        }
      })(),
    ],
    experimental: {
      renderBuiltUrl(filename, { hostType, type, ssr }) {
        if (ssr && type === 'asset' && hostType === 'js') {
          return {
            runtime: `__ssr_vue_processAssetPath(${JSON.stringify(filename)})`,
          };
        }
      },
    },
    build: {
      cssMinify: true,
    },
    ssr: {
      noExternal: [
        
      ],
    },
    resolve: {
      alias: {
        '@assets': fileURLToPath(new URL('./src/client/assets', import.meta.url)),
        '@components': fileURLToPath(new URL('./src/client/components', import.meta.url)),
        '@core': fileURLToPath(new URL('./src/client/common/core', import.meta.url)),
        '@router': fileURLToPath(new URL('./src/client/router', import.meta.url)),
        '@services': fileURLToPath(new URL('./src/client/common/services', import.meta.url)),
        '@stores': fileURLToPath(new URL('./src/client/stores', import.meta.url)),
        '@styles': fileURLToPath(new URL('./src/client/common/styles', import.meta.url)),
        '@types': fileURLToPath(new URL('./src/client/common/types', import.meta.url)),
        '@views': fileURLToPath(new URL('./src/client/views', import.meta.url)),
      }
    }
  });
}

/* eslint-disable no-undef */
import express from 'express';
import { readFileSync } from 'node:fs';
import { resolve as _resolve, dirname } from 'node:path';
import spdy from 'spdy';
import { fileURLToPath } from 'url';

const isTest = process.env.VITEST;
const baseUrl = process.env.BASE_URL;
const serverEntry = './src/server/entry-server.js';
const builtServerEntry = './dist/server/entry-server.js';

async function createServer() {
    const root = process.cwd();
    const isProd = process.env.NODE_ENV === 'production';
    const __dirname = dirname(fileURLToPath(import.meta.url));
    const resolve = (p) => _resolve(__dirname, p);

    const indexProd = isProd ? readFileSync(resolve('dist/client/index.html'), 'utf-8') : '';
    const manifest = isProd ? JSON.parse(readFileSync(resolve('dist/client/ssr-manifest.json'), 'utf-8')) : {};

    const app = express();

    let vite;

    if (!isProd) {
        vite = await (await import('vite')).createServer({
            base: baseUrl,
            root,
            logLevel: isTest ? 'error' : 'info',
            server: {
                middlewareMode: true,
                watch: {
                    usePolling: true,
                    interval: 100,
                },
                hmr: {
                    host: process.env.HOST,
                    port: (process.env.PORT_DEV) ? Number.parseInt(process.env.PORT_DEV) : 0,
                },
                https: {
                    cert: readFileSync((process.env.SSL_CERT) ? process.env.SSL_CERT : ''),
                    key: readFileSync((process.env.SSL_KEY) ? process.env.SSL_KEY : ''),
                }
            },
            appType: 'custom',
        });
        app.use(vite.middlewares);
    } else {
        app.use((await import('compression')).default());
        app.use((baseUrl) ? baseUrl : '', (await import('serve-static')).default(resolve('dist/client'), {
            index: false,
        }));
    }

    app.use('/api', (req, res) => {
        console.log("Test called");
        res.status(404).end();
    })

    app.use('*', async (req, res) => {
        try {
            let url;
            const originalUrl = req.originalUrl;
            if (originalUrl.startsWith('/') && originalUrl.length > 1) {
                url = originalUrl;
            } else {
                url = originalUrl.replace(originalUrl, '/');
            }

            let template;
            let render;
            if (!isProd) {
                template = readFileSync(resolve('index.html'), 'utf-8');
                if (!vite) {
                    throw new Error('Server is undefined');
                }
                template = await vite.transformIndexHtml(url, template);
                render = (await vite.ssrLoadModule(serverEntry)).render;
            } else {
                template = indexProd;
                
                // @ts-ignore
                render = (await import(builtServerEntry)).render;
            }

            const [html, preloadLinks] = await render(url, manifest);
            const htmlString = template.replace('<!-- app-content -->', html).replace('<!--links-->', preloadLinks);
            res.status(200).contentType('text/html').end(htmlString);
        } catch(e) {
            vite && vite.ssrFixStacktrace(e);
            console.log(e.stack);
            res.status(500).end(e.stack);
        }
    });
    return { app, vite };
}

if (!isTest) {
    createServer().then((server) => {
        let port;
        if (process.env.PORT) {
            port = Number.parseInt(process.env.PORT);
        } else {
            throw new Error("Can not config port for server");
        }
        let host;
        if (process.env.HOST) {
            host = process.env.HOST;
        } else {
            throw new Error("Can not config address for server");
        }

        const option = {
            key: readFileSync((process.env.SSL_KEY) ? process.env.SSL_KEY : ''),
            cert: readFileSync((process.env.SSL_CERT) ? process.env.SSL_CERT : ''),
            spdy: {
                protocols: ['h2'],
                plain: false
            }
        };
        spdy.createServer(option, server.app).listen(port, host);
    })
    .catch(e => console.log(e));
}
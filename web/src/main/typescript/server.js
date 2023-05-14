/* eslint-disable no-undef */

const isTest = process.env.VITEST;
const baseUrl = process.env.BASE_URL;
const serverEntry = './src/server/entry-server.js';
const builtServerEntry = './dist/server/entry-server.js';

const express = require('express');
const path = require('node:path');
const readFileSync = require('node:fs').readFileSync;
const _resolve = require('node:path').resolve;
const dirname = require('node:path').dirname;
const spdy = require('spdy');

async function createServer() {
    const root = process.cwd();
    const isProd = process.env.NODE_ENV === 'production';
    const dir = dirname(path.join(__dirname, 'server.js'));
    const resolve = (p) => _resolve(dir, p);

    const indexProd = isProd ? readFileSync(resolve('dist/client/index.html'), 'utf-8') : '';
    const manifest = isProd
        ? JSON.parse(readFileSync(resolve('dist/client/ssr-manifest.json'), 'utf-8'))
        : {};

    const app = express();

    let vite;
    let ssrModule;
    let grpcFunction;

    if (!isProd) {
        vite = await require('vite').createServer({
            base: baseUrl,
            root,
            logLevel: isTest ? 'error' : 'info',
            server: {
                middlewareMode: true,
                watch: {
                    usePolling: true,
                    interval: 100
                },
                hmr: {
                    host: process.env.HOST,
                    port: process.env.PORT_DEV ? Number.parseInt(process.env.PORT_DEV) : 0
                },
                https: {
                    cert: readFileSync(process.env.SSL_CERT ? process.env.SSL_CERT : ''),
                    key: readFileSync(process.env.SSL_KEY ? process.env.SSL_KEY : '')
                }
            },
            appType: 'custom'
        });
        app.use(vite.middlewares);
        ssrModule = await vite.ssrLoadModule(serverEntry);
    } else {
        app.use(require('compression').default());
        app.use(
            baseUrl ? baseUrl : '',
            require('serve-static').default(resolve('dist/client'), {
                index: false
            })
        );
        ssrModule = await vite.ssrLoadModule(builtServerEntry);
        grpcFunction = await ssrModule.createGrpcConnector;
    }

    app.use('/api(/*)?', (req, res) => {
        console.log('Test called');
        if (!grpcFunction) {
            res.status(404).end();
        }
        req.body
    });

    app.use('*', async (req, res) => {
        const render = ssrModule.render;
        try {
            let template;
            let url;
            const originalUrl = req.originalUrl;
            if (originalUrl.startsWith('/') && originalUrl.length > 1) {
                url = originalUrl;
            } else {
                url = originalUrl.replace(originalUrl, '/');
            }
            if (!isProd) {
                template = readFileSync(resolve('index.html'), 'utf-8');
                if (!vite) {
                    throw new Error('Server is undefined');
                }
                template = await vite.transformIndexHtml(url, template);
            } else {
                template = indexProd;
            }

            const [html, preloadLinks] = await render(url, manifest);
            const htmlString = template
                .replace('<!-- app-content -->', html)
                .replace('<!--links-->', preloadLinks);
            res.status(200).contentType('text/html').end(htmlString);
        } catch (e) {
            vite && vite.ssrFixStacktrace(e);
            console.log(e.stack);
            res.status(500).end(e.stack);
        }
    });
    return { app, vite };
}

if (!isTest) {
    createServer()
        .then((server) => {
            let port;
            if (process.env.PORT) {
                port = Number.parseInt(process.env.PORT);
            } else {
                throw new Error('Can not config port for server');
            }
            let host;
            if (process.env.HOST) {
                host = process.env.HOST;
            } else {
                throw new Error('Can not config address for server');
            }

            const option = {
                key: readFileSync(process.env.SSL_KEY ? process.env.SSL_KEY : ''),
                cert: readFileSync(process.env.SSL_CERT ? process.env.SSL_CERT : ''),
                spdy: {
                    protocols: ['h2'],
                    plain: false
                }
            };
            spdy.createServer(option, server.app).listen(port, host);
        })
        .catch((e) => console.log(e));
}

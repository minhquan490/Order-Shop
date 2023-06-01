export default defineEventHandler((event) => {
  
  const requestFrom = event.node.req.headers["request-from"];

  if (!requestFrom || requestFrom !== 'XMLHttpRequest') {
    event.node.res.statusCode = 404;
    return event.node.res.end();
  }

  const handler = globalThis.httpMessageHandler;

  return handler.handle(event.node.req, event.node.res);
})

export default eventHandler(async (event) => {
  const requestFrom = event.node.req.headers["request-from"];
  // if (!requestFrom || requestFrom !== 'XMLHttpRequest') {
  //   event.node.res.statusCode = 404;
  //   return event.node.res.end();
  // }
  const request = event.node.req;
  const headers = new Headers();
  for (const head in request.headers) {
    headers.set(head, request.headers[head] as string);
  };
  headers.set('Sec-Client-UA', '?1');
  const res = await $fetch('https://localhost:8443/login', {
    method: 'POST',
    body: request.read(),
    headers: headers,
    keepalive: true
  });
  return res;
})

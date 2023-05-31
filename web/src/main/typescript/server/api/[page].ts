
export default eventHandler(async (event) => {
  
  const requestFrom = event.node.req.headers["request-from"];

  if (!requestFrom || requestFrom !== 'XMLHttpRequest') {
    event.node.res.statusCode = 404;
    return event.node.res.end();
  }

  const handler = globalThis.httpMessageHandler;
  const data = await handler.handle(event.node.req, event.node.res);
  console.log(data)
  
  return send(event, data);
  // return res.then(json => {
  //   if (typeof json === 'string') {

  //     return Promise.resolve(JSON.parse(json));
  //   } else {
  //     return json;
  //   }
  // });
})

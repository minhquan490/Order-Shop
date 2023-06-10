export default defineEventHandler((event) => {
  if (process.env.NODE_ENV == "production") {
    const url = event.node.req.url as string;
    const maxage = url.match(/(.+)\.(jpg|jpeg|gif|png|ico|svg|css|js|mjs)/)
      ? 60 * 60 * 12 * 30
      : 60 * 60;
    appendHeader(event, "Cache-Control", `max-age=${maxage},immutable`);
  }
});

const { legacyCreateProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(
    '/api',
    legacyCreateProxyMiddleware({
      target: 'http://backend:8090',
      changeOrigin: true,
    })
  );
  app.use(
    '/api2',
    legacyCreateProxyMiddleware({
      target: 'http://localhost:4000',
      changeOrigin: true,
    })
  );
};
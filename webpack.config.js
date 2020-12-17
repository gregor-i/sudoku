module.exports = (env, options) => {
  const devMode = options.mode !== 'production';

  return {
    entry: {
      app: [
        devMode ? './frontend/target/scala-2.13/frontend-fastopt.js' :  './frontend/target/scala-2.13/frontend-opt.js'
      ]
    },
    output: {
      filename: '../build/app.js',
      publicPath: '/'
    },
    devtool: devMode ? 'source-map' : undefined
  }
}

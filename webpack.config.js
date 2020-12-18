module.exports = (env, options) => {
  const devMode = options.mode !== 'production';

  return {
    entry: {
      app: [
        devMode
          ? './frontend/target/scala-2.13/frontend-fastopt/main.js'
          : './frontend/target/scala-2.13/frontend-opt/main.js'
      ]
    },
    output: {
      filename: '../build/app.js',
      publicPath: '/'
    },
    devtool: undefined
  }
}

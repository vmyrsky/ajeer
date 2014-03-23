// Karma (test) configuration

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: './src/',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser (same as you specify in your 'index.html' [phonebook.html])
    // + some extra for testing
    // Note: The order of loading stuff is relevant
    files: [
      'main/webapp/app/lib/jquery/jquery-1.10.2.js',
      'main/webapp/app/lib/jquery/jquery-ui-1.10.3.js',
      'main/webapp/app/lib/angular/angular.js',
      'main/webapp/app/lib/angular/angular-mocks.js',
      'main/webapp/app/lib/angular/angular-translate.min.js',
      'main/webapp/app/lib/angular/angular-translate-loader-static-files.min.js',
      'main/webapp/app/lib/angular/angular-translate-loader-url.min.js',
      'main/webapp/app/js/filters/I18N.js',
      'main/webapp/app/lib/angular/angular-route.js',
      'main/webapp/app/js/App.js',
      'main/webapp/app/js/services/RestServices.js',
      'main/webapp/app/js/services/ShareDataService.js',
      'main/webapp/app/js/controllers/MessageControllers.js',
      'main/webapp/app/js/controllers/MainControllers.js',
      'main/webapp/app/js/controllers/DetailControllers.js',
      'main/webapp/app/js/directives/AddDirectives.js',
      // Chai enables the tests and expectjs provides something to test with
      // Require.js is to make these available in tests
      'test/webapp/app/lib/require.js',
      'test/webapp/app/lib/assert.js',
      'test/webapp/app/lib/chai.js',
      'test/webapp/app/lib/expect.js',
      // The test-main.js enables the require.js
//      'test/webapp/app/js/test-main.js',
      // The test files to execute
      'test/webapp/app/js/AppTest.js'
    ],


    // list of files to exclude
    exclude: [
      
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
    
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['Firefox'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false
  });
};

basePath = '../../../';

files = [
  JASMINE,
  JASMINE_ADAPTER,
  'main/webapp/resources/js/jquery/jquery.js',
  'main/webapp/resources/js/angular/angular.js',
  'main/webapp/resources/js/angular/angular-resource.js',
  'main/webapp/resources/js/ui-bootstrap/ui-bootstrap-2.0.0.js',
  'main/webapp/resources/js/underscore/underscore-min.js',
  'test/js/lib/angular/angular-mocks.js',
  'main/webapp/resources/js/custom/**/*.js',
  'test/js/unit/**/*.js'
];

autoWatch = true;

frameworks = ['Jasmine'];

browsers = ['PhantomJS'];

junitReporter = {
  outputFile: 'test_out/unit.xml',
  suite: 'unit'
};

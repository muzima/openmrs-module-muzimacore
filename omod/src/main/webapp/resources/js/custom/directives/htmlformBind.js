muzimaCoreModule.directive("htmlFormBind", function($compile) {
  return function(scope, elm, attrs) {
    scope.$watch(attrs.htmlFormBind, function(newValue, oldValue) {
      if (newValue && newValue !== oldValue) {
        elm.html(newValue);
        $compile(elm.contents())(scope);
      }
    });
  };
});

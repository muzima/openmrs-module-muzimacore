muzimaCoreModule.directive("sideNavigation", function () {
    return {
        restrict: 'E',
        scope: {
            menuItem: '@'
        },
        link: function(scope, element, attrs) {
          scope.closeTour = function() {
             scope.currentStep = 25;
          };

          scope.openTour = function() {
             scope.currentStep = 0;
          };

          scope.postTourCallback = function() {
            scope.currentStep = 25;
          };

          if(scope.currentStep == undefined || scope.currentStep == null || scope.currentStep ==''){
            scope.currentStep = 25;
          }
        },
        templateUrl: '../../moduleResources/muzimacore/partials/directives/_side_nav.html'
    };
});

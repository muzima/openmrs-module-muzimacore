muzimaCoreModule.directive("sideNavigation", function ($localeService, $translate) {
    return {
        restrict: 'E',
        scope: {
            menuItem: '@'
        },
        link: function(scope, element, attrs) {
          $localeService.getUserLocale().
          then(function (response) {
              var serverData = response.data.locale;
              $translate.use(serverData);
          });

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

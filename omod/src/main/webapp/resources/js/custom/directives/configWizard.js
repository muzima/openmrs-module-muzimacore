muzimaCoreModule.directive('configWizard', function() {
    return {
        restrict: 'E',
        transclude: false, // Insert custom content inside the directive
        link: function(scope, element, attrs) {
            scope.dialogStyle = {};
            if (attrs.boxWidth) {
                scope.dialogStyle.width = attrs.boxWidth;
            }
            if (attrs.boxHeight) {
                scope.dialogStyle.height = attrs.boxHeight;
            }
            scope.hideModal = function() {
                scope.showConfigWizard = false;
            };
        },
        templateUrl: '../../moduleResources/muzimacore/partials/directives/configWizard.html'
    };
});
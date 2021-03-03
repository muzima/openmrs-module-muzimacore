muzimaCoreModule.directive('configWizardSideNav', function() {
    return {
        restrict: 'E',
        scope: {
            wizardStage: '='
        },
        templateUrl:'../../moduleResources/muzimacore/partials/directives/configWizardSideNav.html'
    }
});
muzimaCoreModule.directive("sideNavigation", function () {
    return {
        restrict: 'E',
        scope: {
            menuItem: '@'
        },
        templateUrl: '../../moduleResources/muzimacore/partials/directives/_side_nav.html'
    };
});

muzimaCoreModule.directive('openmrsFormCreator', function(FormService,) {
    return {
        restrict: 'E',
        link:function(scope){

            FormService.getEncounterTypes().then(function (response) {
                scope.encounterTypes = response.data.results;
            });

            var showErrorMessage = function (content, cl, time) {
                $('<div/>')
                    .addClass('alert')
                    .addClass('alert-error')
                    .hide()
                    .fadeIn('slow')
                    .delay(time)
                    .appendTo('#error-alert')
                    .text(content);
            };

            scope.saveFormMetaData = function () {

            }
        },
        templateUrl: '../../moduleResources/muzimacore/partials/directives/openmrsFormCreator.html'
    };
});
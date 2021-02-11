muzimaCoreModule.directive('openmrsFormCreator', function(FormService,) {
    return {
        restrict: 'E',
        link:function(scope){

            FormService.getEncounterTypes().then(function (response) {
                scope.encounterTypes = response.data.results;
            });

            scope.resetNewFormCreationFields();

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

            scope.isRequiredNewFormFieldsEntered = function(){
                return scope.name.trim() != '' && scope.version.trim() != '' && scope.encounterType != '';
            }

            scope.saveFormMetaData = function () {

            }
        },
        templateUrl: '../../moduleResources/muzimacore/partials/directives/openmrsFormCreator.html'
    };
});
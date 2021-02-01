muzimaCoreModule.directive('cohortDefinitionCreator', function($cohortDefinitionService) {
    return {
        restrict: 'E',
        link:function(scope){
            cohortDefinition = {};
            scope.saveCohortAndCohortDefinition = function (cohort) {
                if(cohort.isMemberAdditionEnabled===undefined){
                    cohort.isMemberAdditionEnabled=false;
                }
                if(cohort.isMemberRemovalEnabled===undefined){
                    cohort.isMemberRemovalEnabled=false;
                }
                if(cohort.isFilterByProviderEnabled===undefined){
                    cohort.isFilterByProviderEnabled=false;
                }
                if(cohort.isFilterByLocationEnabled===undefined){
                    cohort.isFilterByLocationEnabled=false;
                }
                cohort.isScheduledForExecution = cohort.isMemberAdditionEnabled || cohort.isMemberRemovalEnabled;
                $cohortDefinitionService.saveCohortAndCohortDefinition(cohort.name, cohort.description, cohort.definition, cohort.isScheduledForExecution,
                    cohort.isMemberAdditionEnabled, cohort.isMemberRemovalEnabled, cohort.isFilterByProviderEnabled, cohort.isFilterByLocationEnabled,
                    cohort.filterQuery).success(function (response) {
                    if(response.hasOwnProperty('uuid')) {
                        cohort.uuid = response.uuid;
                        scope.setSelectedCohort(cohort);
                        scope.goToPreviousWizardTab();
                        cohortDefinition = {};
                    } else if(response.hasOwnProperty('error')) {
                        console.log(response.error)
                    }
                }).error(function (response) {
                    console.log(response)
                });
            };

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
        },
        templateUrl: '../../moduleResources/muzimacore/partials/directives/cohortDefinitionCreator.html'
    };
});
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
                        console.log("Save response success: "+JSON.stringify(response));
                    if(response.hasOwnProperty('uuid')) {
                        cohort.uuid = response.uuid;
                        scope.setSelectedCohort(cohort);
                        scope.exitCohortCreationTab();
                        cohortDefinition = {};
                    } else if(response.hasOwnProperty('error')) {
                        console.log(response.error)
                        console.log("Save response error: "+JSON.stringify(response));
                    }
                }).error(function (response) {
                    console.log(response)
                    console.log("Save response error2: "+JSON.stringify(response));
                });
            };

            scope.isAllRequiredFieldsEntered = function(){
                console.log("scope.enableDynamicCohort: "+scope.enableDynamicCohort);
                console.log("scope.enableCohortFilters: "+scope.enableCohortFilters);
                console.log("scope.name.trim(): "+scope.cohortDefinition.name.trim());
                console.log("scope.description.trim(): "+scope.cohortDefinition.description.trim());
                return (scope.enableDynamicCohort == false || scope.enableDynamicCohort !== false
                    && scope.enableCohortFilters != undefined) && scope.cohortDefinition != undefined
                    && scope.cohortDefinition.name.trim() != '' && scope.cohortDefinition.description.trim() != '';
            }

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
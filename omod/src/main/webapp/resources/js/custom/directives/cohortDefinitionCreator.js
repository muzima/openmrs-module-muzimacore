muzimaCoreModule.directive('cohortDefinitionCreator', function($cohortDefinitionService) {
    return {
        restrict: 'E',
        link:function(scope){
            cohortDefinition = {};
            scope.locations;
            scope.isLocationParameter = false;
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
                if(cohort.definition !== undefined){
                    cohort.definition = cohort.definition.replaceAll(":location",cohort.location);
                }
                $cohortDefinitionService.saveCohortAndCohortDefinition(cohort.name, cohort.description, cohort.definition, cohort.isScheduledForExecution,
                    cohort.isMemberAdditionEnabled, cohort.isMemberRemovalEnabled, cohort.isFilterByProviderEnabled, cohort.isFilterByLocationEnabled,
                    cohort.filterQuery).success(function (response) {
                    if(response.hasOwnProperty('uuid')) {
                        cohort.uuid = response.uuid;
                        scope.setSelectedCohort(cohort);
                        scope.exitCohortCreationTab();
                        cohortDefinition = {};
                    } else if(response.hasOwnProperty('error')) {
                        console.log("Save response error: "+JSON.stringify(response));
                    }
                }).error(function (response) {
                    console.log("Save response error2: "+JSON.stringify(response));
                });
            };

            $cohortDefinitionService.getAllLocations().
                then(function (response) {
                    var serverData = response.data;
                    scope.locations = serverData.objects;
                });

            scope.$watch('cohortDefinition.definition', function (newValue, oldValue) {
                if (newValue != oldValue) {
                    if(newValue === undefined){
                        scope.isLocationParameter = false;
                    } else if(newValue.includes(":location")){
                        scope.isLocationParameter = true;
                    }else{
                        scope.isLocationParameter = false;
                    }
                }
            }, true);


            scope.isAllRequiredFieldsEntered = function(){
                return (scope.enableDynamicCohort == false || scope.enableDynamicCohort !== false
                    && scope.enableCohortFilters != undefined) && scope.cohortDefinition != undefined
                    && scope.cohortDefinition.name.trim() != '' && scope.cohortDefinition.description.trim() != ''
                    && scope.validateDefinitionQuery() && scope.validateFilterQuery() && scope.validateLocationParameter();
            }

            scope.validateDefinitionQuery = function(){
                if(scope.cohortDefinition.isMemberAdditionEnabled != undefined || scope.cohortDefinition.isMemberRemovalEnabled != undefined ){
                    return scope.cohortDefinition.definition !=undefined && scope.cohortDefinition.definition.trim() != '';
                }else{
                  return true;
                }
            }

            scope.validateFilterQuery = function(){
                if(scope.cohortDefinition.isFilterByProviderEnabled != undefined || scope.cohortDefinition.isFilterByLocationEnabled != undefined ){
                    return scope.cohortDefinition.filterQuery !=undefined && scope.cohortDefinition.filterQuery.trim() != '';
                }else{
                  return true;
                }
            }

            scope.validateLocationParameter = function(){
                if(scope.isLocationParameter){
                    return scope.cohortDefinition.location != undefined;
                }else{
                  return true;
                }
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
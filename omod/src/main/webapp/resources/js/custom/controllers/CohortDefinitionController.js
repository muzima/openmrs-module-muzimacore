function CohortDefinitionsCtrl($scope, $location, $cohortDefinitionService){
// initialize the paging structure
$scope.maxSize = 10;
$scope.pageSize = 10;
$scope.currentPage = 1;
$scope.totalItems = 0;
$cohortDefinitionService.getCohortDefinitions($scope.currentPage, $scope.pageSize).
        then(function (response) {
            var serverData = response.data;
                $scope.cohortDefinitions = serverData.objects;
                $scope.noOfPages = serverData.pages;
                $scope.totalItems = serverData.totalItems;
                $('#wait').hide();
        });

$scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $cohortDefinitionService.getCohortDefinitions($scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.cohortDefinitions = serverData.objects;
                $scope.noOfPages = serverData.pages;
                $scope.totalItems = serverData.totalItems;
                $('#wait').hide();
            });
        }
    }, true);
}


function CohortDefinitionCtrl($scope, $routeParams, $location, $cohortDefinitionService){
    $scope.cohortDefinition = {};
    // initialize the view to be read only
    $scope.mode = "view";
    $scope.uuid = $routeParams.uuid;

    if ($scope.uuid === undefined) {
        $scope.mode = "edit";
        $cohortDefinitionService.getAllCohortsWithoutDefinition().
            then(function (response) {
                var serverData = response.data;
                $scope.cohorts = serverData.objects;
                $('#wait').hide();
                $('#processingFailure').hide();
                $('#processingSuccessful').hide();
            });
    } else {
        $cohortDefinitionService.getCohortDefinition($scope.uuid).
            then(function (response) {
                $scope.cohortDefinition = response.data;
            $('#wait').hide();
            $('#processingFailure').hide();
            $('#processingSuccessful').hide();
            });
        $cohortDefinitionService.getAllCohorts().
            then(function (response) {
                var serverData = response.data;
                $scope.cohorts = serverData.objects;
            });
    }

    $scope.edit = function () {
        $scope.mode = "edit";
    };

    $scope.cancel = function () {
        if ($scope.mode == "edit") {
            if ($scope.uuid === undefined) {
                $location.path("/cohortDefinitions");
            } else {
                $scope.mode = "view"
            }
        } else {
            $location.path("/cohortDefinitions");
        }
    };

    $scope.save = function (cohortDefinition) {
        if(cohortDefinition.isScheduledForExecution===undefined){
            cohortDefinition.isScheduledForExecution=false;
        }
        if(cohortDefinition.isMemberAdditionEnabled===undefined){
            cohortDefinition.isMemberAdditionEnabled=false;
        }
        if(cohortDefinition.isMemberRemovalEnabled===undefined){
            cohortDefinition.isMemberRemovalEnabled=false;
        }
        if(cohortDefinition.isFilterByProviderEnabled===undefined){
            cohortDefinition.isFilterByProviderEnabled=false;
        }
        if(cohortDefinition.isFilterByLocationEnabled===undefined){
            cohortDefinition.isFilterByLocationEnabled=false;
        }
        $cohortDefinitionService.saveCohortDefinition(cohortDefinition.uuid,cohortDefinition.cohortid, cohortDefinition.definition,
            cohortDefinition.isScheduledForExecution, cohortDefinition.isMemberAdditionEnabled, cohortDefinition.isMemberRemovalEnabled, cohortDefinition.isFilterByProviderEnabled, cohortDefinition.isFilterByLocationEnabled, cohortDefinition.filterQuery).
            then(function () {
                $location.path("/cohortDefinitions");
            })
    };

    $scope.toggleRetireCohortDefinition = function(){
        $scope.retireCohortDefinition = true;
    };

    $scope.delete = function (cohortDefinition) {
         if(cohortDefinition.isScheduledForExecution===undefined){
             cohortDefinition.isScheduledForExecution=false;
         }
         $cohortDefinitionService.deleteCohortDefinition(cohortDefinition.uuid,cohortDefinition.cohortid, cohortDefinition.definition,
            cohortDefinition.isScheduledForExecution, cohortDefinition.isMemberAdditionEnabled, cohortDefinition.isMemberRemovalEnabled, cohortDefinition.isFilterByProviderEnabled, cohortDefinition.isFilterByLocationEnabled,
            cohortDefinition.filterQuery, cohortDefinition.retireReason).
            then(function () {
                $location.path("/cohortDefinitions");
            });
    };

    $scope.cohortselected = function(cohortDefinition,cohorts){
                angular.forEach(cohorts,function(cohort,key){
                    if(cohortDefinition.cohortid==cohort.id){
                        cohortDefinition.description = cohort.description;
                        $scope.definitioninputdisabled=function(e){
                            return false;
                         };
                    }
                });

    }
    $scope.definitioninputdisabled=function(uuid){
        if(uuid){
            return false;
        }else{
            return true;
        }
    };
    $scope.definitionsavingdisabled=function(definition){
        if(definition){
            return false;
        }else{
            return true;
        }
    };

    $scope.processDefinition = function (cohortDefinition) {
         $('#wait').show();
         $cohortDefinitionService.processCohortDefinition(cohortDefinition.uuid).
            then(function (response) {
                $('#wait').hide();
                $('#processingSuccessful').show();
                console.log("success");
            },function (response) {
                $('#wait').hide();
                $('#processingFailure').show();
                console.log("fail");
            });
    };
}


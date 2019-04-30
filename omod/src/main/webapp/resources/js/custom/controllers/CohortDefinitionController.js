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
            });
    } else {
        $cohortDefinitionService.getCohortDefinition($scope.uuid).
            then(function (response) {
                $scope.cohortDefinition = response.data;
            $('#wait').hide();
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
        $cohortDefinitionService.saveCohortDefinition(cohortDefinition.uuid,cohortDefinition.cohortid, cohortDefinition.definition,
            cohortDefinition.isScheduledForExecution, cohortDefinition.isMemberAdditionEnabled, cohortDefinition.isMemberRemovalEnabled).
            then(function () {
                $location.path("/cohortDefinitions");
            })
    };

    $scope.delete = function () {
        $cohortDefinitionService.deleteSource($scope.uuid).
            then(function () {
                $location.path("/cohortDefinitions");
            })
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
}


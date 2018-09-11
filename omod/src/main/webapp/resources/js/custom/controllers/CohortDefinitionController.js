function CohortDefinitionsCtrl($scope, $location, $cohortDefinitionService){
$cohortDefinitionService.getCohortDefinitions().
        then(function (response) {
            var serverData = response.data;
            $scope.cohortdefinitions = serverData.objects;
            $scope.noOfPages =1;
        });
}
function CohortDefinitionCtrl($scope, $routeParams, $location, $cohortDefinitionService){
    $scope.cohortdefinition = {};
    // initialize the view to be read only
    $scope.mode = "view";
    $scope.uuid = $routeParams.uuid;

    if ($scope.uuid === undefined) {
        $scope.mode = "edit";
        $cohortDefinitionService.getAllCohortsWithoutDefinition().
            then(function (response) {
                var serverData = response.data;
                $scope.cohorts = serverData.objects;
            });
    } else {
        $cohortDefinitionService.getCohortDefinition($scope.uuid).
            then(function (response) {
                $scope.cohortdefinition = response.data;
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
                $location.path("/cohortdefinitions");
            } else {
                $scope.mode = "view"
            }
        } else {
            $location.path("/cohortdefinitions");
        }
    };

    $scope.save = function (cohortdefinition) {
        if(cohortdefinition.isscheduled===undefined){
            cohortdefinition.isscheduled=false;
        }
        $cohortDefinitionService.saveCohortDefinition(cohortdefinition.uuid,cohortdefinition.cohortid, cohortdefinition.definition,
            cohortdefinition.isscheduled, cohortdefinition.enableMemberAddition, cohortdefinition.enableMemberRemoval).
            then(function () {
                $location.path("/cohortdefinitions");
            })
    };

    $scope.delete = function () {
        $cohortDefinitionService.deleteSource($scope.uuid).
            then(function () {
                $location.path("/cohortdefinitions");
            })
    };
    $scope.cohortselected = function(cohortdefinition,cohorts){
                angular.forEach(cohorts,function(cohort,key){
                    if(cohortdefinition.cohortid==cohort.id){
                        cohortdefinition.description = cohort.description;
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


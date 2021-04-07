function DashboardCtrl($scope, $location, $dashboardService) {
    $scope.configs = 0;
    $scope.cohortdefinations = 0;
    $scope.reportConfigs = 0;
    $scope.encounterForms = 0;
    $scope.demographicsUpdateForms = 0;
    $scope.genericRegistrationForms = 0;
    $scope.personDemographicUpdateForms = 0;
    $scope.registrationForms = 0;
    $scope.relationshipForms = 0;
    $scope.encounterQueue = 0;
    $scope.demographicsUpdateQueue = 0;
    $scope.genericRegistrationQueue = 0;
    $scope.personDemographicUpdateQueue = 0;
    $scope.registrationQueue = 0;
    $scope.relationshipQueue = 0;
    $scope.individualObsQueue = 0;
    $scope.encounterError = 0;
    $scope.demographicsUpdateError = 0;
    $scope.genericRegistrationError = 0;
    $scope.personDemographicUpdateError = 0;
    $scope.registrationError = 0;
    $scope.relationshipError = 0;
    $scope.individualObsError = 0;
    var currentURL = $location.absUrl();
    $scope.baseURL = currentURL.substr(0, currentURL.indexOf('/module'));

    $dashboardService.getSetupConfigCount().
    then(function (response) {
        var serverData = response.data;
        $scope.configs = serverData;
    });

    $dashboardService.getCohortDefinitionCount().
    then(function (response) {
        var serverData = response.data;
        $scope.cohortdefinations = serverData;
    });

    $dashboardService.getReportConfigurationCount().
    then(function (response) {
        var serverData = response.data;
        $scope.reportConfigs = serverData;
    });

    $dashboardService.getMuzimaFormCountGroupedByDiscriminator().
    then(function (results) {
        var serverData = results.data.results;
        for(var i = 0; i < serverData.length; i++) {
            var obj = serverData[i];
            if(obj.discriminator === "json-encounter"){
                $scope.encounterForms = obj.count;
            }else if(obj.discriminator === "json-demographics-update"){
                $scope.demographicsUpdateForms = obj.count;
            }else if(obj.discriminator === "json-generic-registration"){
                $scope.genericRegistrationForms = obj.count;
            }else if(obj.discriminator === "json-person-demographics-update"){
                $scope.personDemographicUpdateForms = obj.count;
            }else if(obj.discriminator === "json-registration"){
                $scope.registrationForms = obj.count;
            }else if(obj.discriminator === "json-relationship"){
                $scope.relationshipForms = obj.count;
            }
        }
        $scope.muzimaForm = serverData.results;
    });

    $dashboardService.getQueueDataCountGroupedByDiscriminator().
    then(function (results) {
        var serverData = results.data.results;
        for(var i = 0; i < serverData.length; i++) {
            var obj = serverData[i];
            if(obj.discriminator === "json-encounter"){
                $scope.encounterQueue = obj.count;
            }else if(obj.discriminator === "json-demographics-update"){
                $scope.demographicsUpdateQueue = obj.count;
            }else if(obj.discriminator === "json-generic-registration"){
                $scope.genericRegistrationQueue = obj.count;
            }else if(obj.discriminator === "json-individual-obs"){
                $scope.individualObsQueue = obj.count;
            }else if(obj.discriminator === "json-person-demographics-update"){
                $scope.personDemographicUpdateQueue = obj.count;
            }else if(obj.discriminator === "json-registration"){
                $scope.registrationQueue = obj.count;
            }else if(obj.discriminator === "json-relationship"){
                $scope.relationshipQueue = obj.count;
            }
        }
    });

    $dashboardService.getErrorDataCountGroupedByDiscriminator().
    then(function (results) {
        var serverData = results.data.results;
        for(var i = 0; i < serverData.length; i++) {
            var obj = serverData[i];
            if(obj.discriminator === "json-encounter"){
                $scope.encounterError = obj.count;
            }else if(obj.discriminator === "json-demographics-update"){
                $scope.demographicsUpdateError = obj.count;
            }else if(obj.discriminator === "json-generic-registration"){
                $scope.genericRegistrationError = obj.count;
            }else if(obj.discriminator === "json-individual-obs"){
                $scope.individualObsError = obj.count;
            }else if(obj.discriminator === "json-person-demographics-update"){
                $scope.personDemographicUpdateError = obj.count;
            }else if(obj.discriminator === "json-registration"){
                $scope.registrationError = obj.count;
            }else if(obj.discriminator === "json-relationship"){
                $scope.relationshipError = obj.count;
            }
        }
    });
}
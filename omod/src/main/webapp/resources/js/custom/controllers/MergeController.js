function MergeCtrl($scope, $routeParams, $location, $data) {
    // page parameter
    $scope.uuid = $routeParams.uuid;

    $data.getError($scope.uuid).
    then(function (response) {
        $scope.error = response.data;
    }).then(function () {
        console.log($scope.error);
        var errorMessages = JSON.parse($scope.error['Errors']);
        for(var key in errorMessages) {
            console.log(errorMessages[key]);
            if(errorMessages[key].indexOf('Found a patient with similar characteristic') > -1) {
                // TODO: Get patient_id
                $scope.getPatientData(4);
            }
        }
    });

    $scope.getPatientData = function (id) {
        $data.getErrorPatient(id).
        then(function (response) {
            $scope.mergePatient = response.data;
        });
    }
}


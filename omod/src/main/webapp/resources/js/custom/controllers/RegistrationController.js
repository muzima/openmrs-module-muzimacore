function ViewRegistrationCtrl($scope, $location, $routeParams, $registrations) {
    // page parameter
    $scope.uuid = $routeParams.uuid;
    // get the current notification
    $registrations.getRegistration($scope.uuid).
    then(function (response) {
        $scope.registration = response.data;
    });

    $scope.cancel = function () {
        $location.path('/registrations');
    };
}

function ListRegistrationsCtrl($scope, $registrations) {
    // initialize the paging structure
    $scope.maxSize = 5;
    $scope.pageSize = 5;
    $scope.currentPage = 1;
    $registrations.getRegistrations($scope.currentPage, $scope.pageSize).
    then(function (response) {
        var serverData = response.data;
        $scope.registrations = serverData.objects;
        $scope.noOfPages = serverData.pages;
    });

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $registrations.getRegistrations($scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.errors = serverData.objects;
                $scope.noOfPages = serverData.pages;
            });
        }
    }, true);

    $scope.$watch('search', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $scope.currentPage = 1;
            $registrations.getRegistrations($scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.errors = serverData.objects;
                $scope.noOfPages = serverData.pages;
            });
        }
    }, true);
}
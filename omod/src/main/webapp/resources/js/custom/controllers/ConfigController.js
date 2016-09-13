function ConfigCtrl($scope, $routeParams, $location, $data) {
    // page parameter
    $scope.uuid = $routeParams.uuid;
    // get the current notification
    $data.getQueue($scope.uuid).
    then(function (response) {
        $scope.queue = response.data;
    });

    $scope.delete = function () {
        var uuidList = [$scope.uuid];
        $data.deleteQueue(uuidList).
        then(function () {
            $location.path("/configs");
        })
    };

    $scope.cancel = function () {
        $location.path('/configs');
    };
}

function ConfigsCtrl($scope, $location, $configs) {
    // initialize the paging structure
    $scope.maxSize = 10;
    $scope.pageSize = 10;
    $scope.currentPage = 1;
    $configs.getConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
    then(function (response) {
        var serverData = response.data;
        $scope.configs = serverData.objects;
        $scope.noOfPages = serverData.pages;
    });

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $configs.getConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.configs = serverData.objects;
                $scope.noOfPages = serverData.pages;
            });
        }
    }, true);

    $scope.$watch('search', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $scope.currentPage = 1;
            $configs.getConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.configs = serverData.objects;
                $scope.noOfPages = serverData.pages;
            });
        }
    }, true);
}
function SettingCtrl($scope, $routeParams, $location, $data) {
    $scope.setting = {};
    // initialize the view to be read only
    $scope.mode = "view";
    $scope.uuid = $routeParams.uuid;
    if ($scope.uuid === undefined) {
        $scope.mode = "edit";
    } else {
        $data.getSource($scope.uuid).
        then(function (response) {
            $scope.setting = response.data;
        });
    }

    $scope.edit = function () {
        $scope.mode = "edit";
    };

    $scope.cancel = function () {
        if ($scope.mode == "edit") {
            if ($scope.uuid === undefined) {
                $location.path("/settings");
            } else {
                $scope.mode = "view"
            }
        } else {
            $location.path("/settings");
        }
    };

    $scope.save = function (source) {
        $data.saveSource(source.uuid, source.name, source.description).
        then(function () {
            $location.path("/settings");
        })
    };

    $scope.delete = function () {
        $data.deleteSource($scope.uuid).
        then(function () {
            $location.path("/settings");
        })
    };
}

function SettingsCtrl($scope, $location, $data) {
    // initialize the paging structure
    $scope.maxSize = 10;
    $scope.pageSize = 10;
    $scope.currentPage = 1;
    $data.getSettings($scope.search, $scope.currentPage, $scope.pageSize).
    then(function (response) {
        var serverData = response.data;
        $scope.settings = serverData.objects;
        $scope.noOfPages = serverData.pages;
    });

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $data.getSettings($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.settings = serverData.objects;
                $scope.noOfPages = serverData.pages;
            });
        }
    }, true);

    $scope.$watch('search', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $scope.currentPage = 1;
            $data.getSettings($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.settings = serverData.objects;
                $scope.noOfPages = serverData.pages;
            });
        }
    }, true);
}
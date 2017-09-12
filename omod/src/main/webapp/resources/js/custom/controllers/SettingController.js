function SettingCtrl($scope, $routeParams, $location, $muzimaSettings) {
    $scope.setting = {};
    // initialize the view to be read only
    $scope.mode = "view";
    $scope.uuid = $routeParams.uuid;
    if ($scope.uuid === undefined) {
        $scope.mode = "edit";
    } else {
        $muzimaSettings.getSetting($scope.uuid).
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

    $scope.save = function (setting) {
        $muzimaSettings.saveSetting(setting.uuid, setting.property, setting.name, setting.description).
        then(function () {
            $location.path("/settings");
        })
    };

    $scope.delete = function () {
        $muzimaSettings.deleteSetting($scope.uuid).
        then(function () {
            $location.path("/settings");
        })
    };
}

function SettingsCtrl($scope, $location, $muzimaSettings) {
    // initialize the paging structure
    $scope.maxSize = 10;
    $scope.pageSize = 10;
    $scope.currentPage = 1;
    $muzimaSettings.getSettings($scope.search, $scope.currentPage, $scope.pageSize).
    then(function (response) {
        var serverData = response.data;
        $scope.settings = serverData.objects;
        $scope.noOfPages = serverData.pages;
    });

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $muzimaSettings.getSettings($scope.search, $scope.currentPage, $scope.pageSize).
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
            $muzimaSettings.getSettings($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.settings = serverData.objects;
                $scope.noOfPages = serverData.pages;
            });
        }
    }, true);
}
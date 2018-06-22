function ReportConfigurationCtrl($scope, $routeParams, $location, $muzimaReportConfigurations) {
    $scope.reportConfiguration = {};
    // initialize the view to be read only
    $scope.mode = "view";
    $scope.uuid = $routeParams.uuid;
    if ($scope.uuid === undefined) {
        $scope.mode = "edit";
    } else {
        $muzimaReportConfigurations.getReportConfiguration($scope.uuid).
        then(function (response) {
            $scope.reportConfiguration = response.data;
        });
    }

    $scope.edit = function () {
        $scope.mode = "edit";
    };

    $scope.cancel = function () {
        if ($scope.mode == "edit") {
            if ($scope.uuid === undefined) {
                $location.path("/reportConfigs");
            } else {
                $scope.mode = "view"
            }
        } else {
            $location.path("/reportConfigs");
        }
    };

    $scope.save = function (reportConfiguration) {
        $muzimaReportConfigurations.saveReportConfiguration(reportConfiguration.uuid, reportConfiguration.reportId, reportConfiguration.cohortId).
        then(function () {
            $location.path("/reportConfigs");
        })
    };

    $scope.delete = function () {
        $reportConfigurations.deleteReportConfiguration($scope.uuid).
        then(function () {
            $location.path("/reportConfigs");
        })
    };
}

function ReportConfigurationsCtrl($scope, $location, $muzimaReportConfigurations) {
    // initialize the paging structure
  
    $scope.maxSize = 10;
    $scope.pageSize = 10;
    $scope.currentPage = 1;
    $muzimaReportConfigurations.getReportConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
    then(function (response) {
        var serverData = response.data;
        $scope.reportConfigurations = serverData.objects;
        $scope.noOfPages = serverData.pages;
    });

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $muzimaReportConfigurations.getReportConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.reportConfigurations = serverData.objects;
                $scope.noOfPages = serverData.pages;
            });
        }
    }, true);

    $scope.$watch('search', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $scope.currentPage = 1;
            $muzimaReportConfigurations.getReportConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.reportConfigurations = serverData.objects;
                $scope.noOfPages = serverData.pages;
            });
        }
    }, true);

    $scope.settingDisplayValue = function(setting){
        if(setting.datatype == 'STRING'){
            return setting.value
        } else if(setting.datatype == 'PASSWORD'){
            var str = setting.value;
            return str.replace(/./g, '*');
        } else if(setting.datatype == 'BOOLEAN'){
            if(setting.value == true){
                return 'Enabled';
            } else if(setting.value == false){
                return 'Disabled';
            }
            return setting.value
        }
    }
}

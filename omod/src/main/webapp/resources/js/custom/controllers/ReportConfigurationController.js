function ReportConfigurationCtrl($scope, $routeParams, $location, $muzimaReportConfigurations) {
    $scope.reportConfiguration = {};
    $scope.search = { cohorts: '', reports: ''};
    $scope.selected = {cohorts: '', reports: ''};
    $scope.configReports = [];
    $scope.checkboxModel = {priority : false};

    var createJson = function () {
        var reportConfigurationJsonString = {"reports":$scope.configReports};
        return angular.toJson(reportConfigurationJsonString);
    };

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
        $muzimaReportConfigurations.getReportsForReportConfiguration($scope.reportConfiguration.uuid).
        then(function (response) {
            $scope.reports = response.data.objects;

            angular.forEach($scope.reports, function (report, index) {
                $scope.configReports.push(report);
            });
        });

        $muzimaReportConfigurations.getCohortForReportConfiguration($scope.reportConfiguration.uuid).then(function (response) {
            $scope.cohorts = response.data;
            $scope.search.cohorts = response.data[0];
        });

        $scope.mode = "edit";

    };

    $scope.cancel = function () {
        if ($scope.mode == "edit") {
            if ($scope.uuid === undefined) {
                $location.path("/reportConfigs");
            } else {
                $scope.configReports=[];
                $scope.mode = "view"
            }
        } else {
            $location.path("/reportConfigs");
        }
    };

    $scope.save = function (reportConfiguration) {
        $muzimaReportConfigurations.saveReportConfiguration(reportConfiguration.uuid, $scope.search.cohorts.uuid, createJson(),reportConfiguration.priority).
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

    /****************************************************************************************
     ***** Group of methods to manipulate Cohorts
     *****************************************************************************************/
    $scope.$watch('search.cohorts', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $muzimaReportConfigurations.searchReportConfigCohorts($scope.search.cohorts).
            then(function (response) {
                $scope.cohorts = response.data.objects;
            });
        }
    }, true);

    /****************************************************************************************
     ***** Group of methods to manipulate Reports
     *****************************************************************************************/

    $scope.$watch('search.reports', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $muzimaReportConfigurations.searchReportConfigReports($scope.search.reports).
            then(function (response) {
                $scope.reports = response.data.objects;
            });
        }
    }, true);

    $scope.addReport = function(report) {
        var reportExists = _.find($scope.configReports, function (configReport) {
            return configReport.uuid == report.uuid
        });
        if (!reportExists) {
            $scope.configReports.push(report);
            $scope.search.reports = '';
        }
    };

    $scope.chosenReport = function (value) {
        $scope.selected.report= value;
    };

    $scope.removeReport = function () {
        angular.forEach($scope.configReports, function (configReport, index) {
            if (configReport.uuid === $scope.selected.report) {
                $scope.configReports.splice(index, 1);
                $scope.selected.report = '';
            }
        });
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

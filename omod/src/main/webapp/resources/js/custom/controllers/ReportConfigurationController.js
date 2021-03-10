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
        $('#wait').hide();
    } else {
        $muzimaReportConfigurations.getReportConfiguration($scope.uuid).then(function (response) {
            $scope.reportConfiguration = response.data;
            var reports = JSON.parse(response.data.reports);
            $scope.configReports = reports.reports;
            $scope.search.cohorts = response.data.cohort;
            $('#wait').hide();
        }).then(function () {
            $scope.bindData();
            $scope.setEvent();
            $('#wait').hide();
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
        }, function (response) {
            $scope.error = response;
            $('#wait').hide();
        });
    };

    $scope.toggleRetireReportConfiguration = function(){
        $scope.retireReportConfiguration = true;
    };

    $scope.delete = function (reportConfiguration) {
        if(!reportConfiguration.retireReason){
             $scope.retireReasonError = true;
        }else{
            $muzimaReportConfigurations.deleteReportConfiguration(reportConfiguration.uuid,reportConfiguration.retireReason).
            then(function () {
                $location.path("/reportConfigs");
            });
        }
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


    /****************************************************************************************
     ***** Group of convenient methods to display a Json form
     *****************************************************************************************/
    $scope.bindData = function(){
        $scope.ul_li_Data = '';
        var jsonFormData = JSON.parse($scope.reportConfiguration.reports);
        if (jsonFormData != undefined) {
            $scope.to_ul(jsonFormData,'treeul');
            $scope.ul_li_Data = '';
        }
    };

    $scope.to_ul = function(branches, htmlElement) {
        $.each(branches, function(key,value) {
            if (":" + value == ':[object Object]' || angular.isArray(value)) {
                $scope.ul_li_Data = $scope.ul_li_Data + "<li><span><i class = 'icon-minus-sign'></i>&nbsp;"
                    + (isFinite(key)? "Entry:" + (parseInt(key, 10) + 1) : key);
                $scope.ul_li_Data = $scope.ul_li_Data + "<ul>";
                $scope.to_ul(value, htmlElement);
                $scope.ul_li_Data = $scope.ul_li_Data + "</ul>";
            } else{
                $scope.ul_li_Data = $scope.ul_li_Data + "<li><span>" + key;
                $scope.ul_li_Data = $scope.ul_li_Data + " : <b>" + value + "</b></span></li>";
            }
        });
        $('#'+htmlElement).empty().append($scope.ul_li_Data);
    };

    $scope.setEvent = function () {
        $('.icon-plus-sign, .icon-minus-sign').click(function () {
            $(this).parent().parent().find("ul").toggle();
            if ($(this).hasClass('icon-plus-sign')) {
                $(this).removeClass("icon-plus-sign").addClass("icon-minus-sign");
            } else if ($(this).hasClass('icon-minus-sign')) {
                $(this).removeClass("icon-minus-sign").addClass("icon-plus-sign");
            }
        });
    };

}

function ReportConfigurationsCtrl($scope, $location, $muzimaReportConfigurations) {
    // initialize the paging structure

    $scope.maxSize = 10;
    $scope.pageSize = 10;
    $scope.currentPage = 1;

    $scope.loadPaginationStub = false;
    $localeService.getUserLocale().then(function (response) {
        var serverData = response.data.locale;
        $translate.use(serverData).then(function () {
            $scope.loadPaginationStub = true;
        });
    });

    $muzimaReportConfigurations.getReportConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
    then(function (response) {
        var serverData = response.data;
        var reportConfigs = serverData.objects;
        angular.forEach(reportConfigs, function (reportConfig) {
            var reports = JSON.parse(reportConfig.reports);
            reportConfig['reports'] = reports.reports;
        });
        $scope.reportConfigurations = reportConfigs;
        $scope.noOfPages = serverData.pages;
        $('#wait').hide();
    }, function (response) {
        $scope.error = response;
        $('#wait').hide();
    });

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $muzimaReportConfigurations.getReportConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                var reportConfigs = serverData.objects;
                angular.forEach(reportConfigs, function (reportConfig) {
                    var reports = JSON.parse(reportConfig.reports);
                    reportConfig['reports'] = reports.reports;
                });
                $scope.reportConfigurations = reportConfigs;
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
                var reportConfigs = serverData.objects;
                angular.forEach(reportConfigs, function (reportConfig) {
                    var reports = JSON.parse(reportConfig.reports);
                    reportConfig['reports'] = reports.reports;
                });
                $scope.reportConfigurations = reportConfigs;
                $scope.noOfPages = serverData.pages;
            });
        }
    }, true);
}

'use strict';
function XFormsCtrl($scope, $location, XFormService, _) {

    // initialize the paging structure
    $scope.maxSize = 5;
    $scope.pageSize = 5;
    $scope.currentPage = 1;
    $scope.totalItems = 0;

    $scope.init = function () {
        $scope.selectedXformId = -1;
        $scope.xformsModuleStarted = true;
        $scope.fetch();
    };

    var showErrorMessage = function (content, cl, time) {
        $('<div/>')
            .addClass('alert')
            .addClass('alert-error')
            .hide()
            .fadeIn('slow')
            .delay(time)
            .appendTo('#error-alert')
            .text(content);
    };

    $scope.fetch = function () {
        XFormService.moduleState().then(function (result) {
            $scope.xformsModuleStarted = result.data;
        });
        XFormService.getXForms($scope.search, $scope.currentPage, $scope.pageSize).
        then(function (response) {
            var serverData = response.data;
            $scope.xForms = serverData.objects;
            $scope.totalItems = serverData.totalItems;
        });
        XFormService.getDiscriminatorTypes().
        then(function (results) {
            $scope.discriminatorTypes = results.data;
            $scope.discriminator = $scope.discriminatorTypes[0];
        });
    };

    $scope.importXForm = function () {
        var selectedXform = _.find($scope.xForms, function (xForm) {
            return xForm.id == $scope.selectedXformId
        });
        XFormService.save({
            id: $scope.selectedXformId,
            form: selectedXform.uuid,
            discriminator: $scope.discriminator
        }).success(function () {
            $location.path("/forms");
        }).error(function () {
            showErrorMessage("There was an error transforming ");
        });
    };

    $scope.hasXForms = function () {
        return !_.isEmpty($scope.xForms);
    };

    $scope.searchIsEmpty = function () {
        return $scope.search == undefined || $scope.search == null || $scope.search =='';
    };

    $scope.isValidSelection = function () {
        return $scope.hasXForms && $scope.selectedXformId != -1;
    };

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            XFormService.getXForms($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.xForms = serverData.objects;
                $scope.totalItems = serverData.totalItems;
            });
        }
    }, true);

    $scope.$watch('search', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $scope.currentPage = 1;
            XFormService.getXForms($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.xForms = serverData.objects;
                $scope.totalItems = serverData.totalItems;
            });
        }
    }, true);
}
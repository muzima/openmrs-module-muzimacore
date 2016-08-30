'use strict';
function XFormsCtrl($scope, $location, XFormService, _, $q) {
    $scope.init = function () {
        $scope.selectedXformId = -1;
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
        XFormService.all().then(function (result) {
            $scope.xForms = result.data;
        });
        XFormService.getDiscriminatorTypes().then(function (results) {
            $scope.discriminatorTypes = results.data;
            $scope.discriminator = $scope.discriminatorTypes[0];
        });
    };

    // $scope.done = function () {
    //     $q.all(_.map($scope.selectedXForms, function (value) {
    //         return XFormService.save(_.find($scope.xForms, function (xForm) {
    //             return xForm.id == value
    //         })).then(function () {
    //             $location.path("#/forms");
    //         });
    //     }));
    // };

    $scope.importXForm = function () {
        var selectedXform = _.find($scope.xForms, function (xForm) {
                        return xForm.id == $scope.selectedXformId
                    });
        XFormService.save({id: $scope.selectedXformId, form: selectedXform.uuid, discriminator: $scope.discriminator})
        .success(function () {
            $location.path("/forms");
        }).error(function () {
            showErrorMessage("There was an error transforming ");
        });


    };

    $scope.hasXForms = function () {
        return !_.isEmpty($scope.xForms);
    };

    $scope.isValidSelection = function () {
        return $scope.hasXForms && $scope.selectedXformId != -1;
    };
}

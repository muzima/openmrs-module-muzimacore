'use strict';
function UpdateCtrl($location, $scope, FileUploadService, FormService, _,$routeParams) {
    $scope.init = function () {
        $scope.form_uuid =  $routeParams.muzimaform_uuid;
        $scope.title = 'Click here to update new Form';
        $scope.xformToUpload = "";
        $scope.htmlFormToUpload = "";
        $scope.fetchingForms = false;

        getForm($scope.form_uuid).then(setForm);
    };

    var getForm = function (formUuid) {
        $scope.fetchingForms = true;
        return FormService.get(formUuid);
    };

    var setForm = function (result) {
        $scope.fetchingForms = false;
        $scope.forms = result.data;
        $scope.muzimaforms = {form: result.data, newTag: ""}
    };

    $scope.hasForms = function () {
        return !_.isEmpty($scope.muzimaforms);
    };

    $scope.getUpdateURL = function (formType) {
        if (formType == 'html') return 'html/update.form';
        if (formType == 'odk') return 'odk/update.form';
        return 'javarosa/update.form';
    };

    $scope.update = function (file, form, formType) {
        var uuid = "";
        if (form != null && form !== 'undefined') {
            uuid = form.uuid;
        }

        FileUploadService.post({
            url: $scope.getUpdateURL(formType), file: file, params: {
                form: uuid
            }
        }).success(function () {
            $location.path('/forms');
        }).error(function () {
            showErrorMessage("Unable to update form with uuid " + uuid);
        });
    };

    $scope.validate = function (file, formType) {
        if (formType == 'html') {
            FileUploadService.post({
                url: 'validateMuzimaForm.form',
                file: file
            }).then(function (result) {
                $scope.validations = result.data;
            });
        } else {
            FileUploadService.post({
                url: formType == 'odk' ? 'odk/validate.form' : 'javarosa/validate.form',
                file: file,
                params: { isODK: formType == 'odk'}
            }).then(function (result) {
                $scope.validations = result.data;
            });
        }
    };

    $scope.style = function (type) {
        return type === 'ERROR' ? 'alert-danger' : 'alert-info';
    };

    $scope.hasFile = function () {
        return ($scope.file) ? true : false;
    };

    $scope.isValidXForm = function () {
        return $scope.isValidated() && !hasValidationMessages();
    };

    var hasValidationMessages = function () {
        return !_.isEmpty($scope.validations.list);
    };

    $scope.isInvalidXForm = function () {
        return $scope.isValidated() && hasValidationMessages();
    };

    $scope.isValidated = function () {
        return ($scope.validations) ? true : false;
    };

    $scope.cancel = function () {
        $scope.validations = null;
        if ($scope.clearFile) $scope.clearFile();
    };

    $scope.cancelUpdate = function () {
        $location.path('/forms');
    }
}
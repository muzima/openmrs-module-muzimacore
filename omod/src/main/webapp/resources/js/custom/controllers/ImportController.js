'use strict';
function ImportCtrl($scope, FileUploadService, FormService, _, $location, $routeParams) {
    $scope.searchForm;
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

    FormService.getForms().then(function (results) {
        $scope.forms = results.data.results;
        if ($scope.forms.length > 0 && $scope.forms.length <= 10) {
            $scope.form = $scope.forms[0];
            $scope.loadData();
        }
    });

    FormService.getDiscriminatorTypes().then(function (results) {
        $scope.discriminatorTypes = results.data;
    });

    $scope.$watch('searchForm', function (newValue, oldValue) {
        if (newValue != oldValue) {
            FormService.searchForms($scope.searchForm).
            then(function (response) {
                $scope.searchForms = response.data.results;
            });
        }
    }, true);

    $scope.selectForm = function (value) {
        $scope.form = value;
        $scope.loadData();
    };

    $scope.loadData = function(){
        $scope.name = $scope.form.name;
        $scope.version = $scope.form.version;
        $scope.description = $scope.form.description;
        $scope.discriminator = $scope.discriminatorTypes[0];
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

    $scope.upload = function (file, form, discriminator, formType) {
        var uuid = "";
        if (form != null && form !== 'undefined') {
            uuid = form.uuid;
        }

        FileUploadService.post({
            url: $scope.getURL(formType), file: file, params: {
                form: uuid, discriminator: discriminator
            }
        }).success(function () {
            $location.path("/forms");
        }).error(function () {
            showErrorMessage("The form name already exists !! Please use some other name.");
        });
    };

    $scope.getURL = function (formType) {
        if (formType == 'html') return 'html/upload.form';
        if (formType == 'odk') return 'odk/upload.form';
        return 'javarosa/upload.form';
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

    $scope.cancelUpload = function () {
        $location.path('/forms');
    };

    FormService.getEncounterTypes().then(function (response) {
        $scope.encounterTypes = response.data.results;
    });

    $scope.createAndUpload = function (file, discriminator, formType, name, version, description,
     encounterType) {
        var encounterTypeuuid = "";
        if (encounterType != null && encounterType !== 'undefined') {
            encounterTypeuuid = encounterType.uuid;
        }

        FileUploadService.post({
            url: "html/createAndUpload.form", file: file, params: {
                discriminator: discriminator, name: name, version: version, description:description,
                encounterType: encounterTypeuuid
            }
        }).success(function () {
            $location.path("/forms");
        }).error(function () {
            showErrorMessage("The form name already exists !! Please use some other name.");
        });
    };
}
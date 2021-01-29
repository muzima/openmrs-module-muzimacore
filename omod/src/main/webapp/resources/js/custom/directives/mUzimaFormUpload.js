muzimaCoreModule.directive('mUzimaFormUpload', function(FileUploadService, FormService,) {
    return {
        restrict: 'E',
        link:function(scope){
            scope.$watch('searchForm', function (newValue, oldValue) {
                if (newValue != oldValue) {
                    FormService.searchForms(scope.searchForm).
                    then(function (response) {
                        scope.searchForms = response.data.results;
                    });
                }
            }, true);

            scope.selectForm = function (value) {
                scope.uploadCandidateForm = value;
            };

            var clearFormUploadFields = function () {
                scope.uploadCandidateForm ={};
                scope.newFormMetaData = {}
                scope.clearFile();
                console.log("Cleared file");
            }


            FormService.getDiscriminatorTypes().then(function (results) {
                scope.discriminatorTypes = results.data;
            });

            scope.loadFormsList = function() {
                FormService.getNonMuzimaForms().then(function (results) {
                    scope.forms = results.data.results;

                    if (scope.newFormMetaData != undefined && !$.isEmptyObject(scope.newFormMetaData)) {
                        console.log('There is new form metadata');
                        var newForm = scope.newFormMetaData;
                        scope.forms.push(newForm);
                        scope.selectForm(newForm);
                    } else {
                        console.log("New metadata unavailable");
                    }
                });
            }
            scope.loadFormsList();

            scope.$watch('newFormMetaData', function () {
                scope.loadFormsList();
            }, true);

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

            scope.validate = function (file, formType) {
                FileUploadService.post({
                    url: 'validateMuzimaForm.form',
                    file: file
                }).then(function (result) {
                    $scope.validations = result.data;
                });
            };

            scope.upload = function (file, form) {
                var uuid = "";
                if (form != null && form !== 'undefined') {
                    uuid = form.uuid;
                }

                if(uuid == 'newFormMetadata'){
                    console.log("Uploading with new form metadata...");
                    FileUploadService.post({
                        url: "html/createAndUpload.form", file: file, params: {
                            discriminator: form.discriminator, name: form.name, version: form.version,
                            description: form.description, encounterType: form.encounterType.uuid
                        }
                    }).success(function (response) {
                        console.log("Upload response:"+JSON.stringify(response));
                        if(response.hasOwnProperty('uuid')) {
                            form.uuid = response.uuid;
                            scope.setSelectedForm(form);
                            scope.goToPreviousWizardTab();
                            clearFormUploadFields();
                        }
                    }).error(function () {
                        showErrorMessage("The form name already exists !! Please use some other name.");
                    });

                } else {
                    console.log("Going to upload");
                    FileUploadService.post({
                        url: 'html/upload.form', file: file, params: {
                            form: form.uuid, discriminator: form.discriminator
                        }
                    }).success(function (response) {
                        console.log("Upload response:"+JSON.stringify(response));
                        if(response.hasOwnProperty('uuid')) {
                            form.uuid = response.uuid;
                            scope.setSelectedForm(form);
                            scope.goToPreviousWizardTab();
                            clearFormUploadFields();
                        }
                    }).error(function (error) {
                        console.log("Error...."+JSON.stringify(error));
                        showErrorMessage("The form name already exists !! Please use some other name.");
                    });
                }
            };
        },
        templateUrl: '../../moduleResources/muzimacore/partials/directives/mUzimaFormUpload.html'
    };
});
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
            }


            FormService.getDiscriminatorTypes().then(function (results) {
                scope.discriminatorTypes = results.data;
            });

            scope.loadFormsList = function() {
                FormService.getNonMuzimaForms().then(function (results) {
                    scope.forms = results.data.results;

                    if (scope.newFormMetaData != undefined && !$.isEmptyObject(scope.newFormMetaData)) {
                        var newForm = scope.newFormMetaData;
                        scope.forms.push(newForm);
                        scope.selectForm(newForm);
                    }
                });
            }
            scope.loadFormsList();

            scope.$watch('newFormMetaData', function () {
                scope.loadFormsList();
            }, true);

            scope.areAllFormFieldsEntered= function(){
               return scope.uploadCandidateForm != ''  && scope.discriminator != '' && scope.hasFile();
            }

            scope.hasFile = function () {
                return (scope.file) ? true : false;
            };

            scope.isValidXForm = function(){
                return scope.isValidated() && !hasValidationMessages();
            }

            var hasValidationMessages = function () {
                return !_.isEmpty(scope.validations.list);
            };

            scope.isValidated = function () {
                return (scope.validations) ? true : false;
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

            scope.validate = function (file, formType) {
                FileUploadService.post({
                    url: 'validateMuzimaForm.form',
                    file: file
                }).then(function (result) {
                    scope.validations = result.data;
                });
            };

            scope.upload = function (file, form) {
                var uuid = "";
                if (form != null && form !== 'undefined') {
                    uuid = form.uuid;
                }

                if(uuid == 'newFormMetadata'){
                    FileUploadService.post({
                        url: "html/createAndUpload.form", file: file, params: {
                            discriminator: form.discriminator, name: form.name, version: form.version,
                            description: form.description, encounterType: form.encounterType.uuid
                        }
                    }).success(function (response) {
                        if(response.hasOwnProperty('uuid')) {
                            form.uuid = response.uuid;
                            scope.setSelectedForm(form);
                            scope.exitFormUploadTab();
                            clearFormUploadFields();
                        }
                    }).error(function () {
                        showErrorMessage("The form name already exists !! Please use some other name.");
                    });

                } else {
                    FileUploadService.post({
                        url: 'html/upload.form', file: file, params: {
                            form: form.uuid, discriminator: form.discriminator
                        }
                    }).success(function (response) {
                        if(response.hasOwnProperty('uuid')) {
                            form.uuid = response.uuid;
                            scope.setSelectedForm(form);
                            scope.exitFormUploadTab();
                            clearFormUploadFields();
                        }
                    }).error(function (error) {
                        showErrorMessage("The form name already exists !! Please use some other name.");
                    });
                }
            };
        },
        templateUrl: '../../moduleResources/muzimacore/partials/directives/mUzimaFormUpload.html'
    };
});
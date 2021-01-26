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
                scope.form = value;
                scope.loadData();
            };

            scope.loadData= function(){
                console.log("Loading data///////");
                scope.name = scope.form.name;
                scope.version = scope.form.version;
                scope.description = scope.form.description;
            };

            FormService.getDiscriminatorTypes().then(function (results) {
                scope.discriminatorTypes = results.data;
                //scope.discriminator = scope.discriminatorTypes[0];
            });

            FormService.getForms().then(function (results) {
                scope.forms = results.data.results;
                // if (scope.forms.length > 0 && scope.forms.length <= 10) {
                //     scope.form = scope.forms[0];
                //     scope.loadData();
                // }
            });

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

            scope.upload = function (file, form, discriminator,wizardStep) {
                var uuid = "";
                if (form != null && form !== 'undefined') {
                    uuid = form.uuid;
                }

                FileUploadService.post({
                    url: 'html/upload.form', file: file, params: {
                        form: uuid, discriminator: discriminator
                    }
                }).success(function () {
                    console.log("Success...");
                    scope.setSelectedRegistrationForm(uuid,wizardStep);
                    //$location.path("/forms");
                }).error(function () {
                    console.log("Error....");
                    showErrorMessage("The form name already exists !! Please use some other name.");
                });
            };
        },
        templateUrl: '../../moduleResources/muzimacore/partials/directives/mUzimaFormUpload.html'
    };
});
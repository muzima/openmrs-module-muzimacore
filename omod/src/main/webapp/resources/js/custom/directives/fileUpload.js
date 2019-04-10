//TODO: Write angular e2e tests for testing this
muzimaCoreModule.directive("fileUpload", function () {
    return {
        restrict: "E",
        templateUrl: "../../moduleResources/muzimacore/partials/directives/fileUpload.html",
        replace: false,
        transclude: true,
        link: function (scope, element, attrs) {
            var fileUploadButton = element.find('#fileUploadControl');
            var activator = element.find("#fileUploadControlActivator");

            $(fileUploadButton).hide();
            $(activator).text(attrs.message);

            $(activator).click(function () {
                $(fileUploadButton).click();
            });

            scope.$parent.clearFile = function() {
                scope.file = scope.$parent.file = null;
                $(activator).text(attrs.message);
                scope.$parent.$apply();
            };

            $(fileUploadButton).on("change", function () {
                scope.file = scope.$parent.file = this.files[0];
                $(activator).text((scope.file == undefined ? attrs.message : scope.file.name));
                scope.validations = scope.$parent.validations = null;
                scope.$parent.$apply();
            });
        }
    }
});

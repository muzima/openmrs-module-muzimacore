muzimaCoreModule.directive('locationCreator', function($configs) {
    return {
        restrict: 'E',
        link:function(scope){
            scope.saveLocation = function (location) {
                $configs.saveLocation(location.name, location.description).success(function (response) {
                    if(response.hasOwnProperty('uuid')) {
                        location.uuid = response.uuid;
                        scope.setSelectedLocation(location);
                        scope.exitLocationCreationTab();
                        scope.location = {};
                    } else if(response.hasOwnProperty('error')) {
                        console.log("Save response error: "+JSON.stringify(response));
                    }
                }).error(function (response) {
                    console.log("Save response error2: "+JSON.stringify(response));
                });
            };

            scope.areAllRequiredLocationFieldsEntered = function(){
                return scope.location != undefined && scope.location.name != undefined && scope.location.description != undefined && scope.location.name.trim() != '' && scope.location.description.trim() != '';
            }

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
        },
        templateUrl: '../../moduleResources/muzimacore/partials/directives/locationCreator.html'
    };
});
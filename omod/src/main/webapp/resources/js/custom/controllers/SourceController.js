function SourceCtrl($scope, $routeParams, $location, $data) {
    // initialize the source object
    $scope.source = {};
    // initialize the view to be read only
    $scope.mode = "view";
    $scope.retire_source = false;
    $scope.uuid = $routeParams.uuid;
    if ($scope.uuid === undefined) {
        $scope.mode = "edit";
        $('#wait').hide();
    } else {
        $data.getSource($scope.uuid).
        then(function (response) {
            $scope.source = response.data;
            $('#wait').hide();
        });
    }

    $scope.edit = function () {
        $scope.mode = "edit";
    };

    $scope.cancel = function () {
        if ($scope.mode == "edit") {
            if ($scope.uuid === undefined) {
                $location.path("/sources");
            } else {
                $scope.mode = "view"
            }
        } else {
            $location.path("/sources");
        }
    };

    $scope.save = function (source) {
        $data.saveSource(source.uuid, source.name, source.description).
        then(function () {
            $location.path("/sources");
        })
    };

    $scope.toggleRetireSource = function(source){
        $scope.retire_source = true;
    };

    $scope.delete = function (source) {
        $data.deleteSource(source.uuid,source.retireReason).
        then(function () {
            $location.path("/sources");
        })
    };
}

function SourcesCtrl($scope, $data) {
    // initialize the paging structure
    $scope.maxSize = 10;
    $scope.pageSize = 10;
    $scope.currentPage = 1;
    $data.getSources($scope.search, $scope.currentPage, $scope.pageSize).
    then(function (response) {
        var serverData = response.data;
        $scope.sources = serverData.objects;
        $scope.noOfPages = serverData.pages;
        $('#wait').hide();
    });

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $data.getSources($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.sources = serverData.objects;
                $scope.noOfPages = serverData.pages;
                $('#wait').hide();
            });
        }
    }, true);

    $scope.$watch('search', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $scope.currentPage = 1;
            $data.getSources($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.sources = serverData.objects;
                $scope.noOfPages = serverData.pages;
            });
        }
    }, true);
}
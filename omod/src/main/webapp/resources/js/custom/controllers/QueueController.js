function QueueCtrl($scope, $routeParams, $location, $data) {
    // page parameter
    $scope.uuid = $routeParams.uuid;
    $scope.remove_queue_data = false;
    $scope.remove_reason = false;
    $scope.removeReasonError = false;
    // get the current notification
    $data.getQueue($scope.uuid).
    then(function (response) {
        $scope.queue = response.data;
        $('#wait').hide();
    });

    $scope.delete = function () {
        if(!$scope.removeReason){
             $scope.removeReasonError = true;
        }else{
            var uuidList = [$scope.uuid];
            $data.deleteQueue(uuidList, $scope.removeReason).
            then(function () {
                $location.path("/queues");
            });
        }
    };

    $scope.toggleRemoveQueue = function(){
        $scope.remove_queue_data = true;
    };

    $scope.cancel = function () {
        $location.path('/queues');
    };
}

function QueuesCtrl($scope, $location, $data, $localeService, $translate) {
    // initialize selected error data for re-queueing
    $scope.selected = {};
    // initialize the paging structure
    $scope.maxSize = 10;
    $scope.pageSize = 10;
    $scope.currentPage = 1;
    $scope.totalItems = 0;
    $scope.remove_queue_data = false;
    $scope.remove_reason = false;
    $scope.removeReasonError = false;

    $scope.loadPaginationStub = false;
    $localeService.getUserLocale().then(function (response) {
        var serverData = response.data.locale;
        $translate.use(serverData).then(function () {
            $scope.loadPaginationStub = true;
        });
    });

    $data.getQueues($scope.search, $scope.currentPage, $scope.pageSize).
    then(function (response) {
        var serverData = response.data;
        $scope.queues = serverData.objects;
        $scope.noOfPages = serverData.pages;
        $scope.totalItems = serverData.totalItems;
        $('#wait').hide();
    });

    $scope.delete = function () {
        var uuidList = [];
        angular.forEach($scope.selected, function (value, key) {
            if (value) {
                uuidList.push(key);
            }
        });
        if(!$scope.removeReason){
             $scope.removeReasonError = true;
        }else{
            $data.deleteQueue(uuidList, $scope.removeReason).
            then(function () {
                $data.getQueues($scope.search, $scope.currentPage, $scope.pageSize).
                then(function (response) {
                    var serverData = response.data;
                    $scope.queues = serverData.objects;
                    $scope.noOfPages = serverData.pages;
                    $scope.totalItems = serverData.totalItems;
                    $scope.remove_queue_data = false;
                });
            });
        }
    };

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $data.getQueues($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.queues = serverData.objects;
                $scope.noOfPages = serverData.pages;
                $scope.totalItems = serverData.totalItems;
            });
        }
    }, true);

    $scope.$watch('search', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $scope.currentPage = 1;
            $data.getQueues($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.queues = serverData.objects;
                $scope.noOfPages = serverData.pages;
                $scope.totalItems = serverData.totalItems;
            });
        }
    }, true);

    $scope.toggleRemoveQueue = function(){
        $scope.remove_queue_data = true;
    };
}
function ErrorCtrl($scope, $routeParams, $location, $data) {
    // page parameter
    $scope.uuid = $routeParams.uuid;

    // get the current notification
    $data.getError($scope.uuid).
    then(function (response) {
        $scope.error = response.data;
        if (response.data.discriminator != "registration") {
            document.getElementById('editData').style.display = 'none';
        } else
            document.getElementById('editData').style.display = 'inline';
    }).then(function(){
        $scope.bindData();
        $scope.setEvent();
        $scope.setControls();
    });

    $scope.bindData = function(){
        $scope.ul_li_Data = '';
        $scope.ul_li_xml_Data = '';
        $scope.string_xml_Data = '';
        if($scope.error.discriminator =='xml-registration'){
            var jsonFormData = $scope.error.payload;
            $scope.to_ul_from_xml(jsonFormData,'treeul');
            $scope.xml_to_string(jsonFormData);
        }else{
            var jsonFormData = JSON.parse($scope.error.payload);
            $scope.to_ul(jsonFormData,'treeul');
            $scope.json_to_string(jsonFormData);
        }
        $scope.ul_li_Data = '';
        var jsonFormDataError = JSON.parse('{"Errors":'+$scope.error.Errors+'}');
        $scope.to_ul(jsonFormDataError,'treeError');

    };

    $scope.to_ul_from_xml = function(xml, htmlElement){
        $(xml).contents().each(function (i, node)
        {
            if( $(this).children().length>0){

                $scope.ul_li_xml_Data =  $scope.ul_li_xml_Data + "<li><span><i class = 'icon-minus-sign'></i>"+ this.tagName ;
                $scope.ul_li_xml_Data =  $scope.ul_li_xml_Data + "<ul>";
                $scope.to_ul_from_xml($(this));
                $scope.ul_li_xml_Data =  $scope.ul_li_xml_Data + "</ul>";
            }
            else{
                $scope.ul_li_xml_Data =  $scope.ul_li_xml_Data + "<li><span>"+ this.tagName ;
                $scope.ul_li_xml_Data =  $scope.ul_li_xml_Data + " : <b>"+ this.innerText+"</b></span></li>";
            }
        });
        $('#'+htmlElement).empty();
        $('#'+htmlElement).append($scope.ul_li_xml_Data);
    };

    $scope.to_ul = function(branches, htmlElement) {
        $.each(branches, function(key,value) {
            if(":"+value ==':[object Object]'){
                $scope.ul_li_Data = $scope.ul_li_Data + "<li><span><i class = 'icon-minus-sign'></i>&nbsp;"+ key ;
                $scope.ul_li_Data = $scope.ul_li_Data + "<ul>";
                $scope.to_ul(value, htmlElement);
                $scope.ul_li_Data = $scope.ul_li_Data + "</ul>";
            }
            else{
                $scope.ul_li_Data = $scope.ul_li_Data + "<li><span>"+ key ;
                $scope.ul_li_Data = $scope.ul_li_Data + " : <b>"+ value+"</b></span></li>";
            }
        });
        $('#'+htmlElement).empty();
        $('#'+htmlElement).append($scope.ul_li_Data);
    };

    $scope.json_to_string = function(branches) {
        var strData =  JSON.stringify(branches)
        strData = strData.replace(/\:{/g, ':\n\t\t{\n\t\t\t');
        strData = strData.replace(/\","/g, '",\n\t\t\t"');
        strData = strData.replace(/\},/g, '\n\t\t},\n');
        $('#editJson').val(strData);
    };

    $scope.xml_to_string = function(branches) {
        strData = branches;
        strData = strData.replace(/\></g, '>\n<');
        $('#editJson').val(strData);
    };

    $scope.setControls = function(){
        $('#editJsonSection').hide();
        $( "#btnUpdate" ).prop( "disabled", true );
        $('#wait').hide();
        $('#search').hide();
        $('.messages').hide();
    }

    $scope.setEvent = function(){
        $('.icon-plus-sign, .icon-minus-sign').click(function(){
            $(this).parent().parent().find( "ul" ).toggle();
            if($(this).hasClass('icon-plus-sign')){

                $(this).removeClass( "icon-plus-sign" ).addClass( "icon-minus-sign" );

            }else if($(this).hasClass('icon-minus-sign')){

                $(this).removeClass( "icon-minus-sign" ).addClass( "icon-plus-sign" );
            }
        });

        $('.icon-plus-sign, .icon-minus-sign').hover(function(){
            $(this).css('cursor','hand');
        });

        $('#btnCancel').click(function(){
            $('#editJsonSection').hide();
            $( "#btnQueue" ).prop( "disabled", false );
            $( "#btnCancelQueue" ).prop( "disabled", false );
            $('.messages').hide();
        });

        $('.icon-edit').click(function(){
            $('#editJsonSection').show();
            $( "#btnQueue" ).prop( "disabled", true );
            $( "#btnCancelQueue" ).prop( "disabled", true );
            if($scope.error.discriminator =='xml-registration'){
                var jsonFormData = $scope.error.payload;
                $scope.xml_to_string(jsonFormData);
            }else{
                var jsonFormData = JSON.parse($scope.error.payload);
                $scope.json_to_string(jsonFormData);
            }
        });
    };

    $('#btnValidate').click(function(){
        $('#wait').show();
        $('.messages').hide();
        var jsonDataToValidate = $('#editJson').val();
        $data.validateData($scope.uuid,jsonDataToValidate).
        then(function (result) {
            $scope.ul_li_Data = '';
            $scope.to_ul(result.data,'treeError');
            //IF THERE ARE NO VALIDATION ERRORS THEN ENABLE UPDATE BUTTON
            if(Object.keys(result.data.Errors).length == 0){
                $( "#btnUpdate" ).prop( "disabled", false );
                $scope.isValid = true;
            }
            else{
                $scope.isValid = false;
                $('html,body').animate({scrollTop: $('#errorList').offset().top},1000);
            }
            $('.messages').show();
            $('#search').hide();
            $('#wait').hide();
        });
    });

    $('#btnUpdate').click(function(){

    });

    $scope.queue = function () {
        var uuidList = [$scope.uuid];
        $data.reQueueErrors(uuidList).
        then(function () {
            $location.path("/errors");
        })
    };

    $scope.cancel = function () {
        $location.path("/errors");
    };

    $('#btnYes').click(function(){
        //SAVE THE EDITED DATA
        $('#wait').show();
        var formDataToSave = $('#editJson').val();
        $data.saveEditedFormData($scope.uuid,formDataToSave).
        then(function (response) {
            $scope.error = response.data;
            $scope.bindData();
            $('#editJsonSection').hide();
            $( "#btnQueue" ).prop( "disabled", false );
            $( "#btnCancelQueue" ).prop( "disabled", false );
            $('.messages').hide();
            $('#wait').hide();
            $('#search').hide();
            $('#myModal').modal('hide');
        });
    });
    $('#btnNo').click(function(){

    });
}

function ErrorsCtrl($scope, $location, $data) {
    $scope.isErrorLoadingCompleted = false;
    // initialize selected error data for re-queueing
    $scope.selected = {};
    // initialize the paging structure
    $scope.maxSize = 10;
    $scope.pageSize = 10;
    $scope.currentPage = 1;
    $scope.totalItems = 0;
    $data.getErrors($scope.search, $scope.currentPage, $scope.pageSize).
    then(function (response) {
        var serverData = response.data;
        $scope.errors = serverData.objects;
        $scope.totalItems = serverData.totalItems;

        $scope.isErrorLoadingCompleted = true;
        $('#wait').hide();
    });

    $scope.queue = function () {
        $('#wait').show();
        var uuidList = [];
        angular.forEach($scope.selected, function (value, key) {
            if (value) {
                uuidList.push(key);
            }
        });
        $data.reQueueErrors(uuidList).
        then(function () {
            $data.getErrors($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.errors = serverData.objects;
                $scope.totalItems = serverData.totalItems;
                $('#wait').hide();
            });
        })
    };

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $('#wait').show();
            $data.getErrors($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.errors = serverData.objects;
                $scope.totalItems = serverData.totalItems;
                $('#wait').hide();
            });
        }
    }, true);

    $scope.$watch('search', function (newValue, oldValue) {
        $('#search').show();
        if (newValue != oldValue) {
            $scope.currentPage = 1;
            $data.getErrors($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.errors = serverData.objects;
                $scope.totalItems = serverData.totalItems;
                $('#search').hide();
            });
        }
    }, true);
}

function PotentialDuplicatesErrorsCtrl($scope, $data) {
    // initialize selected error data for re-queueing
    $scope.selected = {};
    // initialize the paging structure
    $scope.maxSize = 10;
    $scope.pageSize = 10;
    $scope.currentPage = 1;
    $scope.totalItems = 0;
    var searchTerm = 'Found a patient with similar characteristic';
    $data.getErrors(searchTerm, $scope.currentPage, $scope.pageSize).then(function (response) {
        var serverData = response.data;
        $scope.errors = serverData.objects;
        $scope.totalItems = serverData.totalItems;
        $('#wait').hide();
    }).catch(function(err) {
        console.log(err);
        $('#wait').hide();
    });

    $scope.$watch('currentPage', function (newValue, oldValue) {
        $('#wait').show();
        if (newValue != oldValue) {
            $data.getErrors($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.errors = serverData.objects;
                $scope.totalItems = serverData.totalItems;
                $('#wait').hide();
            });
        }
    }, true);

    $scope.$watch('search', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $scope.currentPage = 1;
            $data.getErrors($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.errors = serverData.objects;
                $scope.totalItems = serverData.totalItems;
            });
        }
    }, true);
}
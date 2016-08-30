function EditCtrl($scope, $routeParams, $location, $data) {
    // page parameter
    $scope.uuid = $routeParams.uuid;

    // get the current notification
    $data.getEdit($scope.uuid).
        then(function (response) {
            $scope.error = response.data;
            $scope.family_name = response.data.family_name;
            $scope.middle_name = response.data.middle_name;
            $scope.given_name = response.data.given_name;
            $scope.sex = response.data.sex;
            $scope.birth_date = response.data.birth_date;
            $scope.birthdate_estimated = response.data.birthdate_estimated;
            $scope.phone_number = response.data.phone_number;
            $scope.person_attribute4 = response.data.person_attribute4;
            $scope.uuid = response.data.patient_uuid;
            $scope.identifier_type_id = response.data.identifier_type_id;
            $scope.medical_record_number = response.data.medical_record_number;
            $scope.location_id = response.data.location_id;
            $scope.provider_id = response.data.provider_id;
            $scope.encounter_datetime = response.data.encounter_datetime;
            $scope.record_uuid = response.data.uuid;
            $scope.other_identifier_type = response.data.other_identifier_type;
            $scope.other_identifier_type_name = response.data.other_identifier_type_name;
            $scope.other_identifier_type_value = response.data.other_identifier_type_value;
        });


    $scope.postEditErrors = function (isValid) {

        var formData = JSON.stringify({
            family_name: $scope.family_name,
            middle_name: $scope.middle_name,
            given_name: $scope.given_name,
            sex: $scope.sex,
            patient_uuid: $scope.uuid,
            identifier_type_id: $scope.identifier_type_id,
            medical_record_number: $scope.medical_record_number,
            birth_date: $scope.birth_date,
            location_id: $scope.location_id,
            provider_id: $scope.provider_id,
            encounter_datetime: $scope.encounter_datetime,
            record_uuid: $scope.record_uuid,
            birthdate_estimated: $scope.birthdate_estimated,
            phone_number: $scope.phone_number,
            person_attribute4: $scope.person_attribute4,
            other_identifier_type: $scope.other_identifier_type,
            other_identifier_type_name: $scope.other_identifier_type_name,
            other_identifier_type_value: $scope.other_identifier_type_value
        });



        if(!checkDigit($scope.other_identifier_type_value)){
            document.getElementById('amrsError').innerHTML="";
            document.getElementById('identifierError').innerHTML="Please enter digits that matches CheckDigit algorithm";
        } else if(!checkDigit($scope.medical_record_number)){
            document.getElementById('identifierError').innerHTML="";
            document.getElementById('amrsError').innerHTML="Please enter digits that matches CheckDigit algorithm";
        } else if (isValid ){
            document.getElementById('identifierError').innerHTML="";
            document.getElementById('amrsError').innerHTML="";
            $data.editErrors(formData).
                then(function () {
                    $location.path("/errors");
                })
        }
    };

    $scope.edit = function () {
        var uuidList = [$scope.uuid];
        $data.reQueueErrors(uuidList).
            then(function () {
                $location.path("/edit");
            })
    };
    $scope.cancel = function () {
        $location.path('/errors');
    };
}

function checkDigit(number){

    var num = number.split('-');
    if (num.length != 2) {
        return false;
    }
    alert(luhnCheckDigit(num[0]));
    alert(num[1]);
    return luhnCheckDigit(num[0]) == num[1];
}

//
//check Digit Algorithm
//Source https://wiki.openmrs.org/display/docs/Check+Digit+Algorithm
function luhnCheckDigit(number) {
    var validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVYWXZ_";
    number = number.toUpperCase().trim();
    var sum = 0;
    for (var i = 0; i < number.length; i++) {
        var ch = number.charAt(number.length - i - 1);
        if (validChars.indexOf(ch) < 0) {
            return false;
        }
        var digit = ch.charCodeAt(0) - 48;
        var weight;
        if (i % 2 == 0) {
            weight = (2 * digit) - parseInt(digit / 5) * 9;
        }
        else {
            weight = digit;
        }
        sum += weight;
    }
    sum = Math.abs(sum) + 10;
    var digit = (10 - (sum % 10)) % 10;
    return digit;
}
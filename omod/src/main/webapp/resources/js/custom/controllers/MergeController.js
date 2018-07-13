function MergeCtrl($scope, $routeParams, $location, $data) {
    // page parameter
    $scope.uuid = $routeParams.uuid;

    $scope.queue_checkbox = { select_all: false };

    $data.getError($scope.uuid).
    then(function (response) {
        $scope.error = response.data;
    }).then(function () {
        const errorMessages = JSON.parse($scope.error['Errors']);
        for(const key in errorMessages) {
            if(errorMessages[key].indexOf('Found a patient with similar characteristic') > -1) {
                // TODO: Get patient_id
                $scope.getPatientData(4);
                return $scope.getPatientDataByUuid($scope.error['patientUuid'])
            }
        }
    }).then(function () {
        $scope.allKeys = _.union(Object.keys($scope.emrPatient), Object.keys($scope.queuePatient));
        let tableData = [];
        for(let key of $scope.allKeys) {
            let rowData = {
                key,
                label: getLabel(key),
                isConflict: isConflict(key, $scope.emrPatient[key], $scope.queuePatient[key]),
            };
            rowData['emrPatient'] = $scope.emrPatient[key];
            rowData['queuePatient'] = $scope.queuePatient[key];
            tableData.push(rowData)

            $scope.queue_checkbox[key] = false;
        }
        console.log(tableData);
        $scope.tableData = tableData
    });

    $scope.getPatientData = function (id) {
        return $data.getErrorPatient(id).
        then(function (response) {
            $scope.emrPatient = response.data;
        });
    };
    $scope.getPatientDataByUuid = function (uuid) {
        // $data.getPatientByUuid(uuid).
        return $data.getErrorPatient(5).
        then(function (response) {
            $scope.queuePatient = response.data;
        });
    };

    function getLabel(filed) {
        const lableMap = {
            'first_name': 'First Name',
            'middle_name': 'Middle Name',
            'family_name': 'Family Name',
            'sex': 'Sex',
            'age': 'Age',
            'country': 'Country',
            'birthday': 'Birthday',
        };

        if(_.has(lableMap, filed)) {
            return _.get(lableMap, filed);
        }
        return filed;
    }

    function isConflict(filed, val1, val2) {
        switch(filed) {
            case 'first_name':
            case 'middle_name':
            case 'family_name':
            case 'sex':
            case 'age':
            case 'country':
            case 'birthday':
            /*case 'another_one':
            case 'another':*/
                return val1 !== val2;
            default:
                return false
        }
    }

    $('#erm_select_all').click(function () {
        const isChecked = $('#erm_select_all').is(':checked');
        $('[id^="erm_"][id$="_checkbox"]').each(function (index) {
            if(isChecked) {
                $(this).prop('checked', true);
            } else {
                $(this).prop('checked', false);
            }
        })

    });

    $scope.toggleCheckboxes = function (side = 'queue') {
        if(side === 'erm') {

        } else if(side === 'queue') {
            if($scope.queue_checkbox.select_all === true) {
                for(let key of $scope.allKeys) {
                    $scope.queue_checkbox[key] = true
                }
            } else {
                for(let key of $scope.allKeys) {
                    $scope.queue_checkbox[key] = false
                }
            }
        }
    }

}

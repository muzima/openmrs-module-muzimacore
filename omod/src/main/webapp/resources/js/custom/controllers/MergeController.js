function MergeCtrl($scope, $routeParams, $location, $data) {
    // page parameter
    $scope.uuid = $routeParams.uuid;
    $scope.queue_checkbox = { select_all: false };
    $scope.emr_checkbox = { select_all: false};
    $data.getError($scope.uuid).then(function (response) {
        $scope.error = response.data;
        $scope.payload = JSON.parse($scope.error['payload']);
        $scope.queuePatient = _stripPatientPrefixFromPayloadPatient($scope.payload['patient']);
        if($scope.payload['tmp'] && $scope.payload['tmp']['tmp.age_in_years']) {
            $scope.queuePatient['age'] = $scope.payload['tmp']['tmp.age_in_years'];
        }
        const errorMessages = JSON.parse($scope.error['Errors']);
        var duplicatePatientErrorMessage = Object.values(errorMessages).find(function(error) {
            return error.startsWith('Found a patient with similar characteristic');
        });

        // Parse to find the patient identifier
        if(duplicatePatientErrorMessage) {
            let identifier = _findThePatientIdentifier(duplicatePatientErrorMessage);
            if(identifier != null) {
                $data.getPatientByIdentifier(identifier).then(function(response) {
                    $scope.existingPatient = response.data.results[0];         // just pick the first record for now.
                    $scope.emrPatient = _harmonizePatientFromServer($scope.existingPatient);
                    $scope.allKeys = _.union(Object.keys($scope.emrPatient), Object.keys($scope.queuePatient));
                    console.log( "$scope.allKeys");
                    console.log( $scope.allKeys);

                    let tableData = [];
                    for(let key of $scope.allKeys) {
                        if(key != 'confirm_identifier_value') {
                            if($scope.emrPatient[key] instanceof Object && $scope.queuePatient[key] instanceof Object){

                                for (let key2 of Object.keys($scope.emrPatient[key])){
                                    let rowData = {
                                        key:key+"."+key2,
                                        label: getLabel(key) + " " + key2,
                                        isConflict: isConflict(key, $scope.emrPatient[key][key2], $scope.queuePatient[key][key2]),
                                        category: getRegistrationDataCategory(key),
                                    };
                                    rowData['emrPatient'] = $scope.emrPatient[key][key2];
                                    rowData['queuePatient'] = $scope.queuePatient[key][key2];

                                    tableData.push(rowData);
                                    $scope.queue_checkbox[key] = false;
                                    $scope.emr_checkbox[key] = false;
                                }
                            } else if($scope.emrPatient[key] instanceof Object ){

                                let count = 1;
                                for (let key2 of Object.keys($scope.emrPatient[key])){
                                    let rowData = {
                                        key:key+"."+key2,
                                        label: getLabel(key) + " " + key2,
                                        isConflict: isConflict(key, $scope.emrPatient[key][key2], $scope.queuePatient[key]),
                                        category: getRegistrationDataCategory(key),
                                    };
                                    rowData['emrPatient'] = $scope.emrPatient[key][key2];
                                    if(key2.includes("value")) {
                                        rowData['queuePatient'] = $scope.queuePatient[key];
                                        count++;
                                    }

                                    tableData.push(rowData);
                                    $scope.queue_checkbox[key] = false;
                                    $scope.emr_checkbox[key] = false;
                                }
                            } else if( $scope.queuePatient[key] instanceof Object && !($scope.queuePatient[key] instanceof Array)){

                                let count = 1;
                                for (let key2 of Object.keys($scope.queuePatient[key])){
                                    let rowData = {
                                        key:key+"."+key2,
                                        label: getLabel(key) + " " + key2,
                                        isConflict: isConflict(key, $scope.emrPatient[key], $scope.queuePatient[key][key2]),
                                        category: getRegistrationDataCategory(key),
                                    };

                                    rowData['queuePatient'] = $scope.queuePatient[key][key2];
                                    if(key2.includes("value")) {
                                        rowData['emrPatient'] = $scope.emrPatient[key];
                                        count++;
                                    }

                                    tableData.push(rowData);
                                    $scope.queue_checkbox[key] = false;
                                    $scope.emr_checkbox[key] = false;
                                }
                            } else {
                                let rowData = {
                                    key,
                                    label: getLabel(key),
                                    isConflict: isConflict(key, $scope.emrPatient[key], $scope.queuePatient[key]),
                                    category: getRegistrationDataCategory(key),
                                };
                                rowData['emrPatient'] = $scope.emrPatient[key];
                                rowData['queuePatient'] = $scope.queuePatient[key];

                                tableData.push(rowData);
                                $scope.queue_checkbox[key] = false;
                                $scope.emr_checkbox[key] = false;
                            }
                        }

                    }
                    $scope.tableData = tableData;




                    $('#wait').hide();
                }).catch(function(err) {
                    console.log(err);
                    $('#wait').hide();
                });
            }
        } else {
            $('#wait').hide();
        }
    });

    $scope.categoryMap = ["Basic Demographics","Attributes","Identifiers","Addresses","Others"];

    function getRegistrationDataCategory(key){
        switch(key) {
            case 'uuid':
            case 'given_name':
            case 'middle_name':
            case 'family_name':
            case 'sex':
            case 'age':
            case 'birth_date':
            case 'birthdate_estimated':
                return 'Basic Demographics';
            case 'medical_record_number':
            case 'other_identifier_type':
            case 'other_identifier_value':
                return 'Identifiers';
            case 'country':
            case 'location':
            case 'sub_location':
            case 'village':
                return 'Addresses';
            case 'mothers_name':
            case 'phone_number':
                return 'Attributes';
            default:
                return 'Others';
        }
    }

    function getLabel(key) {
        const lableMap = {
            'given_name': 'Given name',
            'first_name': 'First Name',
            'middle_name': 'Middle Name',
            'family_name': 'Family Name',
            'sex': 'Sex',
            'country': 'Country',
            'birth_date': 'Birthdate',
            'age': 'Age',
        };
        if(_.has(lableMap, key)) {
            return _.get(lableMap, key);
        }

        // replace _ with space.
        let toRet = key.charAt(0).toUpperCase() + key.substring(1);
        toRet = toRet.replace(/_/g, ' ');

        return toRet;
    }

    function isConflict(key, val1, val2) {
        switch(key) {
            case 'given_name':
            case 'middle_name':
            case 'family_name':
            case 'sex':
            case 'country':
            case 'birthdate':
            case 'medical_record_number':
            case 'mothers_name':
            case 'birthdate_estimated':
                return val1 != val2;
            default:
                return false
        }
    }

    function _findThePatientIdentifier(errorMessage) {
        // Format is
        // "Found a patient with similar characteristic :  patientId = 9409 Identifier Id = 333333333-8"
        // regex
        const regex = /(?:^|\s)Found a patient with similar characteristic :  patientId =\s\d+\sIdentifier Id =\s(.+)(?:\s|$)/g;
        const matches = regex.exec(errorMessage);

        if(Array.isArray(matches) && matches[1]) return matches[1];
        return null;
    }

    function _harmonizePatientFromServer(patient) {
        let patientMedicalRecordNumber = {};
        if(Array.isArray(patient.identifiers)) {
            // find the universal type.
            let universal = patient.identifiers.find(identifier => {
                return identifier.preferred === true;
            });
            if(universal) {
                patientMedicalRecordNumber['identifier_type_uuid'] = universal['identifierType']['uuid'];
                patientMedicalRecordNumber['identifier_type_name'] = universal['identifierType']['display'];
                patientMedicalRecordNumber['identifier_value'] = universal['identifier'];
            }

        }

        let patientName = patient['person']['preferredName'] || patient['person'].names[0];
        let patientAddress = patient['person']['preferredAddress'] || patient['person'].addresses[0];
        if(patientAddress == undefined){
            patientAddress = {"country":"","district":"",};
        }
        return {
            uuid: patient['uuid'],
            given_name: patientName['givenName'],
            middle_name: patientName['middleName'],
            family_name: patientName['familyName'],
            sex: patient['person'].gender,
            country: patientAddress['country'],
            district: patientAddress['district'],
            birth_date: patient['person']['birthdate'],
            age: patient['person'].age,
            medical_record_number: patientMedicalRecordNumber,
        };
    }

    /**
     * For example patient.birthdate is turned into birthdate.
     * @param patient
     * @returns {{}}
     * @private
     */
    function _stripPatientPrefixFromPayloadPatient(patient) {
        let stripped = {};
        Object.keys(patient).forEach(key => {
            stripped[key.substring(key.indexOf('.')+1)] = patient[key];
    });
        return stripped;
    }
    function _addPatientPrefixToPayloadPatient(patient) {
        let updated = {};
        Object.keys(patient).forEach(key => {
            updated["patient."+key] = patient[key];
    });
        return updated;
    }

    /**
     * Updates the payload values with the one entered in the UI by the user when resolving the duplicate patients
     * @private
     */
    function _updatePayloadData() {
        let patientKeys = Object.keys($scope.queuePatient);
        $scope.tableData.forEach(rowData => {
            if(patientKeys.find(patKey => patKey === rowData.key)) {
                $scope.payload['patient']['patient.' + rowData.key] = rowData['queuePatient'];
            }
        });
    }
    function _updatePayloadDataAsDemographicsUpdate() {
        let updateSuccessfulStatus = false;
        let birthdateEstimated = $scope.tableData.find(entry => entry['key'] === 'birthdate_estimated');
        if(birthdateEstimated && birthdateEstimated['emrPatient'] != birthdateEstimated['queuePatient']
        && $scope.emr_checkbox['birthdate_estimated'] === $scope.queue_checkbox['birthdate_estimated']) {
            $scope.popupMessage = 'Please make a choice whether birthdate_estimated for patient record should be changed or not during merge.';
            $('#merge-modal').modal('show');
            console.log("Showing error");
            return updateSuccessfulStatus;
        } else {
            console.log("continue merging");


            let patientKeys = Object.keys($scope.queuePatient);

            $scope.payload['demographicsupdate'] = {};
            $scope.tableData.forEach(rowData => {
                if(patientKeys.find(patKey => patKey === rowData.key)){
                $scope.payload['demographicsupdate']['demographicsupdate.' + rowData.key] = rowData['queuePatient'];
            }});

            //update uuids
            $scope.payload['demographicsupdate']['demographicsupdate.temporal_patient_uuid'] = $scope.queuePatient.uuid;
            if ($scope.payload['demographicsupdate']['demographicsupdate.uuid']) {
                delete $scope.payload['demographicsupdate']['demographicsupdate.uuid'];
            }

            // Get demographic to remove
            Object.keys($scope.queue_checkbox).forEach(key => {
                if(key !== 'select_all'){
                    let fullKey = 'demographicsupdate.' + key;
                    if ($scope.emr_checkbox[key] === true) {
                        if ($scope.payload['demographicsupdate'][fullKey]) {
                            delete $scope.payload['demographicsupdate'][fullKey];
                        }
                    }
                }
            });

            $scope.payload['patient'] = _addPatientPrefixToPayloadPatient($scope.emrPatient);

            patientKeys.forEach(key => {
                if(($scope.payload['patient']['patient.' + key]) && ($scope.payload['demographicsupdate']['demographicsupdate.' + key])
                && $scope.payload['patient']['patient.' + key] == $scope.payload['demographicsupdate']['demographicsupdate.' + key]
                ){
                    delete $scope.payload['demographicsupdate']['demographicsupdate.' + key];
                } else {
                    console.log("Not equals: " + key + " :: " + $scope.payload['demographicsupdate']['demographicsupdate.' + key] + " : " + $scope.payload['patient']['patient.' + key]);
                }
            });

            if ($scope.payload['demographicsupdate']['demographicsupdate.birth_date']) {
                delete $scope.payload['demographicsupdate']['demographicsupdate.birth_date'];
            }
            updateSuccessfulStatus = true;
            return updateSuccessfulStatus;
        }

    }

    $scope.createModelId = function(prefix,suffix){
        return prefix+suffix;
    }
    $scope.updatePage = function(rowData) {
        rowData.isConflict = isConflict(rowData.key, rowData['emrPatient'], rowData['queuePatient']);
        // Check the queue data side.
        $scope.queue_checkbox[rowData.key] = true;
        $scope.emr_checkbox[rowData.key] = false;
    };

    $scope.toggleCheckboxes = function (side) {
        if(side === 'emr') {
            if($scope.emr_checkbox.select_all === true) {
                $scope.queue_checkbox.select_all = false;
                for(let key of $scope.allKeys) {
                    $scope.emr_checkbox[key] = true;
                    $scope.queue_checkbox[key] = false;
                }
            } else {
                for(let key of $scope.allKeys) {
                    $scope.emr_checkbox[key] = false;
                }
            }
        } else if(side === 'queue') {
            if($scope.queue_checkbox.select_all === true) {
                $scope.emr_checkbox.select_all = false;
                for(let key of $scope.allKeys) {
                    $scope.queue_checkbox[key] = true;
                    $scope.emr_checkbox[key] = false;
                }
            } else {
                for(let key of $scope.allKeys) {
                    $scope.queue_checkbox[key] = false
                }
            }
        }
    };

    $scope.toggleIndividualCheckbox = function(side, key) {
        if(side === 'emr') {
            if($scope.emr_checkbox[key] === true) {
                // Deselect the other side if selected.
                $scope.queue_checkbox[key] = false;

                // Also deselect all.
                $scope.queue_checkbox.select_all = false;
            }
        } else if(side === 'queue') {
            if($scope.queue_checkbox[key] === true) {
                // Deselect the other side.
                $scope.emr_checkbox[key] = false;

                // Also deselect all
                $scope.emr_checkbox.select_all = false;
            }
        }
    };

    $scope.isEditableField = function(key){
        var isEditable = !key.includes('uuid') && key !== 'age' && key !== 'sex'&& key !== 'birth_date';
        return isEditable;
    }

    $scope.mergeDemographics = function() {
        $('#wait').show();

        let updateSuccessfulStatus = _updatePayloadDataAsDemographicsUpdate();

        if(updateSuccessfulStatus){
            let info = {
                existingPatientUuid: $scope.existingPatient['uuid'],
                errorDataUuid: $scope.error['uuid'],
                payload: JSON.stringify($scope.payload),
            };

            $data.mergePatient(info).then(function (response) {
                // Redirect or something.
                $('#wait').hide();
                $location.path('/duplicates');
            }).catch(function (err) {
                console.log(err);
                $('#wait').hide();
            });
        } else {
            $('#wait').hide();
        }
    };

    $scope.createAndRequeue = function() {
        // Warn if identifier is still the same.
        // Find the rowData object associated with key medical_record_number
        let medicalRecordNumberRowData = $scope.tableData.find(entry => entry['key'] === 'medical_record_number');
        if(medicalRecordNumberRowData && medicalRecordNumberRowData['emrPatient'] === medicalRecordNumberRowData['queuePatient']) {
            $scope.popupMessage = 'Assigned medical record number already in use, please assign a different one on the new patient';
            $('#merge-modal').modal('show');
        } else {
            $('#wait').show();
            // Update & Modify the payload adding a flag that a duplicate patient should not be searched.
            _updatePayloadData();
            $scope.payload['skipPatientMatching'] = true;

            // post to re-queue.
            let info = {
                errorDataUuid: $scope.error['uuid'],
                payload: JSON.stringify($scope.payload),
            };

            $data.requeueDuplicatePatient(info).then(function (response) {
                // Redirect or something.
                $('#wait').hide();
                $location.path('/duplicates');
            }).catch(function (err) {
                console.log(err);
                $('#wait').hide();
            });
        }
    };
}

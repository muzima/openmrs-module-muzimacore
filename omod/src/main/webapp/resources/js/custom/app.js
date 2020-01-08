var muzimaCoreModule = angular.module('muzimaCoreModule', ['ui.bootstrap', 'ngRoute', 'ngSanitize', 'filters', 'muzimafilters']);

muzimaCoreModule.
    config(['$routeProvider', '$compileProvider', function ($routeProvider, $compileProvider) {
        $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|file):/);
        $routeProvider.
            when('/source/:uuid', {controller: SourceCtrl, templateUrl: '../../moduleResources/muzimacore/partials/source.html'}).
            when('/createSource/', {controller: SourceCtrl, templateUrl: '../../moduleResources/muzimacore/partials/source.html'}).
            when('/sources', {controller: SourcesCtrl, templateUrl: '../../moduleResources/muzimacore/partials/sources.html'}).
            when('/config/:uuid', {controller: ConfigCtrl, templateUrl: '../../moduleResources/muzimacore/partials/config.html'}).
            when('/createConfig/', {controller: ConfigCtrl, templateUrl: '../../moduleResources/muzimacore/partials/config.html'}).
            when('/configs', {controller: ConfigsCtrl, templateUrl: '../../moduleResources/muzimacore/partials/configs.html'}).
            when('/queue/:uuid', {controller: QueueCtrl, templateUrl: '../../moduleResources/muzimacore/partials/queue.html'}).
            when('/queues', {controller: QueuesCtrl, templateUrl: '../../moduleResources/muzimacore/partials/queues.html'}).
            when('/registrations', {controller: ListRegistrationsCtrl, templateUrl: '../../moduleResources/muzimacore/partials/registrations.html'}).
            when('/registration/:uuid', {controller: ViewRegistrationCtrl, templateUrl: '../../moduleResources/muzimacore/partials/registration.html'}).
            when('/forms', {controller: FormsCtrl,  templateUrl: '../../moduleResources/muzimacore/partials/forms.html'}).
            when('/xforms', {controller: XFormsCtrl, templateUrl: '../../moduleResources/muzimacore/partials/xforms.html'}).
            when('/htmlformentry', {controller: HtmlFormEntryCtrl, templateUrl: '../../moduleResources/muzimacore/partials/htmlFormEntry.html'}).
            when('/import/forms', {controller: ImportCtrl, templateUrl: '../../moduleResources/muzimacore/partials/import/forms.html'}).
            when('/update/forms/:muzimaform_uuid',{controller: UpdateCtrl, templateUrl: '../../moduleResources/muzimacore/partials/update/forms.html'}).
            when('/error/:uuid', {controller: ErrorCtrl, templateUrl: '../../moduleResources/muzimacore/partials/error.html'}).
            when('/errors', {controller: ErrorsCtrl, templateUrl: '../../moduleResources/muzimacore/partials/errors.html'}).
            when('/duplicates', {controller: PotentialDuplicatesErrorsCtrl, templateUrl: '../../moduleResources/muzimacore/partials/potential_duplicates.html'}).
            when('/merge/:uuid', {controller: MergeCtrl, templateUrl: '../../moduleResources/muzimacore/partials/merge.html'}).
            when('/edit/:uuid', {controller: EditCtrl, templateUrl: '../../moduleResources/muzimacore/partials/edit.html'}).
            when('/setting/:uuid', {controller: SettingCtrl, templateUrl: '../../moduleResources/muzimacore/partials/setting.html'}).
            when('/settings', {controller: SettingsCtrl, templateUrl: '../../moduleResources/muzimacore/partials/settings.html'}).
            when('/createSetting/', {controller: SettingCtrl, templateUrl: '../../moduleResources/muzimacore/partials/setting.html'}).
		    when('/cohortDefinitions', {controller: CohortDefinitionsCtrl, templateUrl: '../../moduleResources/muzimacore/partials/cohortdefinitions.html'}).
            when('/cohortDefinition', {controller: CohortDefinitionCtrl, templateUrl: '../../moduleResources/muzimacore/partials/cohortdefinition.html'}).
            when('/cohortDefinition/:uuid', {controller: CohortDefinitionCtrl, templateUrl: '../../moduleResources/muzimacore/partials/cohortdefinition.html'}).
            when('/createCohortDefinition', {controller: CohortDefinitionCtrl, templateUrl: '../../moduleResources/muzimacore/partials/cohortdefinition.html'}).
            when('/reportConfig/:uuid', {controller: ReportConfigurationCtrl, templateUrl: '../../moduleResources/muzimacore/partials/reportConfiguration.html'}).
            when('/reportConfigs', {controller: ReportConfigurationsCtrl, templateUrl: '../../moduleResources/muzimacore/partials/reportConfigurations.html'}).
            when('/createReportConfig/', {controller: ReportConfigurationCtrl, templateUrl: '../../moduleResources/muzimacore/partials/reportConfiguration.html'}).
            otherwise({redirectTo: '/sources'});
    }]
);

muzimaCoreModule.factory('$data', function ($http) {
    var getQueues = function (search, pageNumber, pageSize) {
        if (search === undefined) {
            // replace undefined search term with empty string
            search = '';
        }
        return $http.get("queues.json?search=" + search + "&pageNumber=" + pageNumber + "&pageSize=" + pageSize);
    };
    var deleteQueues = function (uuidList) {
        return $http.post("queues.json", {"uuidList": uuidList});
    };
    var getQueue = function (uuid) {
        return $http.get("queue.json?uuid=" + uuid);
    };

    var getErrors = function (search, pageNumber, pageSize) {
        if (search === undefined) {
            // replace undefined search term with empty string
            search = '';
        }
        return $http.get("errors.json?search=" + search + "&pageNumber=" + pageNumber + "&pageSize=" + pageSize);
    };
    var reQueueErrors = function (uuidList) {
        return $http.post("errors.json", {"uuidList": uuidList});
    };
    var getError = function (uuid) {
        return $http.get("error.json?uuid=" + uuid);
    };

    var getSources = function (search, pageNumber, pageSize) {
        if (search === undefined) {
            // replace undefined search term with empty string
            search = '';
        }
        return $http.get("sources.json?search=" + search + "&pageNumber=" + pageNumber + "&pageSize=" + pageSize);
    };
    var getSource = function (uuid) {
        return $http.get("source.json?uuid=" + uuid);
    };
    var saveSource = function (uuid, name, description) {
        return $http.post("source.json", {"uuid": uuid, "name": name, "description": description});
    };
    var deleteSource = function (uuid,retireReason) {
        return $http.post("source.json", {"uuid": uuid,"retireReason": retireReason});
    };
    var getEdit = function (uuid) {
        return $http.get("edit.json?uuid=" + uuid);
    };
    var editErrors = function (formData) {
        return $http.post("edit.json",{"formData": formData});
    };
    var validateData = function (uuid, formData) {
        return $http.post("validate.json?uuid="+uuid,formData);
    };
    var saveEditedFormData = function (uuid, formData) {
        return $http.post("error.json?uuid="+uuid,formData);
    };

    var mergePatient = function(info) {
        return $http.post('mergePatient.json', info);
    };

    var requeueDuplicatePatient = function(info) {
        return $http.post('requeueDuplicatePatient.json', info);
    };

    var getPatientByIdentifier = function (identifier) {
        return $http.get('../../ws/rest/v1/patient?identifier=' + identifier + "&v=full");
    };
    return {
        getQueues: getQueues,
        getQueue: getQueue,
        deleteQueue: deleteQueues,

        getErrors: getErrors,
        getError: getError,
        reQueueErrors: reQueueErrors,

        getSources: getSources,
        getSource: getSource,
        saveSource: saveSource,
        deleteSource: deleteSource,
        saveEditedFormData : saveEditedFormData,

        getEdit: getEdit,
        editErrors: editErrors,
        validateData: validateData,

        getPatientByIdentifier: getPatientByIdentifier,
        mergePatient: mergePatient,
        requeueDuplicatePatient: requeueDuplicatePatient
    };
});

muzimaCoreModule.factory('FormService', function ($http) {

    var get = function (id) {
        return $http.get('../../ws/rest/v1/muzima/form/' + id + "?v=custom:(id,uuid,name,modelXml,modelJson,metaJson,html,tags,version,description,discriminator)");
    };
    var save = function (form) {
        return $http.post('form.form', form);
    };
    var all = function () {
        return $http.get('../../ws/rest/v1/muzima/form', {cache: false});
    };
    var getForms = function() {
        return $http.get('../../ws/rest/v1/form?v=custom:(name,uuid,version,description)');
    };
    var retire = function (form, retireReason) {
        return $http.delete('retire/' + form.id +'.form' +'?retireReason=' + retireReason);
    };
    var getDiscriminatorTypes = function() {
        return $http.get('../../module/muzimacore/discriminator.json', {cache: false});
    };
    var searchForms = function(search) {
        return $http.get('../../ws/rest/v1/form?v=custom:(name,uuid,version,description,retired)&q=' + (search === undefined ? '' : search));
    };

    return {
        all: all,
        get: get,
        save: save,
        getForms: getForms,
        retire: retire,
        getDiscriminatorTypes: getDiscriminatorTypes,
        searchForms: searchForms
    }
});

muzimaCoreModule.factory('XFormService', function ($http) {

    var moduleState = function () {
        return $http.get('moduleStatus.form');
    };

    var getXForms = function (search, pageNumber, pageSize) {
        return $http.get('xforms.form?search=' + (search === undefined ? '' : search) + '&pageNumber=' + pageNumber + '&pageSize=' + pageSize);
    };

    var save = function (data) {
        return $http({url: 'xforms.form', method: 'POST', params: data});
    };

    var getDiscriminatorTypes = function() {
        return $http.get('../../module/muzimacore/discriminator.json', {cache: false});
    };

    return {
        moduleState: moduleState,
        getXForms: getXForms,
        save: save,
        getDiscriminatorTypes: getDiscriminatorTypes
    };
});

muzimaCoreModule.factory('TagService', function ($http) {
    var all = function () {
        return $http.get('../../ws/rest/v1/muzima/tag');
    };
    return {all: all};
});

muzimaCoreModule.factory('FileUploadService', function ($http) {
    return {
        post: function (options) {
            return $http({
                method: 'POST',
                url: options.url,
                headers: { 'Content-Type': undefined},
                transformRequest: function (data) {
                    var formData = new FormData();
                    angular.forEach(data.params, function (key, value) {
                        formData.append(value, key);
                    });
                    formData.append("file", data.file);
                    return formData;
                },
                data: {file: options.file, params: options.params}
            })
        }
    };
});

muzimaCoreModule.factory('HtmlFormEntryService', function ($http) {

    var moduleState = function () {

        return $http.get('htmlFormEntrymoduleStatus.form');
    };

    var getHtmlForms = function () {
        
        return $http.get('htmlformentry.form');
    };

    var convert = function (form) {
        // TODO: http request to convert the form
        return new Promise(function (resolve, reject) {
            resolve(
                { uuid: "e72c4bd8-6329-4eb4-b0bd-1c4729ada98e",
                id: 10,
                name: "mUzima Registration Form",
                discriminator: "json-registration",
                description: "mUzima Registration Form for testing purposes",
                html: `<html>

                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <link href="css/bootstrap.min.css" rel="stylesheet">
                    <link href="css/muzima.css" rel="stylesheet">
                    <link href="css/ui-darkness/jquery-ui-1.10.4.custom.min.css" rel="stylesheet">
                    <script src="js/jquery.min.js"></script>
                    <script src="js/jquery-ui-1.10.4.custom.min.js"></script>
                    <script src="js/jquery.validate.min.js"></script>
                    <script src="js/additional-methods.min.js"></script>
                    <script src="js/muzima.js"></script>
                
                    <title>Basic Registration Form</title>
                </head>
                
                <body class="col-md-10 col-md-offset-1">
                    <div id="result"></div>
                    <form id="basic_registration_form" name="basic_registration_form">
                    <div id="pre_populate_data"></div>
                    <h2 class="text-center">Basic Registration Form</h2>
                
                        <div class="section">
                            <h3>Demographics</h3>
                
                            <div class="form-group">
                            <input class="form-control" id="patient.uuid"
                                   name="patient.uuid" type="hidden" readonly="readonly">
                            </div>
                            <div class="form-group">
                                <label for="patient.family_name">Family Name: <span class="required">*</span> </label>
                                <input class="form-control" id="patient.family_name" name="patient.family_name" type="text"
                                       required="required">
                            </div>
                            <div class="form-group">
                                <label for="patient.given_name">Given Name: <span class="required">*</span></label>
                                <input class="form-control" id="patient.given_name" name="patient.given_name" type="text"
                                       required="required">
                            </div>
                            <div class="form-group">
                                <label for="patient.middle_name">Middle Name:</label>
                                <input class="form-control" id="patient.middle_name" name="patient.middle_name" type="text">
                            </div>
                            <div class="form-group">
                                <label for="patient.sex">Gender: <span class="required">*</span></label>
                                <select class="form-control" id="patient.sex" name="patient.sex" required="required">
                                    <option value="">...</option>
                                    <option value="M">Male</option>
                                    <option value="F">Female</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="patient.birth_date">Date Of Birth: <span class="required">*</span></label>
                                <input class="form-control datepicker past-date" id="patient.birth_date" name="patient.birth_date" type="text" required="required">
                            </div>
                            <div class="form-group">
                                <label for="patient.medical_record_number">Medical Record Number : <span class="required require_medical_record_number_hint">*</span></label>
                
                                <div class="form-horizontal">
                                    <div class="group-set" data-group="patient.medical_record_number">
                                      <input type="button" class='btn barcode_btn'>
                                      <input class="barcode_text form-control" id="identifier_value"
                                           name="identifier_value" type="text">
                                         <input data-metadata-for="identifier_value" type="hidden" id="identifier_type_uuid" name="identifier_type_uuid" value="8d793bee-c2cc-11de-8d13-0010c6dffd0f">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="section">
                            <h3>Encounter Details</h3>
                
                            <div class="form-group show_provider_id_text">
                                <label for="encounter.provider_id">Provider's ID: <span class="required">*</span></label>
                                <input class="form-control checkDigit" id="encounter.provider_id" name="encounter.provider_id"
                                   type="text" required="required">
                            </div>
                            <div class="form-group">
                                <label for="encounter.location_id">Encounter Location: <span class="required">*</span></label>
                                <input class="form-control valid-location-only" id="encounter.location_id" type="text" placeholder="Start typing something..." required="required">
                                <input class="form-control" name="encounter.location_id" type="hidden">
                            </div>
                
                            <div class="form-group hidden">
                                <label for="encounter.location_id_select">Encounter Location: <span class="required">*</span></label>
                                <select class="form-control" id="encounter.location_id_select" required="required">
                                    <option>...</option>
                                </select>
                            </div>
                
                            <div class="form-group">
                                <label for="encounter.encounter_datetime">Encounter Date <span class="required">*</span></label>
                                <input class="form-control nonFutureDate past-date datepicker" readonly="readonly" id="encounter.encounter_datetime"
                                       name="encounter.encounter_datetime" type="text" required="required">
                            </div>
                
                            <div class="form-group show_uuid_text">
                                <label for="encounter.form_uuid">Form uuid: <span class="required">*</span></label>
                                <input class="form-control" id="encounter.form_uuid" name="encounter.form_uuid"
                                       type="text" required="required">
                            </div>
                        </div>
                
                        <div class="section">
                            <h3>Additional Details</h3>
                
                            <div class="section repeat" data-group="patient.personattribute">
                              <h4>Attributes</h4>
                              <div class="form-group group-set other_identifier_type" id="other_identifier_type">
                                  <label for="attribute_type_uuid">Attribute Type</label>
                                  <select class="form-control attribute_type_uuid" name="attribute_type_uuid"  id="attribute_type_uuid">
                                      <option value="">...</option>
                                      <option value="8d871d18-c2cc-11de-8d13-0010c6dffd0f">Mother's Name</option>
                                      <option value="8037ba06-fc79-4244-9d14-687baa44bd81">Contact Phone Number</option>
                                  </select>
                              </div>
                              <div class="form-group phone_number">
                                <label for="patient.phone_number">Attribute Value: </label>
                                <input class="form-control phoneNumber" id="patient.phone_number" name="attribute_value" type="tel">
                              </div>
                              <div class="form-group mothers_name">
                                <label for="patient.mothers_name">Attribute Value: </label>
                                <input class="form-control mothers_name" id="patient.mothers_name" name="attribute_value" type="text">
                              </div>
                            </div>
                            <div class="section group-set repeat" data-group="patient.personaddress">
                              <h4>Address</h4>
                              <div class="form-group">
                                  <label for="patient.county">County: </label>
                                  <input class="form-control" id="countyDistrict" name="countyDistrict" type="text">
                              </div>
                              <div class="form-group">
                                  <label for="patient.location">Location: </label>
                                  <input class="form-control" id="address6" name="address6" type="text">
                              </div>
                              <div class="form-group">
                                  <label for="patient.sub_location">Sub-location: </label>
                                  <input class="form-control" id="address5" name="address5" type="text">
                              </div>
                              <div class="form-group">
                                  <label for="patient.village">Village: </label>
                                  <input class="form-control" id="cityVillage" name="cityVillage" type="text">
                              </div>
                            </div>
                            <div class="section group-set repeat" data-group="patient.other_medical_record_numbers">
                              <h4>Other Medical Record Number</h4>
                              <div class="form-group">
                                  <label for="patient.other_amrs">AMRS Medical Record Number: </label>
                                  <input class="form-control checkDigit" id="other_medical_record_number" name="other_medical_record_number" type="text">
                              </div>
                            </div>            
                        </div>
                    </form>
                </body>
                
                <script type="text/javascript">
                
                $(document).ready(function () {
                
                    $('#save_draft').click(function () {
                        $(this).prop('disabled', true);
                        document.saveDraft(this);
                        $(this).prop('disabled', false);
                    });
                
                    $('#submit_form').click(function () {
                        $(this).prop('disabled', true);
                        document.submit();
                        $(this).prop('disabled', false);
                    });
                
                    $('#basic_registration_form').validate({
                
                        submitHandler: function (form) {
                            $('#result').html(JSON.stringify($('form').serializeEncounterForm(), undefined, 2));
                        }
                    });
                
                    var show_message=function(element_id){
                        $(element_id).show();
                    }
                    var hide_message=function(element_id){
                        $(element_id).hide();
                    }
                
                    document.setupAutoCompleteData('encounter\\.location_id');
                
                    document.setupAutoCompleteDataForProvider('encounter\\.provider_id_select');
                
                    document.setupValidationForProvider("$('#encounter\\.provider_id_select').val()","encounter\\.provider_id");
                
                    document.setupValidationForLocation("$('#encounter\\.location_id').val()","encounter\\.location_id");
                
                });
                </script>
                
                </html>`
                }
            );
        });
    };

    var save = function (form) {
        // TODO: Http request to save the form
        return new Promise(function (resolve, reject) {
            resolve(true);
        });
    };

    var getDiscriminatorTypes = function () {
        return $http.get('../../module/muzimacore/discriminator.json', { cache: false });
    };

    return {
        moduleState: moduleState,
        getHtmlForms: getHtmlForms,
        convert: convert,
        save: save,
        getDiscriminatorTypes: getDiscriminatorTypes
    };
});

muzimaCoreModule.factory('_', function () {
    return window._;
});

muzimaCoreModule.factory('$registrations', function($http) {
    var getRegistration = function(uuid) {
        return $http.get("registration.json?uuid=" + uuid);
    };
    var getRegistrations = function(pageNumber, pageSize) {
        return $http.get("registrations.json?pageNumber=" + pageNumber + "&pageSize=" + pageSize);
    };
    return {
        getRegistrations: getRegistrations,
        getRegistration: getRegistration
    }
});

muzimaCoreModule.factory('$configs', function($http) {
    var getConfiguration = function(uuid) {
        return $http.get("config.json?uuid=" + uuid);
    };
    var getConfigurations = function(search, pageNumber, pageSize) {
        return $http.get("configs.json?search=" + (search === undefined ? '' : search) + "&pageNumber=" + pageNumber + "&pageSize=" + pageSize);
    };
    var saveConfiguration = function (uuid, name, description, configJson) {
        return $http.post("config.json", {"uuid": uuid, "name": name, "description": description, "configJson": configJson});
    };
    var deleteConfiguration = function (uuid,retireReason) {
        return $http.post("config.json", {"uuid": uuid, "retireReason": retireReason});
    };
    var searchConfigForms = function(search) {
        return $http.get("configForms.json?search=" + (search === undefined ? '' : search));
    };
    var searchConfigCohorts = function(search) {
        return $http.get("configCohorts.json?search=" + (search === undefined ? '' : search));
    };
    var searchConfigLocations = function(search) {
        return $http.get("configLocations.json?search=" + (search === undefined ? '' : search));
    };
    var searchConfigConcepts = function(search) {
        return $http.get('../../ws/rest/v1/concept?v=custom:(uuid,name:(uuid,name))&q=' + (search === undefined ? '' : search));
    };
    var searchConfigProviders = function(search) {
        return $http.get('../../ws/rest/v1/provider?v=custom:(uuid,name:(uuid,name))&q=' + (search === undefined ? '' : search));
    };
    return {
        getConfiguration: getConfiguration,
        getConfigurations: getConfigurations,
        saveConfiguration: saveConfiguration,
        deleteConfiguration: deleteConfiguration,
        searchConfigForms: searchConfigForms,
        searchConfigCohorts: searchConfigCohorts,
        searchConfigLocations: searchConfigLocations,
        searchConfigProviders: searchConfigProviders,
        searchConfigConcepts: searchConfigConcepts
    }
});

muzimaCoreModule.factory('$muzimaSettings', function($http) {

    var getSettings = function (search, pageNumber, pageSize) {
        if (search === undefined) {
            // replace undefined search term with empty string
            search = '';
        }
        return $http.get("settings.json?search=" + search + "&pageNumber=" + pageNumber + "&pageSize=" + pageSize);
    };
    var getSetting = function (uuid) {
        return $http.get("setting.json?uuid=" + uuid);
    };
    var saveSetting = function (uuid, property, name, description, value) {
        return $http.post("setting.json", {"uuid": uuid, "property": property, "name": name, "description": description,"value": value});
    };
    var deleteSetting = function (uuid) {
        return $http.post("source.json", {"uuid": uuid});
    };

    return {
        getSettings: getSettings,
        getSetting: getSetting,
        saveSetting: saveSetting,
        deleteSetting: deleteSetting
    }
});

muzimaCoreModule.factory('$muzimaReportConfigurations', function($http) {

    var getReportConfigurations = function (search, pageNumber, pageSize) {
        if (search === undefined) {
            // replace undefined search term with empty string
            search = '';
        }
        return $http.get("reportConfigs.json?search=" + search + "&pageNumber=" + pageNumber + "&pageSize=" + pageSize);
    };
    var getReportConfiguration = function (uuid) {
        return $http.get("reportConfig.json?uuid=" + uuid);
    };
    var saveReportConfiguration = function (uuid, cohortUuid,configJson, priority) {
        return $http.post("reportConfig.json", {"uuid": uuid,"cohortUuid": cohortUuid, "reportConfigJson": configJson, "priority": priority});
    };
    var deleteReportConfiguration = function (uuid,retireReason) {
        return $http.post("delete/reportConfig.json", {"uuid": uuid, "retireReason": retireReason});
    };

    var searchReportConfigCohorts = function(search) {
           return $http.get("configCohorts.json?search=" + (search === undefined ? '' : search));
     };

     var searchReportConfigReports = function(search) {
               return $http.get("reportConfigReports.json?search=" + (search === undefined ? '' : search));
     };

    return {
        getReportConfigurations: getReportConfigurations,
        getReportConfiguration: getReportConfiguration,
        saveReportConfiguration: saveReportConfiguration,
        deleteReportConfiguration: deleteReportConfiguration,
        searchReportConfigCohorts: searchReportConfigCohorts,
        searchReportConfigReports: searchReportConfigReports
    }
});


muzimaCoreModule.factory('$cohortDefinitionService', function ($http) {

    var getCohortDefinitions = function (pageNumber, pageSize) {
        return $http.get("cohortDefinitions.json?pageNumber=" + pageNumber + "&pageSize=" + pageSize);
    };
    var getCohortDefinition = function (uuid) {
            return $http.get("cohortDefinition.json?uuid=" + uuid);
        };
    var getAllCohorts = function () {
            return $http.get("cohorts.json");
        };
    var getAllCohortsWithoutDefinition=function(){
            return $http.get("cohortswithoutdefinition.json");
         };
    var saveCohortDefinition = function (uuid, cohortid, definition, isScheduledForExecution, isMemberAdditionEnabled, isMemberRemovalEnabled) {
            return $http.post("cohortDefinition.json", {"uuid": uuid, "cohortid":cohortid, "definition": definition,
                "isScheduledForExecution": isScheduledForExecution, "isMemberAdditionEnabled":isMemberAdditionEnabled, "isMemberRemovalEnabled": isMemberRemovalEnabled});
        };

    var deleteCohortDefinition = function (uuid, cohortid, definition, isScheduledForExecution, isMemberAdditionEnabled, isMemberRemovalEnabled, retireReason) {
        return $http.post("cohortDefinition.json", {"uuid": uuid, "cohortid":cohortid, "definition": definition, "isScheduledForExecution": isScheduledForExecution,
         "isMemberAdditionEnabled":isMemberAdditionEnabled, "isMemberRemovalEnabled": isMemberRemovalEnabled, "retireReason": retireReason});
    };

    return {

        getCohortDefinitions: getCohortDefinitions,
        getCohortDefinition:getCohortDefinition,
        saveCohortDefinition:saveCohortDefinition,
        getAllCohorts:getAllCohorts,
        getAllCohortsWithoutDefinition:getAllCohortsWithoutDefinition,
        deleteCohortDefinition : deleteCohortDefinition
    }
});

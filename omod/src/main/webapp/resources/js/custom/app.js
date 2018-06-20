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
            when('/import/forms', {controller: ImportCtrl, templateUrl: '../../moduleResources/muzimacore/partials/import/forms.html'}).
            when('/update/forms/:muzimaform_uuid',{controller: UpdateCtrl, templateUrl: '../../moduleResources/muzimacore/partials/update/forms.html'}).
            when('/error/:uuid', {controller: ErrorCtrl, templateUrl: '../../moduleResources/muzimacore/partials/error.html'}).
            when('/errors', {controller: ErrorsCtrl, templateUrl: '../../moduleResources/muzimacore/partials/errors.html'}).
            when('/edit/:uuid', {controller: EditCtrl, templateUrl: '../../moduleResources/muzimacore/partials/edit.html'}).
            when('/setting/:uuid', {controller: SettingCtrl, templateUrl: '../../moduleResources/muzimacore/partials/setting.html'}).
            when('/settings', {controller: SettingsCtrl, templateUrl: '../../moduleResources/muzimacore/partials/settings.html'}).
            when('/createSetting/', {controller: SettingCtrl, templateUrl: '../../moduleResources/muzimacore/partials/setting.html'}).
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
    var deleteSource = function (uuid) {
        return $http.post("source.json", {"uuid": uuid});
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
        validateData: validateData
    }
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
        return $http.get('../../ws/rest/v1/form?v=custom:(name,uuid,version,description)&q=' + (search === undefined ? '' : search));
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
    var deleteConfiguration = function (uuid) {
        return $http.post("config.json", {"uuid": uuid});
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

muzimaCoreModule.factory('$reportConfigurations', function($http) {

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
    var saveReportConfiguration = function (uuid, property, name, description, value) {
        return $http.post("reportConfig.json", {"uuid": uuid, "property": property, "name": name, "description": description,"value": value});
    };
    var deleteReportConfiguration = function (uuid) {
        return $http.post("reportConfig.json", {"uuid": uuid});
    };

    return {
        getReportConfigurations: getReportConfigurations,
        getReportConfiguration: getReportConfiguration,
        saveReportConfiguration: saveReportConfiguration,
        deleteReportConfiguration: deleteReportConfiguration
    }
});

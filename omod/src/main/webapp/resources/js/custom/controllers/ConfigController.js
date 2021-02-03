function ConfigCtrl($scope,$uibModal, $routeParams, $location, $configs, FormService,$cohortDefinitionService) {

    // initialize control objects
    $scope.search = {forms: '', cohorts: '', locations: '', providers: '', concepts: ''};
    $scope.selected = {forms: '', cohorts: '', locations: '', providers: '', concepts: [], eConcepts: []};
    $scope.specialFields = {showConfigJson: '', stillLoading: true, extractingMeta: false};

    // initialize the config objects
    $scope.config = {};
    $scope.configForms = [];
    $scope.configCohorts = [];
    $scope.configLocations = [];
    $scope.configProviders = [];
    $scope.extractedConcepts = [];
    $scope.extractedNotUsedConcepts = [];
    $scope.availableNotUsedLocations = [];
    $scope.configConcepts = [];
    $scope.configSettings = [];
    $scope.retire_config = false;
    $scope.retire_reason = false;

    $scope.muzimaforms = [];

    // initialize the view to be read only
    $scope.mode = "view";
    $scope.uuid = $routeParams.uuid;
    if ($scope.uuid === undefined) {
        $scope.mode = "edit";
        $('#wait').hide();
    } else {
        $configs.getConfiguration($scope.uuid).then(function (response) {
            $scope.config = response.data;
            var configString = JSON.parse($scope.config.configJson);
            if (configString != null && configString != undefined) {
                if (configString.config["forms"] != undefined)
                    $scope.configForms = configString.config["forms"];
                if (configString.config["cohorts"] != undefined)
                    $scope.configCohorts = configString.config["cohorts"];
                if (configString.config["locations"] != undefined)
                    $scope.configLocations = configString.config["locations"];
                if (configString.config["providers"] != undefined)
                    $scope.configProviders = configString.config["providers"];
                if (configString.config["concepts"] != undefined)
                    $scope.configConcepts = configString.config["concepts"];
                if (configString.config["settings"] != undefined)
                    $scope.configSettings = configString.config["settings"];
            }
        }).then(function () {
            $scope.bindData();
            $scope.setEvent();
            $('#wait').hide();
        });
    }

    $configs.searchMuzimaForms().then(function (response) {
        var metaObjects = response.data.metaObjects;
        angular.forEach(metaObjects, function (object) {
            if (object.metaJson != undefined && object.metaJson != null) {
                var metaJson = JSON.parse(object.metaJson);
                if (metaJson.concepts != undefined) {
                    $scope.extractedConcepts = _.unionWith($scope.extractedConcepts, metaJson.concepts, _.isEqual);
                }
            }
        });

        //pick only the not used concepts
    }).then(function () {
        $scope.extractedNotUsedConcepts = _.differenceWith($scope.extractedConcepts,$scope.configConcepts, _.isEqual);
        $scope.specialFields.stillLoading=false;
    });

    $scope.loadForms = function() {
        return new Promise((resolve, reject) => {
            FormService.all().then(function (response) {
                $scope.muzimaforms = _.map(response.data.results, function (form) {
                    return {
                        form: form,
                        newTag: "",
                        retired: false,
                        retireReason: ''
                    };
                });
                resolve($scope.muzimaforms);
            });
        });
    }


    $scope.save = function (config) {
        $configs.saveConfiguration(config.uuid, config.name, config.description, createJson(config)).
        then(function () {
            $location.path("/configs");
        })
    };

    var createJson = function (config) {
        var configJsonString = {"config":{}};
        configJsonString.config["willRegisteringPatients"] = config.willRegisteringPatients;
        configJsonString.config["name"] = config.name;
        configJsonString.config["description"] = config.description;
        configJsonString.config["forms"] = $scope.configForms;
        configJsonString.config["cohorts"] = $scope.configCohorts;
        configJsonString.config["locations"] = $scope.configLocations;
        configJsonString.config["providers"] = $scope.configProviders;
        configJsonString.config["concepts"] = $scope.configConcepts;
        configJsonString.config["settings"] = $scope.configSettings;
        return angular.toJson(configJsonString);
    };

    $scope.toggleRetireConfig = function(){
        $scope.retire_config = true;
    };

    $scope.delete = function (config) {
        if(!config.retireReason){
             $scope.config.retireReasonError = true;
        }else{
            $configs.deleteConfiguration(config.uuid,config.retireReason).
            then(function () {
                $location.path("/configs");
            });
        }
    };

    $scope.edit = function () {
        $scope.mode = "edit";
    };

    $scope.cancel = function () {
        $location.path("/configs");
    };

    /****************************************************************************************
     ***** Group of methods to manipulate muzima forms
     *****************************************************************************************/
    $scope.$watch('search.forms', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $configs.searchMuzimaForms($scope.search.forms).
            then(function (response) {
                $scope.forms = response.data.objects;
            });
        }
    }, true);


    var formExistsInConfig = function(formUuid){
        return !!_.find($scope.configForms, function (configForm) {
            return configForm.uuid == formUuid;
        });
    }

    $scope.configHasRegistrationForms = function(){
        return !!_.find($scope.configForms, function (configForm) {
            return configForm.discriminator!= undefined && configForm.discriminator.includes("registration");
        });
    }

    $scope.configHasNonRegistrationForms = function(){
        return !!_.find($scope.configForms, function (configForm) {
            return !(configForm.discriminator!=undefined && configForm.discriminator.includes("registration"));
        });
    }

    var addFormToConfig = function(form){
        form = {'uuid':form.uuid,'name':form.name,'discriminator':form.discriminator};
        $scope.configForms.push(form);
        $scope.search.forms = '';
        $scope.specialFields.extractingMeta=true;

        FormService.get(form.uuid).then(function (response) {

            var formResult = response.data;
            var metaJson = JSON.parse(formResult.metaJson);
            if (metaJson != null && metaJson.concepts != undefined) {
                angular.forEach(metaJson.concepts, function (mConcept) {
                    var conceptExists = _.find($scope.extractedConcepts, function (eConcept) {
                        return mConcept.uuid == eConcept.uuid
                    });

                    if (!conceptExists)
                        $scope.extractedConcepts.push(mConcept);
                });
            }
            //pick only the not used concepts
        }).then(function () {
            $scope.extractedNotUsedConcepts = [];
            angular.forEach($scope.extractedConcepts, function (eConcept) {
                var conceptExists = _.find($scope.configConcepts, function (configConcept) {
                    return configConcept.uuid == eConcept.uuid
                });

                if (!conceptExists)
                    $scope.extractedNotUsedConcepts.push(eConcept);
            });

            $scope.specialFields.extractingMeta=false;
        });
    }

    $scope.addForm = function(form) {
        var formExists = formExistsInConfig(form.uuid);
        if (!formExists) {
            addFormToConfig(form);
        }
    };

    $scope.chosenForm = function (value) {
        $scope.selected.form = value;
    };

    var removeFormFromConfig = function(formUuid){
        angular.forEach($scope.configForms, function (configForm, index) {
            if (configForm.uuid === formUuid) {
                $scope.configForms.splice(index, 1);
                $scope.selected.form = '';
            }
        });
    }

    $scope.removeForm = function () {
        removeFormFromConfig($scope.selected.form);
    };

    /****************************************************************************************
     ***** Group of methods to manipulate Cohorts
     *****************************************************************************************/
    $scope.cohorts = [];
    $scope.loadCohorts = function() {
        return new Promise((resolve, reject) => {
            $cohortDefinitionService.getAllCohorts().then(function (response) {
                $scope.cohorts = response.data.objects;
            });
            resolve($scope.cohorts);
        });
    }

    $scope.$watch('search.cohorts', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $configs.searchCohorts($scope.search.cohorts).
            then(function (response) {
                $scope.cohorts = response.data.objects;
            });
        }
    }, true);

   $scope.cohortExistsInConfig = function(cohort) {
        return !!_.find($scope.configCohorts, function (configCohort) {
            return configCohort.uuid == cohort.uuid
        });
    }

    $scope.addCohort = function(cohort) {

        if (!$scope.cohortExistsInConfig(cohort)) {
            cohort = {'uuid':cohort.uuid,'name':cohort.name};
            $scope.configCohorts.push(cohort);
            $scope.search.cohorts = '';
        }
    };



    $scope.chosenCohort = function (value) {
        $scope.selected.cohort = value;
    };

    $scope.removeCohort = function () {
        angular.forEach($scope.configCohorts, function (configCohort, index) {
            if (configCohort.uuid === $scope.selected.cohort) {
                $scope.configCohorts.splice(index, 1);
                $scope.selected.cohort = '';
            }
        });
    };

    $scope.hasDefinedCohorts = function(){
        return $scope.cohorts.length > 0
    }

    $scope.configHasCohorts = function(){
        return $scope.configCohorts.length > 0;
    }

    $scope.selectedCohorts = []

    $scope.setSelectedCohort = function (cohort) {
        $scope.selectedCohorts.push(cohort.uuid);
        //add to config
        $scope.addCohort(cohort);
    }

    $scope.toggleCohortSelection = function(selectedCohort){
        if($scope.cohortExistsInConfig(selectedCohort)){
            angular.forEach($scope.configCohorts, function (configCohort, index) {
                if (configCohort.uuid === selectedCohort.uuid) {
                    $scope.configCohorts.splice(index, 1);
                }
            });
        } else {
            $scope.addCohort(selectedCohort);
        }
    }

    /****************************************************************************************
     ***** Group of methods to manipulate locations
     *****************************************************************************************/
    $scope.$watch('search.locations', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $configs.searchConfigLocations($scope.search.locations).
            then(function (response) {
                $scope.locations = response.data.objects;
            });
        }
    }, true);

    $scope.loadLocations = function() {
        return new Promise((resolve, reject) => {
            $configs.searchConfigLocations().then(function (response) {
                $scope.availableNotUsedLocations = response.data.objects;
                resolve($scope.availableNotUsedLocations);
            });
        });
    }

    $scope.addLocation = function(location) {
        var locationExists = _.find($scope.configLocations, function (configLocation) {
            return configLocation.uuid == location.uuid
        });
        if (!locationExists) {
            $scope.configLocations.push(location);
            $scope.search.locations = '';

            angular.forEach($scope.availableNotUsedLocations, function (availableLocation, index) {
                if (location.uuid === availableLocation.uuid) {
                    $scope.availableNotUsedLocations.splice(index, 1);
                    $scope.selected.location = '';
                }
            });
        }
    };

    $scope.chosenLocation = function (value) {
        $scope.selected.location = value;
    };

    $scope.removeLocation = function () {
        angular.forEach($scope.configLocations, function (configLocation, index) {
            if (configLocation.uuid === $scope.selected.location) {
                $scope.configLocations.splice(index, 1);
                $scope.selected.location = '';
            }
        });
    };

    $scope.removeSelectedLocations = function () {
        if ($scope.selected.locations != undefined && $scope.selected.locations != null) {
            angular.forEach($scope.selected.locations, function (location) {
                var selectedLocation = JSON.parse(location);
                var locationIndex = _.findIndex($scope.configLocations, function (configLocation) {
                    return configLocation.uuid == selectedLocation.uuid
                });

                // remove it from configs
                if (locationIndex >= 0) {
                    $scope.configLocations.splice(locationIndex, 1);
                    $scope.availableNotUsedLocations.push(selectedLocation);
                }
            });
            $scope.selected.locations = [];
        }
    };

    $scope.moveAllAvailableLocations = function () {
        angular.forEach($scope.availableNotUsedLocations, function (eLocation) {
            var locationExists = _.find($scope.configLocations, function (configLoction) {
                return configLoction.uuid == eLocation.uuid
            });

            if (!locationExists)
                $scope.configLocations.push(eLocation);
        });
        $scope.availableNotUsedLocations = [];
    };

    $scope.moveSelectedLocations = function () {
        if ($scope.selected.eLocations != undefined && $scope.selected.eLocations != null) {
            angular.forEach($scope.selected.eLocations, function (eLocation) {
                var selectedLocation = JSON.parse(eLocation);
                $scope.configLocations.push(selectedLocation);

                angular.forEach($scope.availableNotUsedLocations, function (location, index) {
                    if (location.uuid === selectedLocation.uuid) {
                        $scope.availableNotUsedLocations.splice(index, 1);
                        $scope.selected.location = '';
                    }
                });
            });
            $scope.selected.eLocations = [];
        }
    };

    /****************************************************************************************
     ***** Group of methods to manipulate providers
     *****************************************************************************************/
    $scope.$watch('search.providers', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $configs.searchConfigProviders($scope.search.providers).
            then(function (response) {
                $scope.providers = response.data.results;
            });
        }
    }, true);

    $scope.addProvider = function(provider) {
        var providerExists = _.find($scope.configProviders, function (configProvider) {
            return configProvider.uuid == provider.uuid
        });
        if (!providerExists) {
            $scope.configProviders.push(provider);
            $scope.search.providers = '';
        }
    };

    $scope.chosenProvider = function (value) {
        $scope.selected.provider = value;
    };

    $scope.removeProvider = function () {
        angular.forEach($scope.configProviders, function (configProvider, index) {
            if (configProvider.uuid === $scope.selected.provider) {
                $scope.configProviders.splice(index, 1);
                $scope.selected.provider = '';
            }
        });
    };

    /****************************************************************************************
     ***** Group of methods to manipulate concepts
     *****************************************************************************************/
    $scope.$watch('search.concepts', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $configs.searchConfigConcepts($scope.search.concepts).
            then(function (response) {
                $scope.concepts = response.data.results;
            });
        }
    }, true);

    $scope.addConcept = function(concept) {
        var conceptExists = _.find($scope.configConcepts, function (configConcept) {
            return configConcept.uuid == concept.uuid
        });
        if (!conceptExists) {
            var jsonConcept = {};
            jsonConcept["uuid"] = concept.uuid;
            jsonConcept["name"] = concept.name.name;
            $scope.configConcepts.push(jsonConcept);
            $scope.search.concepts = '';
        }
    };

    $scope.moveAll = function () {
        // lodash is very slow
        //$scope.configConcepts = _.unionWith($scope.extractedConcepts, $scope.configConcepts, _.isEqual);
        //$scope.extractedNotUsedConcepts = _.differenceWith($scope.extractedConcepts, $scope.configConcepts, _.isEqual);
        angular.forEach($scope.extractedConcepts, function (eConcept) {
            var conceptExists = _.find($scope.configConcepts, function (configConcept) {
                return configConcept.uuid == eConcept.uuid
            });

            if (!conceptExists)
                $scope.configConcepts.push(eConcept);
        });
        $scope.extractedNotUsedConcepts = [];
    };

    $scope.moveSelected = function () {
        if ($scope.selected.eConcepts != undefined && $scope.selected.eConcepts != null) {
            angular.forEach($scope.selected.eConcepts, function (eConcept) {
                var selectedConcept = JSON.parse(eConcept);
                $scope.configConcepts.push(selectedConcept);

                //and remove it from extractedNotUsedConcepts
                angular.forEach($scope.extractedNotUsedConcepts, function (concept, index) {
                    if (concept.uuid === selectedConcept.uuid) {
                        $scope.extractedNotUsedConcepts.splice(index, 1);
                        $scope.selected.concept = '';
                    }
                });
            });
            $scope.selected.eConcepts = [];
        }
    };

    $scope.removeSelected = function () {
        if ($scope.selected.concepts != undefined && $scope.selected.concepts != null) {
            angular.forEach($scope.selected.concepts, function (concept) {
                var selectedConcept = JSON.parse(concept);
                var conceptIndex = _.findIndex($scope.configConcepts, function (configConcept) {
                    return configConcept.uuid == selectedConcept.uuid
                });

                // remove it from configs
                if (conceptIndex >= 0)
                    $scope.configConcepts.splice(conceptIndex, 1);

                // and repush it to extractedNotUsedConcepts if it was from forms
                var conceptExists = _.find($scope.extractedConcepts, function (eConcept) {
                    return selectedConcept.uuid == eConcept.uuid
                });

                if (conceptExists)
                    $scope.extractedNotUsedConcepts.push(selectedConcept);
            });
            $scope.selected.concepts = [];
        }
    };


    /****************************************************************************************
     ***** Group of methods to manipulate settings
     *****************************************************************************************/
    $scope.$watch('search.settings', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $configs.searchConfigSettings($scope.search.settings).
            then(function (response) {
                $scope.settings = response.data.objects;
            });
        }
    }, true);

    $scope.formatSettingDisplay = function (setting) {
        var value = '';
        if (setting.datatype == 'BOOLEAN'){
            value = setting.value == true? 'Enabled':'Disabled';
        } else if(setting.datatype == 'PASSWORD'){
            for(i=0;i<setting.value.length;i++)
            {
                value=value+"*";
            }
        } else {
            value = setting.value
        }
        return setting.name + ' : ' + value;
    }
    var showSettingEditModal = function(setting) {
        $scope.setting = setting;
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: '../../moduleResources/muzimacore/partials/editConfigSetting.html',
            size: 'md',
            scope: $scope,
            resolve: {
                items: function () {
                    return $scope.setting;
                }
            }
        });
        $scope.dismiss = function(){
            modalInstance.close();
        }
    }

    $scope.addSetting = function(setting) {
        var settingExists = _.find($scope.configSettings, function (configSetting) {
            return configSetting.uuid == setting.uuid
        });
        if (!settingExists) {
            $scope.configSettings.push(setting);
            $scope.search.settings = '';
        }
        showSettingEditModal(setting);
    };

    $scope.chosenSetting = function (value) {
        $scope.selected.setting = value;
    };

    $scope.removeSetting = function () {
        angular.forEach($scope.configSettings, function (configSetting, index) {
            if (configSetting.uuid === $scope.selected.setting) {
                $scope.configSettings.splice(index, 1);
                $scope.selected.setting = '';
            }
        });
    };

    $scope.editSettingValue = function () {
        angular.forEach($scope.configSettings, function (configSetting, index) {
            if (configSetting.uuid === $scope.selected.setting) {
                showSettingEditModal(configSetting);
            }
        });
    };

    /****************************************************************************************
     ***** Group of methods to manipulate config wizard
     *****************************************************************************************/
    var navigationTabs = [];

    var showConfigWizardModal = function() {
        $scope.showConfigWizard = true;

        var getActiveTab = function(){
            if(navigationTabs.length>0) {
                return navigationTabs[navigationTabs.length - 1];
            }
            return '';
        }

        $scope.isActiveTab = function (tabName) {
            return getActiveTab() == tabName;
        }

        var loadWizardTab = function (nextWizardTab, isBackNavigation) {
            if(nextWizardTab == "registration-form-selection" || nextWizardTab == "other-forms-selection"){
                $scope.loadForms().then(()=>{
                    $scope.activeTab = nextWizardTab;
                });
            } else if (nextWizardTab == "cohorts-selection"){
                $scope.loadCohorts().then(()=>{
                    $scope.activeTab = nextWizardTab;
                });
                $scope.activeTab = nextWizardTab;
            } else if (nextWizardTab == "add-locations"){
                $scope.loadLocations().then(()=>{
                    $scope.activeTab = nextWizardTab;
                });
                $scope.activeTab = nextWizardTab;
            } else {
                $scope.activeTab = nextWizardTab;
            }

            if(!isBackNavigation){
                navigationTabs.push(nextWizardTab);
            }
        }
        var activeTab = getActiveTab();
        loadWizardTab(activeTab == ''?'description':activeTab); //initialize first page of wizard

        $scope.goToPreviousWizardTab = function(){
            if(navigationTabs.length > 1){
                navigationTabs.pop();
                loadWizardTab(navigationTabs[navigationTabs.length-1],true);
            }
        }

        $scope.goToNextWizardTab = function(nextWizardTab){
            loadWizardTab(nextWizardTab,false);
        }

        $scope.selectedForms = []

        $scope.setSelectedForm = function (form) {
            $scope.selectedForms.push(form.uuid);
            //add to config
            addFormToConfig(form);
        }

        $scope.unsetDeselectedForm = function(form){
            var index = $scope.selectedForms.indexOf(form.uuid);
            if(index > -1) {
                $scope.selectedForms.splice(index, 1);
                removeFormFromConfig(form.uuid);
            }
        }

        $scope.toggleFormSelection = function(form){
            if(formExistsInConfig(form.uuid)){
                removeFormFromConfig(form.uuid);
            } else {
                addFormToConfig(form);
            }
        }

        $scope.isFormSelected = function(formUuid){
            return $scope.selectedForms.includes(formUuid) || formExistsInConfig(formUuid);
        }

        $scope.isRegistrationForm = function(muzimaform){
            return muzimaform.form.discriminator.includes("registration");
        }

        $scope.isNonRegistrationForm = function(muzimaform){
            return !$scope.isRegistrationForm(muzimaform);
        }

        $scope.hasRegistrationForms = function(){
            for (muzimaform of $scope.muzimaforms){
                if($scope.isRegistrationForm(muzimaform)){
                    return true;
                }
            }
            return false;
        }

        $scope.hasNonRegistrationForms = function(){
            for (muzimaform of $scope.muzimaforms){
                if($scope.isNonRegistrationForm(muzimaform)){
                    return true;
                }
            }
            return false;
        }

        if($scope.configHasRegistrationForms()){
            $scope.config.willRegisteringPatients = true;
        }

        if($scope.configHasNonRegistrationForms()){
            $scope.config.willFillOtherForms = true;
        }

        if($scope.configHasCohorts()){
            $scope.config.willAddCohorts = true;
        }

        $scope.setNewFormMetadata = function (name, version, description,encounterType) {
            $scope.newFormMetaData = {
                name:name,
                version:version,
                description:description,
                encounterType:encounterType,
                uuid:'newFormMetadata'
            }
            $scope.goToPreviousWizardTab();
        };

        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: '../../moduleResources/muzimacore/partials/directives/configWizard.html',
            size: 'xl',
            scope: $scope,
            resolve: {
                items: function () {
                    return true;
                }
            }
        });

        $scope.dismiss = function(){
            modalInstance.close();
        }
    }

    $scope.launchWizard = function (e) {
        if(e != undefined) {
            e.preventDefault();
        }
        showConfigWizardModal();
    }

    var launchWizard = $routeParams.launchWizard;
    if(launchWizard != undefined && launchWizard == true){
        $scope.launchWizard();
    }

    /****************************************************************************************
     ***** Group of convenient methods to display a Json form
     *****************************************************************************************/
    $scope.bindData = function(){
        $scope.ul_li_Data = '';
        var jsonFormData = JSON.parse($scope.config.configJson);
        if (jsonFormData != undefined) {
            $scope.to_ul(jsonFormData,'treeul');
            $scope.ul_li_Data = '';
        }
    };

    $scope.to_ul = function(branches, htmlElement) {
        $.each(branches, function(key,value) {
            if (":" + value == ':[object Object]' || angular.isArray(value)) {
                $scope.ul_li_Data = $scope.ul_li_Data + "<li><span><i class = 'icon-minus-sign'></i>&nbsp;"
                    + (isFinite(key)? "Entry:" + (parseInt(key, 10) + 1) : key);
                $scope.ul_li_Data = $scope.ul_li_Data + "<ul>";
                $scope.to_ul(value, htmlElement);
                $scope.ul_li_Data = $scope.ul_li_Data + "</ul>";
            } else{
                $scope.ul_li_Data = $scope.ul_li_Data + "<li><span>" + key;
                $scope.ul_li_Data = $scope.ul_li_Data + " : <b>" + value + "</b></span></li>";
            }
        });
        $('#'+htmlElement).empty().append($scope.ul_li_Data);
    };

    $scope.setEvent = function () {
        $('.icon-plus-sign, .icon-minus-sign').click(function () {
            $(this).parent().parent().find("ul").toggle();
            if ($(this).hasClass('icon-plus-sign')) {
                $(this).removeClass("icon-plus-sign").addClass("icon-minus-sign");
            } else if ($(this).hasClass('icon-minus-sign')) {
                $(this).removeClass("icon-minus-sign").addClass("icon-plus-sign");
            }
        });
    };

    $configs.checkViewLocationPrivilege().
    then(function (response) {
        var serverData = response.data;
        $scope.isViewLocationPrivilegeGranted = serverData;
    });

    $configs.checkManageProviderPrivilege().
    then(function (response) {
        var serverData = response.data;
        $scope.isManageProviderPrivilegeGranted = serverData;
    });

    $configs.checkManageFormsPrivilege().
    then(function (response) {
        var serverData = response.data;
        $scope.isManageFormsPrivilegeGranted = serverData;
    });

    $configs.checkManageProviderPrivilege().
    then(function (response) {
        var serverData = response.data;
        $scope.isAddCohortsPrivilegeGranted = serverData;
    });

    $scope.saveLocation = function (location) {
        $configs.saveLocation(location.name, location.description).
        then(function () {
            //Add location to setupconfig and close the modal
        })
    };

    $scope.$watch('search.person', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $configs.searchConfigPersons($scope.search.person).
            then(function (response) {
                $scope.person = response.data.results;
            });
        }
    }, true);

    $scope.saveProvider = function (provider) {
        $configs.saveProvider(provider.person_id, provider.name, provider.identifier).
        then(function () {
            //Add provider to setupconfig and close the modal
        })
    };
}

function ConfigsCtrl($scope, $configs) {
    $scope.showConfigWizard = false;
    // initialize the paging structure
    $scope.maxSize = 10;
    $scope.pageSize = 10;
    $scope.currentPage = 1;
    $scope.totalItems = 0;
    $configs.getConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
    then(function (response) {
        var serverData = response.data;
        $scope.configs = serverData.objects;
        $scope.noOfPages = serverData.pages;
        $scope.totalItems = serverData.totalItems;
        $('#wait').hide();
    });

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $configs.getConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.configs = serverData.objects;
                $scope.noOfPages = serverData.pages;
                $scope.totalItems = serverData.totalItems;
            });
        }
    }, true);

    $scope.$watch('search', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $scope.currentPage = 1;
            $configs.getConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.configs = serverData.objects;
                $scope.noOfPages = serverData.pages;
                $scope.totalItems = serverData.totalItems;
            });
        }
    }, true);

    $scope.launchWizard = function (e) {
        $scope.showConfigWizard = !$scope.showConfigWizard;
        e.preventDefault();
    }
}

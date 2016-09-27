function ConfigCtrl($scope, $routeParams, $location, $configs) {

    // initialize control objects
    $scope.search = {forms: '', cohorts: '', locations: '', providers: '', concepts: ''};
    $scope.selected = {forms: '', cohorts: '', locations: '', providers: '', concepts: [], eConcepts: []};
    $scope.specialFields = {showConfigJson: '', stillLoading: true};

    // initialize the config objects
    $scope.config = {};
    $scope.configForms = [];
    $scope.configCohorts = [];
    $scope.configLocations = [];
    $scope.configProviders = [];
    $scope.extractedConcepts = [];
    $scope.extractedNotUsedConcepts = [];
    $scope.configConcepts = [];

    // initialize the view to be read only
    $scope.mode = "view";
    $scope.uuid = $routeParams.uuid;
    if ($scope.uuid === undefined) {
        $scope.mode = "edit";
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
            }
        }).then(function () {
            $scope.bindData();
            $scope.setEvent();
        });
    }

    $configs.searchConfigForms().then(function (response) {
        var forms = response.data.objects;
        angular.forEach(forms, function (form) {
            if (form.metaJson != undefined && form.metaJson != null) {
                var metaJson = JSON.parse(form.metaJson);
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

    $scope.save = function (config) {
        $configs.saveConfiguration(config.uuid, config.name, config.description, createJson(config)).
        then(function () {
            $location.path("/configs");
        })
    };

    var createJson = function (config) {
        var configJsonString = {"config":{}};
        configJsonString.config["name"] = config.name;
        configJsonString.config["description"] = config.description;
        configJsonString.config["forms"] = $scope.configForms;
        configJsonString.config["cohorts"] = $scope.configCohorts;
        configJsonString.config["locations"] = $scope.configLocations;
        configJsonString.config["providers"] = $scope.configProviders;
        configJsonString.config["concepts"] = $scope.configConcepts;
        return angular.toJson(configJsonString);
    };

    $scope.delete = function () {
        $configs.deleteConfiguration($scope.uuid).
        then(function () {
            $location.path("/configs");
        })
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
            $configs.searchConfigForms($scope.search.forms).
            then(function (response) {
                $scope.forms = response.data.objects;
            });
        }
    }, true);

    $scope.addForm = function(form) {
        var formExists = _.find($scope.configForms, function (configForm) {
            return configForm.uuid == form.uuid
        });
        if (!formExists) {
            $scope.configForms.push(form);
            $scope.search.forms = '';
        }
    };

    $scope.chosenForm = function (value) {
        $scope.selected.form = value;
    };

    $scope.removeForm = function () {
        angular.forEach($scope.configForms, function (configForm, index) {
            if (configForm.uuid === $scope.selected.form) {
                $scope.configForms.splice(index, 1);
                $scope.selected.form = '';
            }
        });
    };

    /****************************************************************************************
     ***** Group of methods to manipulate Cohorts
     *****************************************************************************************/

    $scope.$watch('search.cohorts', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $configs.searchConfigCohorts($scope.search.cohorts).
            then(function (response) {
                $scope.cohorts = response.data.objects;
            });
        }
    }, true);

    $scope.addCohort = function(cohort) {
        var cohortExists = _.find($scope.configCohorts, function (configCohort) {
            return configCohort.uuid == cohort.uuid
        });
        if (!cohortExists) {
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

    $scope.addLocation = function(location) {
        var locationExists = _.find($scope.configLocations, function (configLocation) {
            return configLocation.uuid == location.uuid
        });
        if (!locationExists) {
            $scope.configLocations.push(location);
            $scope.search.locations = '';
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
}

function ConfigsCtrl($scope, $configs) {
    // initialize the paging structure
    $scope.pageSize = 10;
    $scope.currentPage = 1;
    $configs.getConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
    then(function (response) {
        var serverData = response.data;
        $scope.configs = serverData.objects;
        $scope.noOfPages = serverData.pages;
    });

    $scope.$watch('currentPage', function (newValue, oldValue) {
        if (newValue != oldValue) {
            $configs.getConfigurations($scope.search, $scope.currentPage, $scope.pageSize).
            then(function (response) {
                var serverData = response.data;
                $scope.configs = serverData.objects;
                $scope.noOfPages = serverData.pages;
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
            });
        }
    }, true);
}
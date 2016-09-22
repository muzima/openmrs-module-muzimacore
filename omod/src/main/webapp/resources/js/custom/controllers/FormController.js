'use strict';
function FormsCtrl($location, $scope, $window, FormService, TagService, _) {
    $scope.init = function () {
        $scope.editMode = false;
        $scope.tagColorMap = {};
        $scope.activeTagFilters = [];
        $scope.xformToUpload = "";
        $scope.htmlFormToUpload = "";
        $scope.fetchingForms = false;

        getTags().then(setTags);
        getForms().then(setForms);
    };

    var getTags = function () {
        return TagService.all();
    };

    var getForms = function () {
        $scope.fetchingForms = true;
        return FormService.all();
    };

    var setTags = function (result) {
        $scope.tags = result.data.results;
    };

    var setForms = function (result) {
        $scope.fetchingForms = false;
        $scope.forms = result.data.results;
        $scope.muzimaforms = _.map(result.data.results, function (form) {

            return {
                form: form,
                newTag: "",
                retired: false,
                retireReason: ''
            };
        });
    };

    $scope.toggleRetireForm = function(form){
        form.retired = !form.retired;
    };

    $scope.hasForms = function () {
        return !_.isEmpty($scope.muzimaforms);
    };

    $scope.showFormPreview = function (formHTML, formModel, formJSON) {
        var previewWindow = $window.open("../../moduleResources/muzimacore/preview/enketo/template.html");

        previewWindow.formHTML = formHTML;
        previewWindow.formModel = formModel;
        previewWindow.formJSON = formJSON;
    };

    $scope.editForm = function(muzimaform){
        $location.path('/update/xforms/' + muzimaform.form.uuid);
    };

    var tagColor = function (tagId) {
        var tag = $scope.tagColorMap[tagId];
        if (!tag) {
            $scope.tagColorMap[tagId] = {};
            $scope.tagColorMap[tagId].color =
                'rgb(' + (50 + Math.floor(Math.random() * 150))
                + ',' + (50 + Math.floor(Math.random() * 150))
                + ',' + (50 + Math.floor(Math.random() * 150)) + ')';
        }
        return $scope.tagColorMap[tagId].color;
    };

    $scope.tagStyle = function (tagId) {
        return  {'background-color': tagColor(tagId)};
    };

    $scope.tagNames = function () {
        var tagNames = [];
        angular.forEach($scope.tags, function (tag) {
            tagNames.push(tag.name);
        });
        return tagNames;
    };

    var caseInsensitiveFind = function (tags, newTag) {
        return _.find(tags, function (tag) {
            return angular.lowercase(tag.name) === angular.lowercase(newTag);
        });
    };

    $scope.remove = function (muzimaform) {
        var form = muzimaform.form;
        FormService.retire(muzimaform.form, muzimaform.retireReason || '')
            .then(function () {
                for (var i = $scope.muzimaforms.length - 1; i >= 0; i--) {
                    var muzimaform = $scope.muzimaforms[i].form;
                    if (muzimaform.id == form.id) {
                        $scope.muzimaforms.splice(i, 1);
                    }
                }
            });
    };

    $scope.saveTag = function (muzimaform) {
        if (muzimaform.newTag === "") return;
        var form = muzimaform.form;
        var newTag = muzimaform.newTag;
        var tagToBeAdded = caseInsensitiveFind($scope.tags, newTag) || {"name": newTag};

        muzimaform.newTag = "";
        if (caseInsensitiveFind(form.tags, tagToBeAdded.name)) return;
        form.tags.push(tagToBeAdded);
        FormService.save(form)
            .then(function (result) {
                return FormService.get(form.uuid);
            })
            .then(function (savedForm) {
                angular.extend(form, savedForm.data);
                if (!tagToBeAdded.id)
                    getTags().then(setTags);
            });

    };

    $scope.removeTag = function (form, tagToRemove) {
        angular.forEach(form.tags, function (tag, index) {
            if (tag.name === tagToRemove.name) {
                form.tags.splice(index, 1);
                FormService.save(form)
                    .then(function (result) {
                        return FormService.get(form.uuid);
                    })
                    .then(function (savedForm) {
                        angular.extend(form, savedForm.data);
                    });
            }
        });
    };

    $scope.tagFilterActive = function () {
        return !_.isEmpty($scope.activeTagFilters);
    };

    $scope.removeTagFilter = function (tag) {
        $scope.activeTagFilters = _.without($scope.activeTagFilters, tag);
    };

    $scope.addTagFilter = function (tagToAdd) {
        var tag = _.find($scope.tags, function (tag) {
            return tag.id === tagToAdd.id;
        });
        $scope.activeTagFilters = _.union($scope.activeTagFilters, [tag]);
    };
}
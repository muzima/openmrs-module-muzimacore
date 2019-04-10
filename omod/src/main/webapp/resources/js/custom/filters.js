'use strict';

/* Filters */

angular.module('muzimafilters', []).filter('tagFilter', function () {
    return function (muzimaforms, tagList) {
        if (tagList.length == 0) {
            return muzimaforms;
        }
        var result = [];
        angular.forEach(muzimaforms, function (muzimaform) {
            angular.forEach(muzimaform.form.tags, function (tag) {
                angular.forEach(tagList, function (activeTag) {
                    if (tag.name === activeTag.name) {
                        var index = result.indexOf(muzimaform);
                        if (index < 0) {
                            result.push(muzimaform);
                        }
                    }
                });
            });
        });
        return result;
    };
});

angular.module('filters', []).filter('truncate', function () {
    return function (text, length, end) {
        if (isNaN(length))
            length = 10;

        if (end === undefined)
            end = "...";

        if (text.length <= length || text.length - end.length <= length) {
            return text;
        }
        else {
            return String(text).substring(0, length - end.length) + end;
        }

    };
});

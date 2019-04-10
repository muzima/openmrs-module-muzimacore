'use strict';

describe('filter', function () {

    beforeEach(module('muzimafilters'));

    describe('tagFilter', function () {
        var muzimaforms = [
            {form: {"id": 1, "name": "Patient Registration Form", "description": "Form for registering patients", "selected": false, "tags": [
                {"id": 1, "name": "Registration"},
                {"id": 2, "name": "Patient"}
            ]  }, newTag: ""},
            {form: {"id": 2, "name": "PMTCT Ante-Natal Care Form", "description": "", "selected": false, "tags": [
                {"id": 1, "name": "Registration"}
            ] }, newTag: ""},
            {form: {"id": 3, "name": "AMPATH Form", "description": "", "selected": false, "tags": [
                {"id": 1, "name": "Encounter"}
            ] }, newTag: ""}
        ];


        it('should return form only if it have a tag corresponding to the active tag list',
            inject(function (tagFilterFilter) {
                var activeTagFilters = [
                    {"id": 1, "name": "Registration"},
                    {"id": 2, "name": "Patient"}
                ];
                var result = tagFilterFilter(muzimaforms, activeTagFilters);
                expect(result.length).toBe(2);
                expect(result[0]).toBe(muzimaforms[0]);
                expect(result[1]).toBe(muzimaforms[1]);
            }));

        it('should return all forms if aciveTagList is empty',
            inject(function (tagFilterFilter) {
                var activeTagFilters = [];
                var result = tagFilterFilter(muzimaforms, activeTagFilters);
                expect(result.length).toBe(3);
                expect(result[0]).toBe(muzimaforms[0]);
                expect(result[1]).toBe(muzimaforms[1]);
                expect(result[2]).toBe(muzimaforms[2]);
            }));
    });
});
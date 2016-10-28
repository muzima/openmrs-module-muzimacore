describe('Html5Forms services', function () {

    beforeEach(module('muzimaCoreModule'));

    describe('FormService', function () {
        var httpBackend, service;

        var setGetAllExpectation = function () {
            httpBackend.expectGET("../../ws/rest/v1/muzima/form").
                respond({ results: [
                    {"id": 1, "name": "Patient Registration Form", "description": "Form for registering patients", "selected": false, "tags": [
                        {"id": 1, "name": "Registration"},
                        {"id": 2, "name": "Patient"}
                    ]  },
                    {"id": 2, "name": "PMTCT Ante-Natal Care Form", "description": "", "selected": false, "tags": [
                        {"id": 1, "name": "Registration"}
                    ] }
                ]});
        };

        beforeEach(inject(function (_$httpBackend_, FormService) {
            httpBackend = _$httpBackend_;
            service = FormService;

        }));

        it("should get all forms", function () {
            setGetAllExpectation();
            service.all().then(function (result) {
                var forms = result.data.results;
                expect(forms.length).toBe(2);
            });
            httpBackend.flush();
        });


    });

    describe('FormService', function () {
        var httpBackend, service;
        var setGetOneExpectation = function () {
            httpBackend.expectGET("../../ws/rest/v1/muzima/form/foo?v=custom:(id,uuid,name,modelXml,modelJson,html,tags)").
                respond(
                {"id": 1,"uuid":"foo" ,"name": "Patient Registration Form", "description": "Form for registering patients", "selected": false,
                    "tags": [
                        {"id": 1, "name": "Registration"},
                        {"id": 2, "name": "Patient"}
                    ]
                });
        };
        beforeEach(inject(function (_$httpBackend_, FormService) {
            httpBackend = _$httpBackend_;
            service = FormService;

        }));

        it("should get one form", function () {
            setGetOneExpectation();
            service.get("foo").then(function (result) {
                var form = result.data;
                expect(form.id).toBe(1);
                expect(form.uuid).toBe("foo");
                expect(form.name).toBe("Patient Registration Form");
            });
            httpBackend.flush();
        });

    });


    describe('TagService', function () {
        var httpBackend, service;
        beforeEach(inject(function (_$httpBackend_, TagService) {
            httpBackend = _$httpBackend_;
            service = TagService;
            httpBackend.expectGET("../../ws/rest/v1/muzima/tag").
                respond({results:[
                    {"id": 1, "name": "Registration"},
                    {"id": 2, "name": "Patient"},
                    {"id": 3, "name": "PMTCT"}
                ]});
        }));

        it("should get all tags", function () {
            service.all().then(function (result) {
                var forms = result.data.results;
                expect(forms.length).toBe(3);
            });
            httpBackend.flush();
        });

    });

    describe('XFormsService', function () {
        var httpBackend, service;
        beforeEach(inject(function (_$httpBackend_, XFormService) {
            httpBackend = _$httpBackend_;
            service = XFormService;
            httpBackend.expectGET("xforms.form").
                respond([
                    {"id": 1, "name": "Patient Registration Form", "description": "Form for registering patients", "selected": false},
                    {"id": 2, "name": "PMTCT Ante-Natal Care Form", "description": "", "selected": false},
                    {"id": 3, "name": "Outreach Adult Locator Form", "description": "", "selected": false}
                ]);
        }));

        it("should get all xforms", function () {
            service.all().then(function (result) {
                var forms = result.data;
                expect(forms.length).toBe(3);
            });
            httpBackend.flush();
        });

    });
});

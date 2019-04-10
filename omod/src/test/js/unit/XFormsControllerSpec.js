describe('muzimaForms controllers', function () {
    beforeEach(module('muzimaCoreModule'));
    describe('XFormsCtrl', function () {
        var XFormService = {};
        XFormService.all = function () {
            return {then: function (callback) {
                callback({data: [
                    {"id": 1, "name": "Patient Registration Form", "description": "Form for registering patients"},
                    {"id": 2, "name": "PMTCT Ante-Natal Care Form", "description": ""},
                    {"id": 3, "name": "Outreach Adult Locator Form", "description": ""}
                ]})
            }};
        }

        XFormService.save = function () {
        };


        var window = {
            open: function () {
                return null;
            }
        };

        beforeEach(inject(function ($rootScope, $controller) {
            scope = $rootScope.$new();
            ctrl = $controller(XFormsCtrl, {
                $scope: scope,
                $window: window,
                XFormService: XFormService
            });
            scope.init();
        }));

        it('should be false if there are no xForms', function () {
            scope.xForms = [];
            expect(scope.hasXForms()).toBe(false);
        });

        it('should be true if there are XForms', function () {
            scope.xForms = [
                {id: 1}
            ];
            expect(scope.hasXForms()).toBe(true);
        });

        it('should fetch Xforms', function () {
            scope.fetch();
        });

        it('should post selected xform ids when clicked on done', function () {
            spyOn(XFormService, "save").andReturn({
                    then: function () {
                    }}
            );
            scope.selectXForm('1');
            scope.selectXForm('2');

            scope.done();
            scope.$apply();

            expect(XFormService.save).toHaveBeenCalledWith({"id": 1, "name": "Patient Registration Form", "description": "Form for registering patients"});
            expect(XFormService.save).toHaveBeenCalledWith({"id": 2, "name": "PMTCT Ante-Natal Care Form", "description": ""});
        });

    });
})
;

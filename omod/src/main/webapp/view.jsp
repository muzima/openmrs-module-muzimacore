<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="Edit Patients" otherwise="/login.htm" redirect="/module/muzimacore/view.list"/>

<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/font-awesome-4.7.0/css/font-awesome.min.css"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/animate/animate.css"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/bootstrap-4.5.3/css/bootstrap.css"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/custom/custom.css"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/custom/sidebar.css"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/angular-tour/angular-tour.css"/>

<openmrs:htmlInclude file="/moduleResources/muzimacore/js/jquery/jquery.js" />
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/lodash/lodash.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/bootstrap-4.5.3/js/bootstrap.min.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/angular/angular.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/angular/angular-route.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/angular/angular-resource.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/angular/angular-sanitize.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/ui-bootstrap/ui-bootstrap-3.0.6.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/ui-bootstrap/ui-bootstrap-tpls-3.0.6.min.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/angular/angular-strap.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/filters.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/app.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/angular/angular-tour.min.js" />
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/EditController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/ErrorController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/FormController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/ImportController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/QueueController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/RegistrationController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/SourceController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/UpdateController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/XFormsController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/ConfigController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/SettingController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/ReportConfigurationController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/CohortDefinitionController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/MergeController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/DashboardController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/directives/fileUpload.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/directives/sideNav.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/directives/configWizard.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/directives/mUzimaFormUpload.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/directives/configWizardSideNav.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/directives/openmrsFormCreator.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/directives/cohortDefinitionCreator.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/directives/locationCreator.js"/>

<div class="bootstrap-scope" ng-app="muzimaCoreModule">
    <div ng-view ></div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
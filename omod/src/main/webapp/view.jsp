<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="Edit Patients" otherwise="/login.htm" redirect="/module/muzimacore/view.list"/>

<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/font-awesome/css/font-awesome.min.css"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/animate/animate.css"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/bootstrap/css/bootstrap.css"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/custom/custom.css"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/alert/alert.css"/>

<openmrs:htmlInclude file="/moduleResources/muzimacore/js/alert/alert.js" />
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/jquery/jquery.js" />
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/lodash/lodash.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/styles/bootstrap/js/bootstrap.min.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/angular/angular.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/angular/angular-route.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/angular/angular-resource.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/angular/angular-sanitize.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/ui-bootstrap/ui-bootstrap-2.0.0.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/ui-bootstrap/ui-bootstrap-tpls-2.0.0.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/angular/angular-strap.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/filters.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/app.js"/>
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
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/HtmlFormEntryController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/controllers/MergeController.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/directives/fileUpload.js"/>
<openmrs:htmlInclude file="/moduleResources/muzimacore/js/custom/directives/sideNav.js"/>

<h3><spring:message code="muzimacore.title"/></h3>
<div class="bootstrap-scope" ng-app="muzimaCoreModule">
    <div ng-view ></div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>
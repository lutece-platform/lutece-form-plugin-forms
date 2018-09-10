![](http://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=form-plugin-forms-deploy)
# Plugin forms

## Introduction

This plugin offers a complete and flexible form management within a Lutece site. User interface : a forms display engine in front-office pages that manage the filling and saving process of the responsesAgent interface : a back-office "multiview" feature to search and display the form responses, and process workflow actions on it. Administrator interface : a back-office feature to design and publish the forms, including steps, transitions, controls, questions and groups of questions.

Plugin-forms is based on the features of the former plugin-form, and adds the followings improvement :
 
* forms composed of steps, with powerful transition management between steps,
* draft-mode : the user can partially fill out a form, save it, and complete later the full form submit,
* tree-based design of forms with possibility of groups inside groups,
* multiview feature: a configurable and workflow-capable feature to manage form responses
* improved management of conditional display of groups/questions,
* improved validation control management,
* architecture open to custom entry types


## Configuration

 **Workflow** 

To enable workflow-base features ( response state monitoring, processing of workflow actions on respones, display of action history, ...), the plugin-workflow must be added in the Lutece site and enabled.

 **Context configuration** 

To configure the Multi-view features for responses list-mode display ( search filters, tabs, displayed columns, ...), the corresponding java beans must be declared in the forms_context.xml file `/plugin-forms/webapp/WEB-INF/conf/plugins/forms_context.xml` before starting the Lutece site. The plugin is available with a default configuration to display an example column. The workflow-related objects are commented. If plugin-workflow is present and enabled, those beans should be uncommented to display the workflow state of the form responses.For a complete description of the multi-view implementation and the configuration of the different objects (panels, panel initializers, filters, columns, cells, ...) in context file, please refer to the [module-directory-multiview documentation](http://dev.lutece.paris.fr/plugins/module-directory-multiview/) .

## Design

 **Form questions/groups display model** 

Implementation of form steps is based on composite pattern. A form is composed of one or several steps. Each step is modeled as a tree, with child groups and/or questions. Questions are the leaves of the tree. A Form_Display object is used to describe and store the tree elements.![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-display-model.png)

A Question object is linked to one Generic-attribute entry, therefore the [plugin-genericattributes](http://dev.lutece.paris.fr/plugins/plugin-genericattributes/) is used to hold the EntryType, the Entry Fields and Responses data.When rendering the Step display page, the composite tree structure is built in a recursive way.![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-composite-architecture.png)

 **Display Services** 

The `fr.paris.lutece.plugins.forms.service.EntryServiceManager` class triggers the corresponding process for Question display on the page or response data saving.The implementation depends on entry Type. Cf. `fr.paris.lutece.plugins.forms.web.EntryTypeDefaultDisplayService` for display processing.![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-dataAndDisplayServices-architecture.png)

Example of a text type Question display:![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-textTypeDisplay-architecture.png)

To implement a new custom EntryType, it must be declared in the context with corresponding Display implementation class of the interface `fr.paris.lutece.plugins.forms.web.IEntryDisplayService` .

 `<!-- DisplayService --><bean id="forms.entryTypeCheckBoxDisplayService" class="fr.paris.lutece.plugins.forms.web.EntryTypeDefaultDisplayService"><constructor-arg name="type="java.lang.String" value="forms.entryTypeCheckBox" /></bean>` 

 **Data Services** 

The implementation depends on entry Type. Cf. `fr.paris.lutece.plugins.forms.web.EntryTypeDefaultDataService` for data management.![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-dataServices-architecture.png)

Example of a text type Question display:![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-textTypeData-architecture.png)

To implement a new custom EntryType, it must be declared in the context with corresponding Data implementation class of the interface `fr.paris.lutece.plugins.forms.web.IEntryDataService` .

 `<!-- DataService --><bean id="forms.entryTypeFileDataService" class="fr.paris.lutece.plugins.forms.web.EntryTypeDefaultDataService"><constructor-arg name="type="java.lang.String" value="forms.entryTypeFile" /></bean>` 

 **Controls** 

The Controls are used as generic objects for Transition validation, Question field response validation , or Conditional display control. Implementation is depending on Question Type. Custom validator types can be added by implemented the interface `fr.paris.lutece.plugins.forms.validation.IValidator` .![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-controls-model.png)

## Usage

After enabling the plugin-forms in Lutece site, there are 3 ways to use it.

 **Front-office :** the published forms can be accessed with the XPage url */jsp/site/Portal.jsp?page=forms&id_form=<form identifier>* 

![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-step-filling-FO.png)

 **Agent interface :** the back-office "multiview" feature to search and display the form responses is available with menu "Viewing of the responses of the forms".

![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-response-list-admin.png)

![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-response-detail-admin.png)

 **Administrator interface :** the back-office feature to design and publish the forms, including steps, transitions, controls, questions and groups of questions is available with menu "Manage forms".

Form modification:![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-form-edition-admin.png)

Step modification:![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-step-edition-admin.png)

Iteration:![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-question-iteration-admin.png)

The plugin-forms let you iterate over a group of questions. This is possible when a group only contains questions (and not other groups). To configure the group iteration you have to navigate into the modify group page. This page let you define a minimum and a maximum number of iterations.The minimum number define the number of iteration that first will be shown to the user (these are not deletable). For the maximum, 0 means limitless.

![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-question-iteration-FO.png)

Draft:![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-form-draft-admin.png)

If the form is configure with the authentication mode enabled, the user would be able to save his progress on any step of the form. His answers and breadcrumb will be save in database, and when the user will reconnect, this save will be load.

![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-form-draft-FO.png)

Control modification for a step transition:![](http://dev.lutece.paris.fr/plugins/plugin-forms/images/Forms-transition-edition-admin.png)


[Maven documentation and reports](http://dev.lutece.paris.fr/plugins/plugin-forms/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*
--liquibase formatted sql
--changeset forms:update_db_forms-3.0.0-3.1.0.sql
--preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS forms_list_portlet;
CREATE TABLE forms_list_portlet (
	id_portlet int NOT NULL,
	id_form int default 0 NOT NULL,
	PRIMARY KEY (id_portlet,id_form)
);

DELETE FROM core_portlet_type where id_portlet_type = 'FORMS_LIST_PORTLET';
INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form,icon_name) VALUES ('FORMS_LIST_PORTLET','forms.portlet.formsList.name','plugins/forms/CreateFormsListPortlet.jsp','plugins/forms/ModifyFormsListPortlet.jsp','fr.paris.lutece.plugins.forms.business.portlet.FormsListPortletHome','forms','plugins/forms/DoCreateFormsListPortlet.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/forms/portlet/create_portlet_formslist.html','','plugins/forms/DoModifyFormsListPortlet.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/forms/portlet/modify_portlet_formslist.html','','list-details');

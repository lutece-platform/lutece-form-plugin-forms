-- liquibase formatted sql
-- changeset forms:init_core_forms.sql
-- preconditions onFail:MARK_RAN onError:WARN

--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'FORMS_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('FORMS_MANAGEMENT','forms.adminFeature.manageForms.name',1,'jsp/admin/plugins/forms/ManageForms.jsp','forms.adminFeature.manageForms.description',0,'forms',NULL,'ti ti-forms',NULL,4);
DELETE FROM core_admin_right WHERE id_right = 'FORMS_MULTIVIEW';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('FORMS_MULTIVIEW','forms.adminFeature.multiviewForms.name',2,'jsp/admin/plugins/forms/MultiviewForms.jsp','forms.adminFeature.multiviewForms.description',0,'forms',NULL,'ti ti-list-check',NULL,5);
DELETE FROM core_admin_right WHERE id_right = 'FORMS_CATEGORIES';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('FORMS_CATEGORIES','forms.adminFeature.categoriesForms.name',1,'jsp/admin/plugins/forms/ManageFormsCategories.jsp','forms.adminFeature.categoriesForms.description',0,'forms',NULL,'ti ti-bookmarks',NULL,6);
DELETE FROM core_admin_right WHERE id_right = 'FORMS_SEARCH_INDEXATION';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES
('FORMS_SEARCH_INDEXATION','forms.adminFeature.manageSearchIndexation.name',1,'jsp/admin/plugins/forms/ManageFormsSearchIndexation.jsp','forms.adminFeature.manageSearchIndexation.description',0,'forms',NULL,'ti ti-settings-search',NULL,7);



--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'FORMS_MANAGEMENT';
DELETE FROM core_user_right WHERE id_right = 'FORMS_MULTIVIEW';

--
-- Dumping data for table core_admin_role
--
DELETE FROM core_admin_role WHERE role_key = 'forms_manager';
INSERT INTO core_admin_role (role_key,role_description) VALUES ('forms_manager','FORMS - Gestion des formulaires');
INSERT INTO core_admin_role (role_key,role_description) VALUES ('forms_multiview','FORMS - Visualisation des résultats des formulaires');

--
-- Dumping data for table core_user_role
--
INSERT INTO core_user_role (role_key,id_user) VALUES ('forms_manager',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('forms_multiview',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('forms_manager',2);

--
-- Dumping data for table core_admin_role_resource
--
DELETE FROM core_admin_role_resource WHERE role_key = 'forms_manager';
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('forms_manager','FORMS_FORM','*','*');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('forms_multiview','FORM_PANEL_CONF','*','*');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('forms_manager','GLOBAL_FORMS_ACTION','','');
--
-- Init  table core_dashboard
--
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('FORMS', 3, 2);

--
-- Dumping data for table core_portlet_type
--
DELETE FROM core_portlet_type where id_portlet_type = 'FORMS_LIST_PORTLET';
INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form,icon_name) VALUES ('FORMS_LIST_PORTLET','forms.portlet.formsList.name','plugins/forms/CreateFormsListPortlet.jsp','plugins/forms/ModifyFormsListPortlet.jsp','fr.paris.lutece.plugins.forms.business.portlet.FormsListPortletHome','forms','plugins/forms/DoCreateFormsListPortlet.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/forms/portlet/create_portlet_formslist.html','','plugins/forms/DoModifyFormsListPortlet.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/forms/portlet/modify_portlet_formslist.html','','list-details');

-- changeset forms:init_core_forms.sql-rev2
-- preconditions onFail:MARK_RAN onError:WARN
-- The admin user of the core (id 1) gets the forms features, and the super_admin role of the core (held by admin)
-- every permission on every forms resource type
DELETE FROM core_user_right WHERE id_user = 1 AND id_right IN ('FORMS_MANAGEMENT','FORMS_MULTIVIEW','FORMS_CATEGORIES','FORMS_SEARCH_INDEXATION');
INSERT INTO core_user_right (id_right,id_user) VALUES ('FORMS_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('FORMS_MULTIVIEW',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('FORMS_CATEGORIES',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('FORMS_SEARCH_INDEXATION',1);
DELETE FROM core_admin_role_resource WHERE role_key = 'super_admin' AND resource_type IN ('FORMS_FORM','FORM_PANEL_CONF','GLOBAL_FORMS_ACTION');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('super_admin','FORMS_FORM','*','*');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('super_admin','FORM_PANEL_CONF','*','*');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('super_admin','GLOBAL_FORMS_ACTION','*','*');

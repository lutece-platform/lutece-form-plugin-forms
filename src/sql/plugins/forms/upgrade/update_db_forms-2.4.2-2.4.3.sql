ALTER TABLE forms_response ADD role varchar(50) default NULL;
ALTER TABLE forms_form ADD access_to_responses_by_role SMALLINT default 0;
ALTER TABLE forms_response ADD admin varchar(50) default NULL;
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('FORM_RESPONSE_MANAGEMENT','forms.adminFeature.manageFormResponse.name',1,'jsp/admin/plugins/forms/ManageFormResponse.jsp','forms.adminFeature.manageFormResponse.description',0,'forms',NULL,NULL,NULL,4);
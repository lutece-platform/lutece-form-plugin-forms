-- liquibase formatted sql
-- changeset forms:update_db_forms-3.1.1-3.1.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE forms_action SET action_url = 'jsp/admin/plugins/forms/ManageForms.jsp?view=modifyForm', icon_url='cog' WHERE name_key = 'forms.action.params.name';
UPDATE forms_action SET action_url = 'jsp/admin/plugins/forms/ManageSteps.jsp?view=manageSteps', icon_url='step-forward' WHERE name_key = 'forms.action.modify.name';

ALTER TABLE forms_message ADD label_end_message_button VARCHAR(255) default NULL;

DROP TABLE IF EXISTS forms_lucene_lock;
CREATE TABLE IF NOT EXISTS forms_lucene_lock (
  index_name varchar(50),
  instance_name varchar(50),
  is_locked boolean,
  date_begin timestamp,
  expired_date timestamp,
  uuid varchar(50),
  PRIMARY KEY (index_name)
);

INSERT INTO forms_lucene_lock (index_name,instance_name,is_locked,date_begin,expired_date,uuid) VALUES
('forms.lucene.lock',null,false,NULL,NULL,NULL);


DELETE FROM core_admin_right WHERE id_right = 'FORMS_SEARCH_INDEXATION';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES
('FORMS_SEARCH_INDEXATION','forms.adminFeature.manageSearchIndexation.name',1,'jsp/admin/plugins/forms/ManageFormsSearchIndexation.jsp','forms.adminFeature.manageSearchIndexation.description',0,'forms',NULL,'ti ti-settings-search',NULL,7);

ALTER TABLE forms_form modify COLUMN unavailable_message VARCHAR(3000);

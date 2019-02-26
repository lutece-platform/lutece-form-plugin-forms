CREATE INDEX idx_fqer_id_question_response on forms_question_entry_response ( id_question_response );
CREATE INDEX idx_fqr_id_form_response on forms_question_response  ( id_form_response );

/*==============================================================*/
/* Table structure for table forms_indexer_action				*/
/*==============================================================*/
CREATE TABLE forms_indexer_action (
  id_action int AUTO_INCREMENT,
  id_form_response INT DEFAULT 0 NOT NULL,
  id_task INT DEFAULT 0 NOT NULL ,
  PRIMARY KEY (id_action)
);

TRUNCATE forms_action;
REPLACE INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (1,'forms.action.modify.name','forms.action.modify.description','jsp/admin/plugins/forms/ManageSteps.jsp?view=manageSteps','edit','MODIFY',0);
REPLACE INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (3, 'forms.action.params.name', 'forms.action.params.description', 'jsp/admin/plugins/forms/ManageForms.jsp?view=modifyForm', 'cog', 'PARAM', 0);
REPLACE INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (2,'forms.action.delete.name','forms.action.delete.description','jsp/admin/plugins/forms/ManageForms.jsp?view=confirmRemoveForm','trash','DELETE',0);
REPLACE INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (4, 'forms.action.publish.name', 'forms.action.publish.description', 'jsp/admin/plugins/forms/ManageForms.jsp?view=modifyPublication', 'calendar', 'PARAM', 0);
REPLACE INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (5, 'forms.action.viewResponses.name', 'forms.action.viewResponses.description', 'jsp/admin/plugins/forms/MultiviewForms.jsp?current_selected_panel=forms', 'list-alt', 'VIEW_FORM_RESPONSE', 0);
REPLACE INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (6, 'module.forms.documentproducer.actions.extractpdf.name', 'module.forms.documentproducer.actions.extractpdf.description', 'jsp/admin/plugins/forms/modules/documentproducer/ManageConfigProducer.jsp?view=getManageConfigProducer', 'file-pdf-o', 'PDFPROD', 0);

ALTER TABLE forms_question ADD COLUMN is_visible_multiview_global SMALLINT default 0 NOT NULL;
ALTER TABLE forms_question ADD COLUMN is_visible_multiview_form_selected SMALLINT default 0 NOT NULL;
ALTER TABLE forms_question ADD COLUMN column_title varchar(255) default '' NOT NULL;

--
-- Table structure for table forms_global_action
--
DROP TABLE IF EXISTS forms_global_action;
CREATE TABLE forms_global_action (
    id_action int default 0 NOT NULL,
    code varchar(100) default NULL,
    name_key varchar(100) default NULL,
    description_key varchar(100) default NULL,
    action_url varchar(255) default NULL,
    icon_url varchar(255) default NULL,
    PRIMARY KEY (id_action)
);

INSERT INTO forms_global_action (id_action, code, name_key, description_key, action_url, icon_url ) VALUES (1,'multiviewconfig','forms.action.multiviewConfig.labelKey','forms.action.multiviewConfig.descriptionKey','jsp/admin/plugins/forms/MultiviewForms.jsp?view=view_multiview_config','edit');

INSERT INTO core_datastore ( entity_key, entity_value ) VALUES
    ( 'forms.display.form.columnTitle', 'true' );
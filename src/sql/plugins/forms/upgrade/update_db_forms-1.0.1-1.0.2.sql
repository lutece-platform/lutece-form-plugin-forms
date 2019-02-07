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

REPLACE INTO `forms_action` VALUES (1, 'forms.action.modify.name', 'forms.action.modify.description', 'jsp/admin/plugins/forms/ManageSteps.jsp?view=manageSteps', 'pencil', 'MODIFY', 0);
REPLACE INTO `forms_action` VALUES (2, 'forms.action.params.name', 'forms.action.params.description', 'jsp/admin/plugins/forms/ManageForms.jsp?view=modifyForm', 'cog', 'PARAM', 0);
REPLACE INTO `forms_action` VALUES (3, 'forms.action.publish.name', 'forms.action.publish.description', 'jsp/admin/plugins/forms/ManageForms.jsp?view=modifyPublication', 'calendar', 'PUBLISH', 0);
REPLACE INTO `forms_action` VALUES (4, 'forms.action.delete.name', 'forms.action.delete.description', 'jsp/admin/plugins/forms/ManageForms.jsp?view=confirmRemoveForm', 'trash', 'DELETE', 0);
REPLACE INTO `forms_action` VALUES (100, 'module.forms.documentproducer.actions.extractpdf.name', 'module.forms.documentproducer.actions.extractpdf.description', 'jsp/admin/plugins/forms/modules/documentproducer/ManageConfigProducer.jsp?view=getManageConfigProducer', 'file-pdf-o', 'PDFPROD', 0);

ALTER TABLE forms_question ADD COLUMN is_visible_multiview_global SMALLINT default 0 NOT NULL;
ALTER TABLE forms_question ADD COLUMN is_visible_multiview_form_selected SMALLINT default 0 NOT NULL;
ALTER TABLE forms_question ADD COLUMN column_title varchar(255) default '' NOT NULL;

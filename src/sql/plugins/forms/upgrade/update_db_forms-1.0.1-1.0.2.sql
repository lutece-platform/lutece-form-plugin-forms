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
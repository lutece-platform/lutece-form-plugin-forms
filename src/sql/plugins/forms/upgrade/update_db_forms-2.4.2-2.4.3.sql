ALTER TABLE forms_response ADD role varchar(50) default NULL;
ALTER TABLE forms_form ADD access_to_responses_by_role SMALLINT default 0;

alter table genatt_field add column id_file_key VARCHAR(50) default null;

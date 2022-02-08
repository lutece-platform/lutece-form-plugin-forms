ALTER TABLE forms_response ADD status SMALLINT default 0 NOT NULL;
ALTER TABLE forms_response ADD update_date_status timestamp default CURRENT_TIMESTAMP NOT NULL;

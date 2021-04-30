ALTER TABLE forms_form ADD COLUMN captcha_step_initial SMALLINT default 0 NOT NULL;
ALTER TABLE forms_form ADD COLUMN captcha_step_final SMALLINT default 0 NOT NULL;
ALTER TABLE forms_form ADD COLUMN captcha_recap SMALLINT default 0 NOT NULL;

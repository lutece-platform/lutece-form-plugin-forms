ALTER TABLE forms_form ADD COLUMN label_final_button VARCHAR(255);
ALTER TABLE forms_question ADD COLUMN multiview_column_order INT default 0 NOT NULL;

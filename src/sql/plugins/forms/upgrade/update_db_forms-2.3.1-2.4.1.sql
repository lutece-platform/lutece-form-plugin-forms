ALTER TABLE forms_form MODIFY availability_start_date TIMESTAMP NULL;
ALTER TABLE forms_form MODIFY availability_end_date TIMESTAMP NULL;

UPDATE forms_action SET icon_url = 'step-forward' WHERE name_key = 'forms.action.params.name';
UPDATE forms_action SET icon_url = 'cog' WHERE name_key = 'forms.action.modify.name';
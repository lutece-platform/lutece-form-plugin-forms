UPDATE forms_control INNER JOIN forms_display ON forms_control.id_control_target = forms_display.id_display SET forms_control.id_control_target = forms_display.id_composite;

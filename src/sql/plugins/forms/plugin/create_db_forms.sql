
--
-- Structure for table forms_form
--

DROP TABLE IF EXISTS forms_form;
CREATE TABLE forms_form (
id_form int AUTO_INCREMENT,
title varchar(255) default '' NOT NULL,
description varchar(255) default '',
creation_date timestamp default CURRENT_TIMESTAMP NOT NULL,
update_date timestamp default CURRENT_TIMESTAMP NOT NULL,
availability_start_date date default NULL,
availability_end_date date default NULL,
workgroup varchar(255),
id_workflow INT DEFAULT NULL,
authentification_needed SMALLINT,
breadcrumb_name varchar(255) default '' NOT NULL,
display_summary SMALLINT default 0 NOT NULL,
end_message varchar(3000) default '',
return_url varchar(255) default '',
PRIMARY KEY (id_form)
);

--
-- Structure for table forms_step
--

DROP TABLE IF EXISTS forms_step;
CREATE TABLE forms_step (
id_step int AUTO_INCREMENT,
title varchar(255) default '' NOT NULL,
description varchar(255) default '',
id_form int default '0' NOT NULL,
is_initial SMALLINT default 0 NOT NULL,
is_final SMALLINT default 0 NOT NULL,
PRIMARY KEY (id_step)
);

--
-- Structure for table forms_transition
--

DROP TABLE IF EXISTS forms_transition;
CREATE TABLE forms_transition (
id_transition int AUTO_INCREMENT,
from_step int NOT NULL,
next_step int NOT NULL,
id_control int default NULL,
priority int default '0',
PRIMARY KEY (id_transition)
);

--
-- Structure for table forms_question
--

DROP TABLE IF EXISTS forms_question;
CREATE TABLE forms_question (
id_question int AUTO_INCREMENT,
title varchar(255) default '' NOT NULL,
description TEXT default '',
id_entry int default '0',
id_step int default '0',
PRIMARY KEY (id_question)
);

--
-- Structure for table forms_group
--

DROP TABLE IF EXISTS forms_group;
CREATE TABLE forms_group (
id_group int AUTO_INCREMENT,
title varchar(255) default '' NOT NULL,
description varchar(255) default '',
id_step int default '0',
collapsible SMALLINT,
iteration_min int default '1',
iteration_max int default '1',
iteration_label varchar(255) default '',
PRIMARY KEY (id_group)
);



--
-- Table structure for table form_action
--
DROP TABLE IF EXISTS forms_action;
CREATE TABLE forms_action (
    id_action int default 0 NOT NULL,
    name_key varchar(100) default NULL,
    description_key varchar(100) default NULL,
    action_url varchar(255) default NULL,
    icon_url varchar(255) default NULL,
    action_permission varchar(255) default NULL,
    form_state smallint default NULL,
    PRIMARY KEY (id_action)
);

--
-- Structure for table forms_display
--

DROP TABLE IF EXISTS forms_display;
CREATE TABLE forms_display (
id_display int AUTO_INCREMENT,
id_form int default '0',
id_step int default '0',
id_composite int default '0',
id_parent int default '0',
display_order int default '0',
composite_type varchar(255) default '',
display_depth int default '0',
PRIMARY KEY (id_display)
);

--
-- Structure for table forms_control
--

DROP TABLE IF EXISTS forms_control;
CREATE TABLE forms_control (
id_control int AUTO_INCREMENT,
value varchar(255),
error_message varchar(255) default '',
id_question int NOT NULL,
id_display int default '0' NOT NULL,
validator_name varchar(255) NOT NULL,
control_type varchar(255) NOT NULL,
PRIMARY KEY (id_control)
);

DROP TABLE IF EXISTS forms_question_entry_response;
CREATE TABLE forms_question_entry_response (
  id_question_entry_response int AUTO_INCREMENT,
  id_question_response int default '0' NOT NULL,
  id_entry_response int default '0' NOT NULL,
  PRIMARY KEY (id_question_entry_response)
);

DROP TABLE IF EXISTS forms_question_response;
CREATE TABLE IF NOT EXISTS forms_question_response (
  id_question_response int AUTO_INCREMENT,
  id_form_response int default '0' NOT NULL,
  id_question int default '0' NOT NULL,
  id_step int default '0' NOT NULL,
  iteration_number int default '0',
  PRIMARY KEY (id_question_response)
);

DROP TABLE IF EXISTS forms_response;
CREATE TABLE IF NOT EXISTS forms_response (
  id_response int AUTO_INCREMENT,
  id_form int default '0' NOT NULL,
  guid varchar(255),
  creation_date timestamp default CURRENT_TIMESTAMP NOT NULL,
  update_date timestamp default CURRENT_TIMESTAMP NOT NULL,
  from_save SMALLINT default 0,
  PRIMARY KEY (id_response)
);

DROP TABLE IF EXISTS forms_response_step;
CREATE TABLE IF NOT EXISTS forms_response_step (
  id int AUTO_INCREMENT,
  id_form_response int DEFAULT '0' NOT NULL,
  id_step int  DEFAULT '0' NOT NULL,
  order_response int DEFAULT '0' NOT NULL,
  PRIMARY KEY (id)
);

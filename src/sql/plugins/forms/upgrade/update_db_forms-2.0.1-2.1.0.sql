--liquibase formatted sql
--changeset forms:update_db_forms-2.0.1-2.1.0.sql
--preconditions onFail:MARK_RAN onError:WARN
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('FORMS', 3, 2);

INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES (1908,'forms_multiview','FORM_PANEL_CONF','*','*');


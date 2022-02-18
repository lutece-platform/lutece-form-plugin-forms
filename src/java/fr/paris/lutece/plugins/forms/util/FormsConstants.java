/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.forms.util;

import fr.paris.lutece.portal.service.datastore.DatastoreService;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 
 * Constants class for the plugin-form
 *
 */
public final class FormsConstants
{
    // Marks
    public static final String MARK_USER = "user";
    public static final String MARK_FORM = "form";
    public static final String MARK_ID_FORM = "id_form";
    public static final String MARK_STEP = "step";
    public static final String MARK_ID_STEP = "id_step";
    public static final String MARK_COMPOSITE_LIST = "composite_list";
    public static final String MARK_ENTRY_TYPE_REF_LIST = "entry_type_list";
    public static final String MARK_ENTRY = "entry";
    public static final String MARK_ID_ENTRY = "id_entry";
    public static final String MARK_ID_PARENT = "id_parent";
    public static final String MARK_GROUP = "group";
    public static final String MARK_QUESTION = "question";
    public static final String MARK_ID_QUESTION = "id_question";
    public static final String MARK_ID_DISPLAY = "id_display";
    public static final String MARK_FIELD = "field";
    public static final String MARK_LIST_STEPS = "list_steps";
    public static final String MARK_LIST_GROUPS = "list_groups";
    public static final String MARK_LIST_QUESTIONS = "list_questions";
    public static final String MARK_LIST_AVAILABLE_POSITIONS = "list_positions";
    public static final String MARK_DISPLAY = "display";
    public static final String MARK_DISPLAY_TITLE = "display_title";
    public static final String MARK_DISPLAY_ORDER = "displayOrder";
    public static final String MARK_TRANSITION = "transition";
    public static final String MARK_CONTROL = "control";
    public static final String MARK_OTHER_STEP_VALIDATION = "other_step_validation";
    public static final String MARK_CONTROL_TYPE = "control_type";
    public static final String MARK_FORM_TOP_BREADCRUMB = "formTopBreadcrumb";
    public static final String MARK_FORM_BOTTOM_BREADCRUMB = "formBottomBreadcrumb";
    public static final String MARK_QUESTION_CONTENT = "questionContent";
    public static final String MARK_INFO = "messageInfo";
    public static final String MARK_CONTROL_TEMPLATE = "control_template";
    public static final String MARK_CONDITION_TITLE = "modify_condition_title";
    public static final String MARK_QUESTION_LIST_RESPONSES = "list_responses";
    public static final String MARK_VALIDATOR = "validator";
    public static final String MARK_VALIDATOR_MANAGER = "validatorManager";
    public static final String MARK_QUESTION_CREATE_TEMPLATE = "question_create_template";
    public static final String MARK_QUESTION_MODIFY_TEMPLATE = "question_modify_template";
    public static final String MARK_MULTIVIEW_CONFIG = "multiview_config";
    public static final String MARK_FORM_USERASSIGNMENT_ENABLED = "userassignment_enabled";
    public static final String MARK_MULTIVIEW_CONFIG_ACTION = "multiviewConfigAction";
    public static final String MARK_MULTIVIEW_EXPORT_ACTION = "multiviewExportAction";
    public static final String MARK_CAN_BE_FILTERED = "can_be_filtered";
    public static final String MARK_REFERENCE_LIST_SELECT = "ref_list_select";
    public static final String MARK_ANONYMIZATION_HELP = "anonymization_help_message";
    public static final String MARK_BREADCRUMBS = "breadcrumb_template";
    public static final String VALUE_VALIDATOR_LISTEQUESTION_NAME = "forms_listQuestionValidator";
    public static final String MARK_TIMESTAMP = "timestamp";
    public static final String MARK_INACTIVEBYPASSTOKENS = "inactiveBypassTokens";
    public static final String MARK_FORM_RESPONSE = "formResponse";

    // Parameters
    public static final String PARAMETER_PAGE = "page";
    public static final String PARAMETER_ID_FORM = "id_form";
    public static final String PARAMETER_ID_STEP = "id_step";
    public static final String PARAMETER_ID_ENTRY = "id_entry";
    public static final String PARAMETER_ID_FIELD = "id_field";
    public static final String PARAMETER_ID_RESPONSE = "id_response";
    public static final String PARAMETER_ID_FIELD_OCR = "id_field_ocr";
    public static final String PARAMETER_OCR_DOCUMENT = "ocr_document";
    public static final String PARAMETER_TYPE_DOCUMENT_KEY = "type_document_key";
    public static final String PARAMETER_ID_MAPPING = "id_mapping";

    public static final String PARAMETER_TARGET_VIEW = "view";
    public static final String COMPOSITE_STEP_TYPE = "step";
    public static final String COMPOSITE_GROUP_TYPE = "group";
    public static final String COMPOSITE_QUESTION_TYPE = "question";
    public static final String QUESTION_ENTRY_MARKER = "entry";
    public static final String INDENT_COMPOSITE_DISPLAY = "&nbsp;&nbsp;&nbsp;";
    public static final String PARAMETER_ID_COMPOSITE_PARENT = "id_parent";
    public static final String PARAMETER_BUTTON_TYPE_ENTRY = "view_createQuestion";
    public static final String PARAMETER_ID_DISPLAY_PARENT = "id_parent";
    public static final String PARAMETER_ID_ENTRY_TYPE = "id_type";
    public static final String PARAMETER_ID_GROUP = "id_group";
    public static final String PARAMETER_ID_QUESTION = "id_question";
    public static final String PARAMETER_ID_QUESTION_MAPPING = "id_question_mapping";
    public static final String PARAMETER_ID_DISPLAY = "id_display";
    public static final String PARAMETER_ID_PARENT = "id_parent";
    public static final String PARAMETER_ID_TARGET = "id_target";
    public static final String PARAMETER_STEP_VALIDATED = "stepValidated";
    public static final String PARAMETER_GROUP_VALIDATED = "groupValidated";
    public static final String PARAMETER_DISPLAY_ORDER = "displayOrder";
    public static final String PARAMETER_ID_TRANSITION = "id_transition";
    public static final String PARAMETER_ID_CONTROL = "id_control";
    public static final String PARAMETER_VALIDATOR_NAME = "validatorName";
    public static final String PARAMETER_CONTROL_VALUE = "control_value";
    public static final String PARAMETER_CONTROL_TYPE = "control_type";
    public static final String PARAMETER_REF_LIST_VALUE = "refListValue";
    public static final String PARAMETER_REF_LIST_FIELD = "refListField";
    public static final String PARAMETER_REF_LIST_MAPPING = "refListMapping";

    public static final String PARAMETER_ACTION_GO_TO_STEP = "action_doGoToStep";
    public static final String PARAMETER_INFO_KEY = "info_key";
    public static final String PARAMETER_BACK_URL = "back_url";
    public static final String PARAMETER_DISPLAYED_QUESTIONS = "displayed_questions";
    public static final String PARAMETER_INIT = "init";
    public static final String PARAMETER_ID_QUESTION_TO_REMOVE = "id_rm_question";
    public static final String PARAMETER_TIMESTAMP = "ts";
    public static final String PARAMETER_TOKEN_BYPASS = "token_bypass";

    public static final String PARAMETER_SELECTED_PANEL = "selected_panel";
    public static final String PARAMETER_CURRENT_SELECTED_PANEL = "current_selected_panel";
    public static final String PARAMETER_WORKFLOW_ACTION_REDIRECTION = "workflow_action_redirection";
    public static final String PARAMETER_URL_FILTER_PREFIX = "filter_";
    public static final String PARAMETER_SORT_COLUMN_POSITION = "column_position";
    public static final String PARAMETER_SORT_ATTRIBUTE_NAME = "sorted_attribute_name";
    public static final String PARAMETER_SORT_ASC_VALUE = "asc_sort";
    public static final String PARAMETER_ACTION_PREFIX = "action_";
    public static final String PARAMETER_MULTIVIEW_GLOBAL = "is_visible_multiview_global";
    public static final String PARAMETER_MULTIVIEW_FORM_SELECTED = "is_visible_multiview_form_selected";
    public static final String PARAMETER_FILTERABLE_MULTIVIEW_GLOBAL = "is_filterable_multiview_global";
    public static final String PARAMETER_FILTERABLE_MULTIVIEW_FORM_SELECTED = "is_filterable_multiview_form_selected";
    public static final String PARAMETER_COLUMN_TITLE = "column_title";
    public static final String PARAMETER_VIEW_MODIFY_CONTROL = "view_modifyControl";
    public static final String PARAMETRE_VALIDATOR_LISTQUESTION_NAME = "forms_listQuestionValidator";
    public static final String PARAMETER_MULTIVIEW_ORDER = "multiview_column_order";

    // Jsp
    public static final String JSP_MANAGE_STEPS = "jsp/admin/plugins/forms/ManageSteps.jsp";
    public static final String JSP_MANAGE_FORMS = "jsp/admin/plugins/forms/ManageForms.jsp";
    public static final String JSP_MANAGE_QUESTIONS = "jsp/admin/plugins/forms/ManageQuestions.jsp";
    public static final String JSP_MANAGE_TRANSITIONS = "jsp/admin/plugins/forms/ManageTransitions.jsp";
    public static final String JSP_MANAGE_CONTROLS = "jsp/admin/plugins/forms/ManageControls.jsp";

    // Properties
    public static final String PROPERTY_MY_LUTECE_ATTRIBUTES_LIST = "entrytype.myluteceuserattribute.attributes.list";
    public static final String CONSTANT_MYLUTECE_ATTRIBUTE_I18N_PREFIX = "forms.entrytype.myluteceuserattribute.attribute.";
    public static final String PROPERTY_EXPORT_FORM_DATE_CREATION_FORMAT = "forms.export.formResponse.form.date.creation.format";
    public static final String PROPERTY_INACTIVE_BYPASS_DURATION_MILLISECONDS = "forms.inactive.bypass.duration.milliseconds";

    // Constants
    public static final int DEFAULT_FILTER_VALUE = NumberUtils.INTEGER_MINUS_ONE;
    public static final String REFERENCE_ITEM_DEFAULT_CODE = "-1";
    public static final String REFERENCE_ITEM_DEFAULT_NAME = "-";
    public static final int DEFAULT_ID_VALUE = NumberUtils.INTEGER_MINUS_ONE;
    public static final int ORDER_NOT_SET = Integer
            .parseInt( DatastoreService.getDataValue( "forms.formResponseStep.orderNotSet", NumberUtils.INTEGER_MINUS_ONE.toString( ) ) );
    public static final int FILE_TYPE_RIB = 1;

    // Markers
    public static final String MARK_AVAILABLE_STEPS = "availableSteps";
    public static final String MARK_TRANSITION_CONTROL_LIST = "listControls";
    public static final String MARK_AVAILABLE_VALIDATORS = "availableValidators";
    public static final String MARK_QUESTION_LIST = "listQuestion";

    // Beans
    public static final String BEAN_TRANSACTION_MANAGER = "forms.transactionManager";
    public static final String BEAN_FORMS_COLUMN_TITLE = "forms.forms.column";
    public static final String BEAN_FORMS_COLUMN_DATE_CREATION = "forms.formResponseCreationDate.column";

    // Datastore keys
    public static final String DS_KEY_FORM_TITLE_COLUMN = "forms.display.form.columnTitle";
    public static final String DS_KEY_FORM_ASSIGNEE_COLUMN = "forms.display.form.columnAssignee";
    public static final String DS_KEY_FORM_CSV_SEPARATOR = "forms.display.form.csv.separator";

    // Actions forms
    public static final String ACTION_FORMS_MANAGE_MULTIVIEW_CONFIG = "multiviewConfig";
    public static final String ACTION_FORMS_EXPORT_RESPONSES = "multiviewExport";
    public static final String VAL_REMOVE_QUESTION = "removeQuestion";
    public static final String VALIDATE_STEP = "validateStep";
    public static final String VALIDATE_VALIDATOR = "validateValidator";

    // Other
    public static final String FORM_DEFAULT_END_MESSAGE = "forms.message.form.submitted";
    public static final String SEPARATOR_UNDERSCORE = "_";
    public static final String SEPARATOR_SEMICOLON = ";";
    public static final String END_OF_LINE = "\n";

    /**
     * Default private constructor. Do not call
     */
    private FormsConstants( )
    {

        throw new AssertionError( );

    }
}

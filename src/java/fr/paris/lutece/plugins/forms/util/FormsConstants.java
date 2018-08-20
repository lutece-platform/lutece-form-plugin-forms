/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
    public static final String MARK_LIST_CONTROLS = "list_controls";
    public static final String MARK_LIST_VALIDATORS = "list_validators";
    public static final String MARK_LIST_AVAILABLE_POSITIONS = "list_positions";
    public static final String MARK_DISPLAY = "display";
    public static final String MARK_DISPLAY_TITLE = "display_title";
    public static final String MARK_DISPLAY_ORDER = "displayOrder";
    public static final String MARK_TRANSITION = "transition";
    public static final String MARK_CONTROL = "control";
    public static final String MARK_FORM_TOP_BREADCRUMB = "formTopBreadcrumb";
    public static final String MARK_FORM_BOTTOM_BREADCRUMB = "formBottomBreadcrumb";
    public static final String MARK_QUESTION_CONTENT = "questionContent";
    public static final String MARK_INFO = "messageInfo";
    public static final String MARK_CONTROL_TEMPLATE = "control_template";
    public static final String MARK_CONDITION_TITLE = "modify_condition_title";

    // Parameters
    public static final String PARAMETER_PAGE = "page";
    public static final String PARAMETER_ID_FORM = "id_form";
    public static final String PARAMETER_ID_STEP = "id_step";
    public static final String PARAMETER_ID_ENTRY = "id_entry";
    public static final String PARAMETER_ID_FIELD = "id_field";
    public static final String PARAMETER_ID_RESPONSE = "id_response";

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
    public static final String PARAMETER_ID_DISPLAY = "id_display";
    public static final String PARAMETER_ID_PARENT = "id_parent";
    public static final String PARAMETER_STEP_VALIDATED = "stepValidated";
    public static final String PARAMETER_GROUP_VALIDATED = "groupValidated";
    public static final String PARAMETER_DISPLAY_ORDER = "displayOrder";
    public static final String PARAMETER_ID_TRANSITION = "id_transition";
    public static final String PARAMETER_ID_CONTROL = "id_control";
    public static final String PARAMETER_VALIDATOR_NAME = "validatorName";
    public static final String PARAMETER_CONTROL_VALUE = "control_value";
    public static final String PARAMETER_REF_LIST_VALUE = "refListValue";
    public static final String PARAMETER_INDEX_STEP = "index_step";
    public static final String PARAMETER_INFO_KEY = "info_key";
    public static final String PARAMETER_BACK_URL = "back_url";

    public static final String PARAMETER_SELECTED_PANEL = "selected_panel";
    public static final String PARAMETER_CURRENT_SELECTED_PANEL = "current_selected_panel";
    public static final String PARAMETER_WORKFLOW_ACTION_REDIRECTION = "workflow_action_redirection";
    public static final String PARAMETER_URL_FILTER_PREFIX = "filter_";
    public static final String PARAMETER_SORT_COLUMN_POSITION = "column_position";
    public static final String PARAMETER_SORT_ATTRIBUTE_NAME = "sorted_attribute_name";
    public static final String PARAMETER_SORT_ASC_VALUE = "asc_sort";

    // Jsp
    public static final String JSP_MANAGE_STEPS = "jsp/admin/plugins/forms/ManageSteps.jsp";
    public static final String JSP_MANAGE_FORMS = "jsp/admin/plugins/forms/ManageForms.jsp";
    public static final String JSP_MANAGE_QUESTIONS = "jsp/admin/plugins/forms/ManageQuestions.jsp";
    public static final String JSP_MANAGE_TRANSITIONS = "jsp/admin/plugins/forms/ManageTransitions.jsp";
    public static final String JSP_MANAGE_CONTROLS = "jsp/admin/plugins/forms/ManageControls.jsp";
    public static final String JSP_FO_DISPLAY_FORM = "jsp/site/Portal.jsp";

    // Properties
    public static final String PROPERTY_MY_LUTECE_ATTRIBUTES_LIST = "entrytype.myluteceuserattribute.attributes.list";
    public static final String CONSTANT_MYLUTECE_ATTRIBUTE_I18N_PREFIX = "forms.entrytype.myluteceuserattribute.attribute.";

    // Constants
    public static final int DEFAULT_FILTER_VALUE = NumberUtils.INTEGER_MINUS_ONE;
    public static final String REFERENCE_ITEM_DEFAULT_CODE = "-1";
    public static final String REFERENCE_ITEM_DEFAULT_NAME = "-";
    public static final int DEFAULT_ID_VALUE = NumberUtils.INTEGER_MINUS_ONE;
    public static final int ID_NOT_SET = -1;

    // Markers
    public static final String MARK_AVAILABLE_STEPS = "availableSteps";
    public static final String MARK_TRANSITION_CONTROL_LIST = "listControls";
    public static final String MARK_AVAILABLE_VALIDATORS = "availableValidators";
    public static final String MARK_QUESTION_LIST = "listQuestion";

    // Javascript variables
    public static final String JS_PARAMETER_INPUT_VALUE = "inputValue";
    public static final String JS_PARAMETER_CONTROL_VALUE = "controlValue";
    public static final String MARKER_JS_PARAMETER_INPUT_VALUE = "inputValue";
    public static final String MARKER_JS_PARAMETER_CONTROL_VALUE = "controlValue";

    // Beans
    public static final String BEAN_TRANSACTION_MANAGER = "forms.transactionManager";

    // Other
    public static final String FORM_DEFAULT_END_MESSAGE = "forms.message.form.submitted";

    /**
     * Default private constructor. Do not call
     */
    private FormsConstants( )
    {

        throw new AssertionError( );

    }
}

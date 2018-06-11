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
    public static final String MARK_FORM = "form";
    public static final String MARK_ID_FORM = "id_form";
    public static final String MARK_STEP = "step";
    public static final String MARK_ID_STEP = "id_step";
    public static final String MARK_COMPOSITE_LIST = "composite_list";
    public static final String MARK_ENTRY_TYPE_REF_LIST = "entry_type_list";
    public static final String MARK_ENTRY = "entry";
    public static final String MARK_ID_PARENT = "id_parent";
    public static final String MARK_GROUP = "group";

    // Parameters
    public static final String PARAMETER_ID_FORM = "id_form";
    public static final String PARAMETER_ID_STEP = "id_step";
    public static final String PARAMETER_ID_ENTRY = "id_entry";
    public static final String PARAMETER_ID_FIELD = "id_field";

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

    public static final String PARAMETER_SELECTED_PANEL = "selected_panel";
    public static final String PARAMETER_CURRENT_SELECTED_PANEL = "current_selected_panel";

    // Jsp
    public static final String JSP_MANAGE_STEPS = "jsp/admin/plugins/forms/ManageSteps.jsp";
    public static final String JSP_MANAGE_FORMS = "jsp/admin/plugins/forms/ManageForms.jsp";
    public static final String JSP_MANAGE_QUESTIONS = "jsp/admin/plugins/forms/ManageQuestions.jsp";

    // Properties
    public static final String PROPERTY_MY_LUTECE_ATTRIBUTES_LIST = "entrytype.myluteceuserattribute.attributes.list";
    public static final String CONSTANT_MYLUTECE_ATTRIBUTE_I18N_PREFIX = "forms.entrytype.myluteceuserattribute.attribute.";

    // Constants
    public static final int DEFAULT_FILTER_VALUE = NumberUtils.INTEGER_MINUS_ONE;
    public static final String REFERENCE_ITEM_DEFAULT_CODE = "-1";
    public static final String REFERENCE_ITEM_DEFAULT_NAME = "-";

    /**
     * Default private constructor. Do not call
     */
    private FormsConstants( )
    {

        throw new AssertionError( );

    }
}

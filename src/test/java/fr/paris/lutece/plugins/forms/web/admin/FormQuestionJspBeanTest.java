/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.forms.web.admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;

/**
 * Questions of a step of a form in the back office
 */
public class FormQuestionJspBeanTest extends LuteceTestCase
{
    private Form _form;
    private Step _step;
    private FormDisplay _formDisplay;

    @BeforeEach
    protected void createQuestion( )
    {
        _form = new Form( );
        _form.setTitle( "FormQuestionJspBeanTest" );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( _form );

        _step = new Step( );
        _step.setTitle( "Step" );
        _step.setDescription( "" );
        _step.setIdForm( _form.getId( ) );
        _step.setInitial( true );
        StepHome.create( _step );

        _formDisplay = new FormDisplay( );
        _formDisplay.setFormId( _form.getId( ) );
        _formDisplay.setStepId( _step.getId( ) );
        _formDisplay.setCompositeType( CompositeDisplayType.QUESTION.getLabel( ) );
        FormDisplayHome.create( _formDisplay );
    }

    @AfterEach
    protected void removeQuestion( )
    {
        FormDisplayHome.remove( _formDisplay.getId( ) );
        StepHome.remove( _step.getId( ) );
        FormHome.remove( _form.getId( ) );
    }

    /**
     * A user who cannot modify the form is refused the removal of a question: the button of the message opens the questions of the step, even in a new
     * tab
     */
    @Test
    public void testConfirmRemoveCompositeRefusedReturnsToTheQuestionsOfTheStep( ) throws ReflectiveOperationException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id_step", String.valueOf( _step.getId( ) ) );
        request.addParameter( "id_display", String.valueOf( _formDisplay.getId( ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), ModifyEntryJspBean.RIGHT_MANAGE_FORMS );

        // The view is called directly: processController needs a Bean Validation provider that the test classpath does not have. The response it
        // would set is the one the redirection to the message writes to.
        FormQuestionJspBean bean = new FormQuestionJspBean( );
        java.lang.reflect.Field fieldResponse = MVCAdminJspBean.class.getDeclaredField( "_response" );
        fieldResponse.setAccessible( true );
        fieldResponse.set( bean, new MockHttpServletResponse( ) );

        bean.getConfirmRemoveComposite( request );

        String strBackUrl = AdminMessageService.getMessage( request ).getBackUrl( );
        assertTrue( strBackUrl.endsWith( "jsp/admin/plugins/forms/ManageQuestions.jsp?view=manageQuestions&id_step=" + _step.getId( ) ), strBackUrl );
    }
}

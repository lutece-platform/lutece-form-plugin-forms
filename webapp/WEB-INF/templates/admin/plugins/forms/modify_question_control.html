
<@formBreadCrumb>
    <li><a href="jsp/admin/plugins/forms/ManageSteps.jsp?view=manageSteps&id_form=${step.idForm}" title="Retour Formulaire">...</i></a></li>
    <li><a href="jsp/admin/plugins/forms/ManageQuestions.jsp?view=manageQuestions&id_step=${step.id}" title="Retour Etape">${step.title}</a></li>
    <li class="active">#i18n{forms.modify_question_control.title}</li></ul>
</@formBreadCrumb>
<@box>
    <@boxHeader title='#i18n{forms.modify_question_control.title}' />
    <@boxBody>
        <@tform class="form-horizontal" method="post" id="modify_control" name="modify_control" action="jsp/admin/plugins/forms/ManageControls.jsp">
         <@messages errors=errors />
             <@formGroup labelKey='#i18n{forms.modify_control.labelValidator}' helpKey='#i18n{forms.modify_control.labelValidator.help}' mandatory=true>
                <@inputGroup>
                     <@select name='validatorName' items=availableValidators default_value='${control.validatorName!}' />
                     <@inputGroupItem>
	                     <@button type='submit' name="view_modifyControl" value="validateValidator" showTitle=false buttonIcon='check'/>
	                 </@inputGroupItem>
                 </@inputGroup>
            </@formGroup>

             ${control_template}
             
            <@formGroup labelKey='#i18n{forms.modify_control.labelErrorMessage}' helpKey='#i18n{forms.modify_control.labelErrorMessage.help}' >
                <@input type='text' name='errorMessage' value='${control.errorMessage!}' />
            </@formGroup>

            <@formsButton okAction='modifyControl' viewAction=''>
                <#if control.id gt 0>
                    <@aButton href='jsp/admin/plugins/forms/ManageControls.jsp?view=confirmRemoveControl&id_control=${control.id}' title='#i18n{forms.manage_controls.action.removeControl}' buttonIcon='remove text-danger' class='pull-right' color='btn-link' />
                </#if>
            </@formsButton>
        </@tform>
    </@boxBody>
</@box>

<script>
$( function(){
    var formAction = $('#modify_control'), btnAction = $('#action_modifyControl');
    $('body').css( 'overflow', 'hidden' );
    $('.content-header').hide();
    $('#admin-wrapper').css( 'margin', '0' ).css( 'overflow', 'hidden' );
    $('header').remove();
    $('#footer').remove();
    $('.box-header').remove();
    $('#breadforms').remove();

    btnAction.click( function(e){ 
        formAction.submit( function(){
            e.preventDefault();
            formAction.attr('target','_top');
            formAction.submit();
        });
    });
});
</script>
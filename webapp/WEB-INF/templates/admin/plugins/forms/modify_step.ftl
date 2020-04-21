
<#include "/admin/plugins/forms/edit_step_tabs.ftl" />
<@box color='primary'>
    <@boxHeader title='#i18n{forms.manageForm.pageTitle}' />
    <@boxBody>
        <@tabs>
            <@formStepTabs tab="general" />
        </@tabs>
        <@tabContent>
            <@tform class="form-horizontal" method="post" name="modify_step" action="jsp/admin/plugins/forms/ManageSteps.jsp">
                <@messages errors=errors />
                <input type="hidden" id="id_form" name="id_form" value="${step.idForm}">
                <input type="hidden" id="id_step" name="id_step" value="${step.id}">
                <@formGroup labelKey='#i18n{forms.modify_step.labelTitle}' helpKey='#i18n{forms.modify_step.labelTitle.help}' mandatory=true>
                    <@input type='text' name='title' value='${step.title!}' />
                </@formGroup>
                <@formGroup labelKey='#i18n{forms.modify_step.labelDescription}' helpKey='#i18n{forms.modify_step.labelDescription.help}' >
                    <@input type='textarea' name='description'>${step.description!}</@input>
                </@formGroup>
                <@formGroup labelKey='#i18n{forms.modify_step.labelInitial}' helpKey='#i18n{forms.modify_step.labelInitial.help}' >
                    <@checkBox labelFor='initial' name='initial' id='initial' value='1' checked=(step?has_content && step.initial) />
                </@formGroup>
                <@formGroup labelKey='#i18n{forms.modify_step.labelFinal}' helpKey='#i18n{forms.modify_step.labelFinal.help}' >
                    <@checkBox labelFor='final' name='final' id='final' value='1' checked=(step?has_content && step.final) />
                </@formGroup>
                <@formsButton okAction='modifyStep' viewAction='manageSteps' />
            </@tform>
        </@tabContent>
    </@boxBody>
</@box>

<script>
$('#pubModal .content-header').hide();
$('#pubModal #admin-wrapper').css( 'margin', '0' );
$('#pubModal header').hide();
$('#pubModal footer').hide();
</script>
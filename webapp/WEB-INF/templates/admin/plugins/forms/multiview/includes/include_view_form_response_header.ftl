<div id="form-detail-header" class="box box-primary clearfix">
<div class="box-header">
	<h3 class="box-title">
		${form.title} <i class="fa fa-hashtag" title="#i18n{forms.multiviewForms.responseDetails.response.number}"></i> <strong title="#i18n{forms.multiviewForms.responseDetails.response.number}">${form_response.id}</strong>
	</h3>	
    <div class="box-tools">
		<form id="form-response-detail-header" class="form-inline" action="jsp/admin/plugins/forms/MultiviewForms.jsp" method="get">
			<input name="page" value="form" type="hidden" >
			<input type="hidden" name="session" value="session">
			<#assign editModeValue = "ReadOnly">
			<#assign viewNumberAttValue = "1">
			<#if list_filter_values?has_content>
				<#list list_filter_values as filter_values>
					<input type="hidden" name="${filter_values.code}" value="${filter_values.name}">		
				</#list>
			</#if>
			<button class="btn btn-default btn-sm pull-right" type="submit" >
				<i class="fa fa-arrow-left"></i> #i18n{portal.util.labelBack}
			</button>
		</form>
	</div> 
</div>
<div class="box-body">
<!-- Closed in site-forms\WEB-INF\templates\admin\plugins\forms\multiview\includes\include_view_form_response_header.ftl-->
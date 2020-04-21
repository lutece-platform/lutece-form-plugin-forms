<@toastr infos=infos warnings=warnings errors=errors />
<input type="hidden" name="current_selected_panel" value="${current_selected_panel!''}">
<div class="filter-forms">
	<#if form_filter_list?has_content>
		<#list form_filter_list as form_filter>
			${form_filter.template!''}
		</#list>
	</#if>
	<div class="form-group pull-right">
		<#if multiviewExportAction?? >
			<label for="format_export" >
				<#assign param><#if !table_template?has_content>disabled<#else></#if> </#assign>
				<@inputGroup>
					<@select name='format_export' id='format_export' default_value='' items=format_export_list title='#i18n{forms.adminFeature.multiviewForms.export.buttonName}' params=param />
					<@inputGroupItem>
						<@button color='btn-info' type='submit' id='action_doExportResponses' name='action_doExportResponses' title='#i18n{forms.adminFeature.multiviewForms.export.buttonName}' disabled=!table_template?has_content  showTitle=false buttonIcon='download' />
					</@inputGroupItem>
				</@inputGroup>
			</label>
		</#if>
		<#if multiviewConfigAction?? >
                    <label for="config_multiview">
			<@inputGroup>
				<@inputGroupItem>
                                     <@aButton name='btn_multiview_config' id='btn_multiview_config' href='${multiviewConfigAction.url!}' params='' title='${multiviewConfigAction.name}' buttonIcon='${multiviewConfigAction.iconUrl}' />
				</@inputGroupItem>
			</@inputGroup>
                    </label>
                </#if>
	</div>
</div>
<div class="row">
	<@tabList>
		<#list form_panel_list as form_panel>
			<#if form_panel.template??>
					${form_panel.template}
			</#if>
		</#list>
	</@tabList>
</div>
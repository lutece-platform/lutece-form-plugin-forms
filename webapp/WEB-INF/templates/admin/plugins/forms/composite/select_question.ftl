<#if question.visible>
<div class="row <#if id_display??>display_field_${id_display}</#if>" displayControl="${question.id}_${question.iterationNumber}">
   	<@formGroup>
		<@checkBox labelKey='${question.title}' name='ids_entry' value='${question.idEntry}' />
	</@formGroup>
</div>
</#if>
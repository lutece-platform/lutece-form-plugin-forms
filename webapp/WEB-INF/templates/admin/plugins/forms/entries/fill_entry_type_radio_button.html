

<#assign hide="">
<#list entry.fields as field>
  <#if field.conditionalQuestions?exists&&field.conditionalQuestions?size!=0>
    <#assign idName = 'div'>
    <#if entry_iteration_number?has_content && entry_iteration_number gt 0>
      <#assign idName = 'div_nIt' + entry_iteration_number + '_'>
    </#if>
    <#assign idConditional = idName + field.idField>
    <#assign hide=hide+"hideId(${idConditional}.id);">
  </#if>
</#list>


<div class="form-group ${entry.CSSClass!}">
  <label class="control-label col-xs-12 col-sm-12 col-md-3 col-lg-3" for="form${entry.idEntry}" id="form${entry.idEntry}">${entry.title}<#if entry.mandatory> *</#if></label>
  <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
  <#assign inLine = entry.fieldInLine>
  <#list entry.fields as field>
  	<#if field.code == 'answer_choice'>
	    <#assign idName = buildEntryName( entry, entry_iteration_number )>
	    <#if entry.fieldInLine != true><div class="radio"></#if>
	      <label <#if entry.fieldInLine>class="radio-inline"</#if> for="${idName}_${field.idField}" <#if field.comment?? && field.comment != ''>title="${field.comment}"</#if> > 
	        <input type="radio" id="${idName}_${field.idField}" name="${idName}" value="${field.idField}"
	        <#if list_responses?? && list_responses?has_content>
	          <#list list_responses as response>
	            <#if response.entry.idEntry == entry.idEntry && response.field??>
	              <#if response.field.idField == field.idField>checked="checked"<#break></#if>
	            </#if>
	          </#list>
	        <#else>
	          <#if field.defaultValue>checked="checked"</#if>
	        </#if>
	          onclick="${hide} <#if field.conditionalQuestions?exists&&field.conditionalQuestions?size!=0>
	          <#assign idName = 'div'>
	          <#if entry_iteration_number?has_content && entry_iteration_number gt 0>
	            <#assign idName = 'div_nIt' + entry_iteration_number + '_'>
	          </#if>
	          <#assign idConditional = idName + field.idField>
	          displayId(${idConditional}.id);</#if>"
	        />
	     <#if !field.noDisplayTitle>
	       ${field.title}
	     </#if>
	      </label>
	      <#if entry.fieldInLine != true></div></#if>
	      
	      <#if field.conditionalQuestions?exists&&field.conditionalQuestions?size!=0 && !inLine>
	        <#if list_entry_children??>
	          <#list list_entry_children as children>
	            <#if children.idField == field.idField && children.conditionalEntries??>${children.conditionalEntries}</#if>
	          </#list>
	        </#if>
	      </#if>
      </#if>
    </#list>
    <#if entry.fieldInLine&&entry.helpMessage?exists&&entry.helpMessage!=''>
    <span class="help-block">${entry.helpMessage}</span>
    </#if>
  </div>
</div>

<#if list_responses??>
  <#list list_responses as response>
    <#if response.entry.idEntry == entry.idEntry && response.entry.error?? && response.entry.error.isDisplayableError>
      <div class="alert alert-danger">
      <#assign error = response.entry.error>
        <#if error.mandatoryError>#i18n{forms.message.mandatory.entry}<#else>${error.errorMessage}</#if>
      </div>
    </#if>
  </#list>
</#if>

<#if inLine>
  <#if list_entry_children??>
    <#list list_entry_children as children>
      <#if children.conditionalEntries??>${children.conditionalEntries}</#if>
    </#list>
  </#if>
</#if>

<#if list_responses??>
  <script type="text/javascript">
    <#list list_responses as response>
      var idName = '';
      <#if entry_iteration_number?has_content && entry_iteration_number gt 0>
        idName = '_nIt' + ${entry_iteration_number} + '_';
      </#if>
      <#if response.entry.idEntry == entry.idEntry && response.field??>
        var baliseId = "div" + idName + ${response.field.idField};
        if( document.getElementById(baliseId) != null)
        {
          document.getElementById(baliseId).style.visibility='visible';
          document.getElementById(baliseId).style.display='block';
        }
      </#if>
    </#list>
  </script>
<#else>
  <#list entry.fields as field>
    <#if field.defaultValue && field.conditionalQuestions?exists && field.conditionalQuestions?size != 0>
      <script type="text/javascript">
        var idName = '';
        <#if entry_iteration_number?has_content && entry_iteration_number gt 0>
          idName = '_nIt' + ${entry_iteration_number} + '_';
        </#if>
        var baliseId = "div" + idName + ${field.idField};

        if(document.getElementById && document.getElementById(baliseId) != null)
        {
          document.getElementById(baliseId).style.visibility='visible';
          document.getElementById(baliseId).style.display='block';
        }
      </script>
    </#if>
  </#list>
</#if>

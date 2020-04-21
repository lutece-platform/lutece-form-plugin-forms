
<#include "/skin/plugins/asynchronousupload/upload_commons.html" />

<#if list_responses?? && list_responses?has_content>
  <#list list_responses as response>
    <#if response.file?? && response.file.title?? && response.file.title != ''>
      <#if listFiles??>
        <#assign listFiles = listFiles + [response.file] >
      <#else>
        <#assign listFiles = [response.file] >
      </#if>
    </#if>
  </#list>
</#if>

<#if !listFiles??>
  <#assign listFiles = ''>
</#if>

<div class="form-group">
  <#assign idName = buildEntryName( entry, entry_iteration_number )>
  <label class="control-label col-xs-12 col-sm-12 col-md-3 col-lg-3" for="${idName}" id="label${entry.idEntry}">${entry.title}<#if entry.mandatory>  *</#if></label>
  <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
    <#assign fieldName=idName >
    <#assign cssClass=entry.CSSClass!'' >
    <#if list_responses??>
      <#list list_responses as response>
        <#if response.entry.idEntry == entry.idEntry && response.entry.error?? && response.entry.error.isDisplayableError>
          <#assign cssClass='error' >
          <#break>
        </#if>
      </#list>
    </#if>
    <@addFileInput fieldName=fieldName handler=uploadHandler cssClass=cssClass multiple=true />

    <#if entry.helpMessage?exists&&entry.helpMessage!=''>
      <span class="help-block">${entry.helpMessage}</span>
    </#if>

    <div id="listUpload">
      <@addUploadedFilesBox fieldName=fieldName handler=uploadHandler listFiles=listFiles />
    </div>

    <#if list_responses?? && list_responses?has_content>
      <#assign response_error = list_responses[0]>
      <#if response_error.entry.idEntry == entry.idEntry && response_error.entry.error?? && response_error.entry.error.isDisplayableError>
        <div class="alert alert-danger">
          <#assign error = response_error.entry.error>
          <#if error.mandatoryError>
            #i18n{forms.message.mandatory.entry}
          <#else>
            ${error.errorMessage}
          </#if>
        </div>
      </#if>
    </#if>
  </div>
</div>

<script>
  /*HACK for firefox*/
  $("#listUpload  button[type='submit']").attr("onclick","return false;");
</script>
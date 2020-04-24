

<#assign editModeValue = "">
<#assign viewNumberValue = "1">
<#if entry.fields??>
        <#assign addressField = getFieldValueByCode( entry, "address" )>
        <#assign viewNumberValue = getFieldValueByCode( entry, "viewNumber" )>
        <#assign editModeValue = getFieldValueByCode( entry, "editMode" )>
</#if>

<#assign addressValue = "" />
<#assign idAddressValue = "" />
<#assign xValue = "" />
<#assign yValue = "" />
<#assign geometryValue = "" />

<div class="form-group">
    <label class="control-label col-xs-12 col-sm-12 col-md-3 col-lg-3" for="form${entry.idEntry}_address" id="attribute${entry.idEntry}">${entry.title}<#if entry.mandatory>    *</#if></label>
    <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
    <#if list_responses?? && list_responses?has_content>
        <#assign addressValue = getResponseContainingTheFieldWithCode(list_responses, "address").toStringValueResponse />
        <#assign idAddressValue = getResponseContainingTheFieldWithCode(list_responses, "idAddress").toStringValueResponse />
        <#assign xValue = getResponseContainingTheFieldWithCode(list_responses, "X").toStringValueResponse />
        <#assign yValue = getResponseContainingTheFieldWithCode(list_responses, "Y").toStringValueResponse />
        <#assign geometryValue = getResponseContainingTheFieldWithCode(list_responses, "geometry").toStringValueResponse />
        <#if getError( list_responses, entry )?? >
          <#assign error = getError( list_responses, entry ) >
        </#if> 
    </#if>
    <#if editModeValue == "" || editModeValue == "Address" || editModeValue == "Adresse" >
        <input type="text" class="form-control input-sm" name="${entry.idEntry}_address" id="${entry.idEntry}_address" <#if error??>class="error"<#else>class="${entry.CSSClass!}"</#if> value="${addressValue!}">
    <#else>
        <input type="hidden" class="form-control input-sm" name="${entry.idEntry}_address" id="${entry.idEntry}_address" value="${addressValue!}">
    </#if>
    
    <input type="hidden" name="${entry.idEntry}_idAddress" id="${entry.idEntry}_idAddress" value="${idAddressValue!}">
    <input type="hidden" name="${entry.idEntry}_x" id="${entry.idEntry}_x" value="${xValue!}">
    <input type="hidden" name="${entry.idEntry}_y" id="${entry.idEntry}_y" value="${yValue!}">
    <input type="hidden" name="${entry.idEntry}_geometry" id="${entry.idEntry}_geometry" value="${geometryValue!}">

    <#if entry.helpMessage?exists&&entry.helpMessage!=''>
        <span class="help-block">${entry.helpMessage}</span>
    </#if>
    
    <#if error?? && error.isDisplayableError>
        <div class="alert alert-danger">
            <#if error.mandatoryError>
                #i18n{forms.message.mandatory.entry}
            <#else>
                ${error.errorMessage}
            </#if>
        </div>
    </#if>

    <#if entry.mapProvider?has_content>
        <#if viewNumberValue?has_content && entry.mapProvider.getParameter(viewNumberValue?number)?? && entry.mapProvider.getParameter(viewNumberValue?number).mapParameter??>
            <#assign map_parameter = entry.mapProvider.getParameter(viewNumberValue?number).mapParameter >
        </#if>
        
        <#if viewNumberValue?has_content && entry.mapProvider.getParameter(viewNumberValue?number)?? && entry.mapProvider.getParameter(viewNumberValue?number).addressParam?? >
            <#assign add_parameter = entry.mapProvider.getParameter(viewNumberValue?number).addressParam >
        </#if>
        <#include entry.mapProvider.htmlCode />
    </#if>
    </div>
</div>
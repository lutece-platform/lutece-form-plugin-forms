<#if searched_text??>
    <#assign previous_searched_text = searched_text>
<#else>
    <#assign previous_searched_text = ''>
</#if>
<@formGroup formStyle='inline' id='search-text' labelKey='#i18n{forms.multiviewForms.labelFilter.searchText}' labelFor='searched_text' >
    <@inputGroup>
        <@input name='searched_text' id='searched_text' value=previous_searched_text placeHolder='#i18n{forms.multiviewForms.labelFilter.searchText}' />
        <@inputGroupItem>
            <@button color='btn-info' type='submit' id='btn_search' buttonIcon='search' />
        </@inputGroupItem>    
    </@inputGroup>
</@formGroup>

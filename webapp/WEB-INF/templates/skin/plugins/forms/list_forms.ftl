<h2>#i18n{form.xpage.listForms.pagetitle}</h2> 

	<#list form_list as form>
		<#if form.active>
			 <ul>
				<li><a href="jsp/site/Portal.jsp?page=forms&view=stepView&id_form=${form.id}&init=true" target="_blank">${form.title}</a> </li>
			</ul>
		</#if>
	</#list>
  
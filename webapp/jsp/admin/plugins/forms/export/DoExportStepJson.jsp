<%@ page errorPage="../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.forms.web.admin.FormJspBean"%>

${ formStepJspBean.init( pageContext.request, FormJspBean.RIGHT_FORMS_MANAGEMENT ) }
${ formStepJspBean.doExportJson( pageContext.request, pageContext.response ) }
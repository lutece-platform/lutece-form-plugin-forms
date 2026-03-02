<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.forms.web.admin.FormJspBean"%>

${ formJspBean.init( pageContext.request, FormJspBean.RIGHT_FORMS_MANAGEMENT ) }
${ formJspBean.doExportJson( pageContext.request, pageContext.response ) }
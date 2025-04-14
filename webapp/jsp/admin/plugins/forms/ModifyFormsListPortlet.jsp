<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.forms.web.portlet.FormsListPortletJspBean"%>

${ formsListPortletJspBean.init( pageContext.request, FormsListPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ formsListPortletJspBean.getModify( pageContext.request ) }

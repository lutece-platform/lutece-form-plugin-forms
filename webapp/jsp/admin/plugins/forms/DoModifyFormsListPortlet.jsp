<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.forms.web.portlet.FormsListPortletJspBean"%>

${ formsListPortletJspBean.init( pageContext.request, FormsListPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( formsListPortletJspBean.updatePortletForm( pageContext.request ) ) }
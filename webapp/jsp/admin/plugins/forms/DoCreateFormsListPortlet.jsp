<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="formsListPortlet" scope="session" class="fr.paris.lutece.plugins.forms.web.portlet.FormsListPortletJspBean" />

<%
    formsListPortlet.init( request, formsListPortlet.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect( formsListPortlet.doCreate( request ) );
%>
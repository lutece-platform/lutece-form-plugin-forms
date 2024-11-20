<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="FormsListPortlet" scope="session" class="fr.paris.lutece.plugins.forms.web.portlet.FormsListPortletJspBean" />


<% FormsListPortlet.init( request, FormsListPortlet.RIGHT_MANAGE_ADMIN_SITE ); %>
<%= FormsListPortlet.getModify( request ) %>

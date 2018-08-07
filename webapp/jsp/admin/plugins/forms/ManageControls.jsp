<jsp:useBean id="manageformsControl" scope="session" class="fr.paris.lutece.plugins.forms.web.admin.FormControlJspBean" />
<% String strContent = manageformsControl.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

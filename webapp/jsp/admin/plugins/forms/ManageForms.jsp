<jsp:useBean id="manageformsForm" scope="session" class="fr.paris.lutece.plugins.forms.web.admin.FormJspBean" />
<% String strContent = manageformsForm.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

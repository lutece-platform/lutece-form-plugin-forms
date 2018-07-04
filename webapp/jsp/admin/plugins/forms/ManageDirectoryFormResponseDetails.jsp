<jsp:useBean id="manageFormResponseDetails" scope="session" class="fr.paris.lutece.plugins.forms.web.admin.MultiviewFormResponseDetailsJspBean" />
<% String strContent = manageFormResponseDetails.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
<jsp:useBean id="manageformsFormResponse" scope="session" class="fr.paris.lutece.plugins.forms.web.admin.FormResponseJspBean" />
<% String strContent = manageformsFormResponse.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
<jsp:useBean id="manageformsFormQuestions" scope="session" class="fr.paris.lutece.plugins.forms.web.admin.FormJspBean" />
<% String strContent = manageformsFormQuestions.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
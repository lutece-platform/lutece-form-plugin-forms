<jsp:useBean id="manageformsQuestion" scope="session" class="fr.paris.lutece.plugins.forms.web.admin.FormQuestionJspBean" />
<% String strContent = manageformsQuestion.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

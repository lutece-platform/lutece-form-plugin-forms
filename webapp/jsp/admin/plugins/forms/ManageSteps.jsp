<jsp:useBean id="manageformsStep" scope="session" class="fr.paris.lutece.plugins.forms.web.admin.FormStepJspBean" />
<% String strContent = manageformsStep.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

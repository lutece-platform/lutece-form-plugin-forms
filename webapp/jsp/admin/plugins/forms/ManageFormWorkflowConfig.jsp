<jsp:useBean id="manageWorkflowBean" scope="session" class="fr.paris.lutece.plugins.forms.web.admin.FormWorkflowConfigJspBean" />
<% String strContent = manageWorkflowBean.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

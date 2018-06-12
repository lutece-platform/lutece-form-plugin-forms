<jsp:useBean id="multiviewForms" scope="session" class="fr.paris.lutece.plugins.forms.web.MultiviewFormsJspBean" />
<% String strContent = multiviewForms.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

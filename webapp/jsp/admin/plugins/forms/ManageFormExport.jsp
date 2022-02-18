<jsp:useBean id="manageformExportBean" scope="session" class="fr.paris.lutece.plugins.forms.web.admin.FormExportJspBean" />
<% String strContent = manageformExportBean.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

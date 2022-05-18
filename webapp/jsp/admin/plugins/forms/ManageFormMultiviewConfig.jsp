<jsp:useBean id="manageMultiviewBean" scope="session" class="fr.paris.lutece.plugins.forms.web.admin.FormMultiviewConfigJspBean" />
<% String strContent = manageMultiviewBean.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

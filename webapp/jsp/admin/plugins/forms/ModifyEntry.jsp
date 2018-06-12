<jsp:useBean id="modifyEntry" scope="session" class="fr.paris.lutece.plugins.forms.web.ModifyEntryJspBean" />
<% String strContent = modifyEntry.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
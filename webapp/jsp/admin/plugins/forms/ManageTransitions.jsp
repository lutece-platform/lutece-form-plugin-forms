<jsp:useBean id="manageformsTransition" scope="session" class="fr.paris.lutece.plugins.forms.web.FormTransitionJspBean" />
<% String strContent = manageformsTransition.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

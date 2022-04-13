<jsp:useBean id="manageformsCategories" scope="session" class="fr.paris.lutece.plugins.forms.web.admin.FormCategoriesJspBean" />
<% String strContent = manageformsCategories.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

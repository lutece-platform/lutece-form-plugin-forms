<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="formsIndexing" scope="session" class="fr.paris.lutece.plugins.forms.web.search.ManageFormsSearchIndexationBean" />

<% formsIndexing.init( request , formsIndexing.RIGHT_FORMS_INDEXER ); %>
<%= formsIndexing.getIndexingProperties( request ) %>

<%@ include file="../../AdminFooter.jsp" %>

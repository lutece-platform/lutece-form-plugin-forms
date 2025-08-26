<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.forms.web.search.ManageFormsSearchIndexationBean"%>

${ manageFormsSearchIndexationBean.init( pageContext.request, ManageFormsSearchIndexationBean.RIGHT_FORMS_INDEXER ) }
${ manageFormsSearchIndexationBean.doIndexing( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>

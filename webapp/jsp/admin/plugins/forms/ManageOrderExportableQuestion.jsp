<%@ page import="fr.paris.lutece.plugins.forms.web.admin.ManageOrderExportableQuestion"%><%@ page errorPage="../../ErrorPagePortal.jsp" trimDirectiveWhitespaces="true" %>

    <jsp:useBean id="manageOrderExportableQuestion" scope="request" class="fr.paris.lutece.plugins.forms.web.admin.ManageOrderExportableQuestion" />


    <%
        if("doChangeOrderExportableQuestion".equals(request.getParameter("actionName"))) {
            manageOrderExportableQuestion.doMoveUpExportableQuestion( request, response );
        }
    %>
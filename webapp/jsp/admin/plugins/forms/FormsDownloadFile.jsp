<%@page import="fr.paris.lutece.plugins.forms.web.file.FormsDownloadFile"%>
<% 
	 String strResult =  FormsDownloadFile.doDownloadFile(request,response);
 	 if (!response.isCommitted())
	{
		  response.sendRedirect(strResult);
	}
%>

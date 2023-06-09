package fr.paris.lutece.plugins.forms.web.admin;

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ManageOrderExportableQuestion {

    private static final String PARAMETER_ACTIVE_TAB_PANNEL_2 = "activeTabPannel2";

    @Action( value = "moveUpExportableQuestion")
    public static HttpServletResponse doMoveUpExportableQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
     // print all parameters
        Map<String, String[]> mapParams = request.getParameterMap( );

        for ( Map.Entry<String, String[]> entry : mapParams.entrySet( ) )
        {
            String strParamName = entry.getKey( );
            String[] tabParamValues = entry.getValue( );
            for ( String strParamValue : tabParamValues )
            {
                AppLogService.info( "Parameter : " + strParamName + " = " + strParamValue );
            }
        }

        int nIdForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );
        int nIdQuestion = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_QUESTION ), FormsConstants.DEFAULT_ID_VALUE );
        int nOrderToSet = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_EXPORT_DISPLAY_ORDER + "_" + nIdQuestion), 0 );

        if ( nIdQuestion != FormsConstants.DEFAULT_ID_VALUE )
        {
            Question questionToChangeOrder = QuestionHome.findByPrimaryKey( nIdQuestion );

            int nCurrentOrder = questionToChangeOrder.getExportDisplayOrder();
            if (nCurrentOrder != nOrderToSet)
            {
                if (nOrderToSet > nCurrentOrder)
                {
                    List<Question> questionList = QuestionHome.getListQuestionBetweenExportOrders(nIdForm, nCurrentOrder, nOrderToSet);
                    for (Question question : questionList)
                    {
                        if (question.getExportDisplayOrder() != nCurrentOrder)
                        {
                            question.setExportDisplayOrder(question.getExportDisplayOrder() - 1);
                            QuestionHome.update(question);
                        }
                    }
                }
                else
                {
                    List<Question> questionList = QuestionHome.getListQuestionBetweenExportOrders(nIdForm, nOrderToSet, nCurrentOrder);
                    for (Question question : questionList)
                    {
                        if (question.getExportDisplayOrder() != nCurrentOrder)
                        {
                            question.setExportDisplayOrder(question.getExportDisplayOrder() + 1);
                            QuestionHome.update(question);
                        }
                    }
                }
                questionToChangeOrder.setExportDisplayOrder(nOrderToSet);
                QuestionHome.update(questionToChangeOrder);
            }
        }

        String strUrl = request.getRequestURL( ).toString();
        strUrl = strUrl.substring( 0, strUrl.lastIndexOf( "/" ) );
        strUrl = strUrl+"/"+"ManageFormExport.jsp"+ "?" + "view=manageExport&"+ FormsConstants.PARAMETER_ID_FORM + "=" + 1 + "&" + PARAMETER_ACTIVE_TAB_PANNEL_2 + "=true";
        System.out.println("URL"+strUrl);
        // http://localhost:8080/lutece/jsp/admin/plugins/forms/ManageFormExport.jsp?view=manageExport&id_form=1
        // http://localhost:8080/lutece/jsp/admin/plugins/formsManageFormExport.jsp?view=manageExport&id_form=-1&activeTabPannel2=true

          response.sendRedirect( strUrl);
       return response;
    }

}
